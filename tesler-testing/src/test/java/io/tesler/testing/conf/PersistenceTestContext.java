/*-
 * #%L
 * IO Tesler - Testing
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

package io.tesler.testing.conf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.service.tx.DeploymentTransactionSupport;
import io.tesler.api.system.SystemSettings;
import io.tesler.core.dao.BaseDAO;
import io.tesler.model.core.config.ScopeConfig;
import io.tesler.model.core.listeners.jpa.BaseEntityListener;
import io.tesler.model.core.listeners.jpa.ExtRevisionListener;
import io.tesler.model.core.tx.SpecificDatabaseTransactionalService;
import io.tesler.model.core.tx.TransactionServiceImpl;
import io.tesler.model.core.tx.TransactionStatusImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@Import({
		ScopeConfig.class,
		TransactionServiceImpl.class,
		SpecificDatabaseTransactionalService.class,
		TransactionStatusImpl.class,
		ExtRevisionListener.class,
		BaseEntityListener.class
})
public class PersistenceTestContext {

	@Bean
	public BaseDAO baseDAO() {
		return mock(BaseDAO.class);
	}

	@Bean
	public DictionaryCache dictionaryCache() {
		DictionaryCache cache = mock(DictionaryCache.class);
		DictionaryCache.instance.set(cache);
		return cache;
	}

	@Bean
	public SystemSettings systemSettings() {
		SystemSettings systemSettings = mock(SystemSettings.class);
		SystemSettings.instance.set(systemSettings);
		return systemSettings;
	}

	@Bean("primaryDS")
	public DataSource primaryDS() throws SQLException {
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		when(connection.createStatement()).thenReturn(statement);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		when(dataSource.getConnection(any(), any())).thenReturn(connection);
		return dataSource;
	}

	@Bean(name = DeploymentTransactionSupport.SERVICE_NAME)
	public DeploymentTransactionSupport deploymentTransactionSupport() {
		return mock(DeploymentTransactionSupport.class);
	}

}
