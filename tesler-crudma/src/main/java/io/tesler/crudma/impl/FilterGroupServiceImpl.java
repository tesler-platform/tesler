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

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.FilterGroupDTO;
import io.tesler.core.dto.rowmeta.FilterGroupDTO_;
import io.tesler.core.service.action.Actions;
import io.tesler.crudma.api.FilterGroupService;
import io.tesler.crudma.config.CoreServiceAssociation;
import io.tesler.crudma.meta.FilterGroupMetaBuilder;
import io.tesler.model.ui.entity.FilterGroup;
import org.springframework.stereotype.Service;

@Service
public class FilterGroupServiceImpl extends VersionAwareResponseService<FilterGroupDTO, FilterGroup> implements
		FilterGroupService {

	protected FilterGroupServiceImpl() {
		super(FilterGroupDTO.class, FilterGroup.class, null, FilterGroupMetaBuilder.class);
	}

	@Override
	protected ActionResultDTO<FilterGroupDTO> doUpdateEntity(FilterGroup filterGroup, FilterGroupDTO data,
			BusinessComponent<InnerBcDescription> bc) {
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(FilterGroupDTO_.name)) {
				filterGroup.setName(data.getName());
			}
			if (data.isFieldChanged(FilterGroupDTO_.filters)) {
				filterGroup.setFilters(data.getFilters());
			}
			if (data.isFieldChanged(FilterGroupDTO_.bc)) {
				filterGroup.setBc(data.getBc());
			}
		}
		return new ActionResultDTO<>(entityToDto(bc, filterGroup));
	}

	@Override
	protected CreateResult<FilterGroupDTO> doCreateEntity(final FilterGroup entity, final BusinessComponent<InnerBcDescription> bc) {
		entity.setName("Введите имя группы фильтров");
		entity.setFilters("Ведите фильтры");
		entity.setBc("Ведите имя бизнес компонента");
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<FilterGroupDTO, InnerBcDescription> getActions() {
		return Actions.<FilterGroupDTO, InnerBcDescription>builder()
				.create().available(this::isAvailable).add()
				.save().available(this::isAvailable).add()
				.delete().available(this::isAvailable).add()
				.build();
	}

	private boolean isAvailable(BusinessComponent<InnerBcDescription> bc) {
		return CoreServiceAssociation.filterGroup.isBc(bc);
	}

}
