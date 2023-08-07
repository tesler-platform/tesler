/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.impl;

import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.data.view.BcDTO;
import io.tesler.core.dto.data.view.BcDTO_;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.crudma.api.BcService;
import io.tesler.crudma.meta.BcFieldMetaBuilder;
import io.tesler.model.ui.entity.Bc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Service
public class BcServiceImpl extends VersionAwareResponseService<BcDTO, Bc> implements BcService {

	@Lazy
	@Autowired
	private BcRegistry bcRegistry;

	@Autowired
	private TransactionService txService;

	public BcServiceImpl() {
		super(BcDTO.class, Bc.class, null, BcFieldMetaBuilder.class);
	}

	@Override
	protected CreateResult<BcDTO> doCreateEntity(final Bc entity, final BusinessComponent<InnerBcDescription> bc) {
		Long id = baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, baseDAO.findById(Bc.class, id)));
	}

	@Override
	protected ActionResultDTO<BcDTO> doUpdateEntity(Bc entity, BcDTO data, BusinessComponent<InnerBcDescription> bc) {
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(BcDTO_.name)) {
				entity.setName(data.getName());
			}
			if (data.isFieldChanged(BcDTO_.parentName)) {
				entity.setParentName(data.getParentName());
			}
			if (data.isFieldChanged(BcDTO_.query)) {
				entity.setQuery(data.getQuery());
			}
			if (data.isFieldChanged(BcDTO_.defaultOrder)) {
				entity.setDefaultOrder(data.getDefaultOrder());
			}
			if (data.isFieldChanged(BcDTO_.reportDateField)) {
				entity.setReportDateField(data.getReportDateField());
			}
			if (data.isFieldChanged(BcDTO_.pageLimit)) {
				entity.setPageLimit(data.getPageLimit());
			}
			if (data.isFieldChanged(BcDTO_.binds)) {
				entity.setBinds(data.getBinds());
			}
		}
		txService.invokeAfterCompletion(Invoker.of(bcRegistry::refresh));
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

}
