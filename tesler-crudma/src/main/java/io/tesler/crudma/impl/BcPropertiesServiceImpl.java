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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.crudma.api.BcPropertiesService;
import io.tesler.crudma.dto.BcPropertiesDTO;
import io.tesler.crudma.dto.BcPropertiesDTO_;
import io.tesler.crudma.meta.BcPropertiesMetaBuilder;
import io.tesler.model.ui.entity.BcProperties;
import io.tesler.model.ui.entity.BcProperties_;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BcPropertiesServiceImpl extends VersionAwareResponseService<BcPropertiesDTO, BcProperties> implements
		BcPropertiesService {

	protected BcPropertiesServiceImpl() {
		super(BcPropertiesDTO.class, BcProperties.class, null, BcPropertiesMetaBuilder.class);
	}

	@Override
	protected CreateResult<BcPropertiesDTO> doCreateEntity(final BcProperties entity, final BusinessComponent<InnerBcDescription> bc) {
		entity.setBc("Ведите имя бизнес компонента");
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<BcPropertiesDTO> doUpdateEntity(BcProperties bcProperties, BcPropertiesDTO data,
			BusinessComponent<InnerBcDescription> bc) {
		String bcName = data.getBc();
		List<BcProperties> existDefaultBcProperties = baseDAO.getList(BcProperties.class, BcProperties_.bc, bcName);
		if (!existDefaultBcProperties.isEmpty()) {
			throw new BusinessException().addPopup(errorMessage("error.bc_settings_already_exist", bc));
		}
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(BcPropertiesDTO_.bc)) {
				bcProperties.setBc(bcName);
			}
			if (data.isFieldChanged(BcPropertiesDTO_.filter)) {
				bcProperties.setFilter(data.getFilter());
			}
			if (data.isFieldChanged(BcPropertiesDTO_.sort)) {
				bcProperties.setSort(data.getSort());
			}
			if (data.isFieldChanged(BcPropertiesDTO_.limit)) {
				bcProperties.setLimit(data.getLimit());
			}
			if (data.isFieldChanged(BcPropertiesDTO_.reportPeriod)) {
				bcProperties.setReportPeriod(data.getReportPeriod());
			}
		}
		baseDAO.save(bcProperties);
		return new ActionResultDTO<>(entityToDto(bc, bcProperties));
	}

	@Override
	public Actions<BcPropertiesDTO, InnerBcDescription> getActions() {
		return Actions.<BcPropertiesDTO, InnerBcDescription>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
