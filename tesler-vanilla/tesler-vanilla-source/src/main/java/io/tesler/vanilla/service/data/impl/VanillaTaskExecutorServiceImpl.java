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
import io.tesler.model.core.entity.User;
import io.tesler.vanilla.dto.TaskExecutorDTO;
import io.tesler.vanilla.service.data.VanillaTaskExecutorService;
import org.springframework.stereotype.Service;

@Service
public class VanillaTaskExecutorServiceImpl extends AbstractResponseService<TaskExecutorDTO, User> implements
		VanillaTaskExecutorService {

	public VanillaTaskExecutorServiceImpl() {
		super(TaskExecutorDTO.class, User.class, null, null);
	}

	@Override
	public ActionResultDTO<TaskExecutorDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

}
