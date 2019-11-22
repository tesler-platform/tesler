/*-
 * #%L
 * IO Tesler - Dictionary Crudma
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

package io.tesler.source.services.data.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.model.dictionary.entity.DictionaryTypeDesc;
import io.tesler.source.dto.DictionaryTypeDescDTO;
import io.tesler.source.dto.DictionaryTypeDescDTO_;
import io.tesler.source.services.data.DictionaryTypeDescService;
import io.tesler.source.services.meta.DictionaryTypeDescFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class DictionaryTypeDescServiceImpl extends
		VersionAwareResponseService<DictionaryTypeDescDTO, DictionaryTypeDesc> implements
		DictionaryTypeDescService {

	public DictionaryTypeDescServiceImpl() {
		super(DictionaryTypeDescDTO.class, DictionaryTypeDesc.class, null, DictionaryTypeDescFieldMetaBuilder.class);
	}

	@Override
	protected CreateResult<DictionaryTypeDescDTO> doCreateEntity(final DictionaryTypeDesc entity,
			final BusinessComponent bc) {
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<DictionaryTypeDescDTO> doUpdateEntity(DictionaryTypeDesc item,
			DictionaryTypeDescDTO data, BusinessComponent bc) {
		if (data.isFieldChanged(DictionaryTypeDescDTO_.type)) {
			item.setType(data.getType());
		}
		if (data.isFieldChanged(DictionaryTypeDescDTO_.typeDesc)) {
			item.setTypeDesc(data.getTypeDesc());
		}
		return new ActionResultDTO<>(entityToDto(bc, item));
	}

	@Override
	public Actions<DictionaryTypeDescDTO> getActions() {
		return Actions.<DictionaryTypeDescDTO>builder()
				.create().add()
				.save().add()
				.build();
	}

}
