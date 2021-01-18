/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.tesler.sqlbc.crudma;


import io.tesler.api.service.AsyncService;
import io.tesler.api.service.tx.DeploymentTransactionSupport;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.core.crudma.bc.RefreshableBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.Bc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Slf4j
@DependsOn(DeploymentTransactionSupport.SERVICE_NAME)
@Service
public final class SqlBcSupplier implements RefreshableBcSupplier {

	private final AsyncService asyncService;

	private final TransactionService txService;

	private final JpaDao jpaDao;

	private final SqlBcCreator sqlBcCreator;

	private Map<String, SqlBcDescription> sqlBcMap;

	public SqlBcSupplier(
			AsyncService asyncService,
			TransactionService txService,
			JpaDao jpaDao,
			SqlBcCreator sqlBcCreator) {
		this.asyncService = asyncService;
		this.txService = txService;
		this.jpaDao = jpaDao;
		this.sqlBcCreator = sqlBcCreator;
		this.sqlBcMap = loadAllBcNames();
		loadData(true);
	}

	@Override
	public BcDescription getBcDescription(String bcName) {
		return sqlBcMap.get(bcName);
	}

	private void loadData(boolean startup) {
		PrivilegeUtil.runPrivileged(() ->
				asyncService.<Void, RuntimeException>invokeAsync(() ->
						txService.<Void, RuntimeException>invokeInTx(
								Invoker.of(() -> doLoadData(startup))
						)
				)
		);
	}

	private void doLoadData(boolean startup) {
		sqlBcMap.forEach((name, desc) -> {
			try {
				desc.getFields();
			} catch (Exception ex) {
				if (startup) {
					log.error(ex.getLocalizedMessage());
				} else {
					log.error(ex.getLocalizedMessage(), ex);
				}
			}
		});
	}

	@Override
	public List<String> getAllBcNames() {
		return new ArrayList<>(sqlBcMap.keySet());
	}

	@Override
	public void refresh() {
		sqlBcMap = loadAllBcNames();
		loadData(false);
	}

	private Map<String, SqlBcDescription> loadAllBcNames() {
		return jpaDao.getList(Bc.class).stream()
				.map(sqlBcCreator::getDescription).collect(
						Collectors.toMap(SqlBcDescription::getName, Function.identity())
				);
	}


}
