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
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.data.SystemSettingDTO;
import io.tesler.core.dto.data.SystemSettingDTO_;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.crudma.api.SystemSettingService;
import io.tesler.crudma.meta.SystemSettingFieldMetaBuilder;
import io.tesler.model.core.entity.SystemSetting;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingServiceImpl extends
		VersionAwareResponseService<SystemSettingDTO, SystemSetting> implements
		SystemSettingService {

	public SystemSettingServiceImpl() {
		super(SystemSettingDTO.class, SystemSetting.class, null, SystemSettingFieldMetaBuilder.class);
	}

	@Override
	protected Specification<SystemSetting> getParentSpecification(BusinessComponent bc) {
		return (root, cq, cb) -> cb.and();
	}

	@Override
	protected ActionResultDTO<SystemSettingDTO> doUpdateEntity(SystemSetting item, SystemSettingDTO data,
			BusinessComponent bc) {
		if (data.isFieldChanged(SystemSettingDTO_.key)) {
			item.setKey(data.getKey());
		}
		if (data.isFieldChanged(SystemSettingDTO_.value)) {
			item.setValue(data.getValue());
		}
		return new ActionResultDTO<>(entityToDto(bc, item));
	}

	@Override
	protected CreateResult<SystemSettingDTO> doCreateEntity(final SystemSetting entity, final BusinessComponent bc) {
		return new CreateResult<>(entityToDto(bc, baseDAO.findById(SystemSetting.class, baseDAO.save(entity))));
	}

	@Override
	public Actions<SystemSettingDTO> getActions() {
		return Actions.<SystemSettingDTO>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}


}
