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
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.vanilla.dto.VanillaSupFactDTO;
import io.tesler.vanilla.entity.VanillaSupervisoryFact;
import io.tesler.vanilla.entity.VanillaSupervisoryFact_;
import io.tesler.vanilla.service.data.VanillaSupFactService;
import org.springframework.stereotype.Service;

@Service
public class VanillaSupFactServiceImpl extends
		AbstractResponseService<VanillaSupFactDTO, VanillaSupervisoryFact> implements VanillaSupFactService {

	public VanillaSupFactServiceImpl() {
		super(VanillaSupFactDTO.class, VanillaSupervisoryFact.class, VanillaSupervisoryFact_.party, null);
	}

	@Override
	public ActionResultDTO<VanillaSupFactDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

}
