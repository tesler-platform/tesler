/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.data.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.vanilla.dto.VanillaResidentDTO;
import io.tesler.vanilla.entity.VanillaCounterparty;
import io.tesler.vanilla.service.data.VanillaResidentService;
import io.tesler.vanilla.service.meta.VanillaResidentFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class VanillaResidentServiceImpl extends
		VersionAwareResponseService<VanillaResidentDTO, VanillaCounterparty> implements VanillaResidentService {

	public VanillaResidentServiceImpl() {
		super(VanillaResidentDTO.class, VanillaCounterparty.class, null, VanillaResidentFieldMetaBuilder.class);
	}

	@Override
	protected CreateResult<VanillaResidentDTO> doCreateEntity(final VanillaCounterparty entity,
			final BusinessComponent bc) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ActionResultDTO<VanillaResidentDTO> doUpdateEntity(VanillaCounterparty task, VanillaResidentDTO data,
			BusinessComponent bc) {
		return new ActionResultDTO<>(entityToDto(bc, task));
	}

	@Override
	public ActionResultDTO<VanillaResidentDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

}
