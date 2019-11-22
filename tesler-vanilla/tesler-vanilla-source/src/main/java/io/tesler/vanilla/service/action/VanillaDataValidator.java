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

package io.tesler.vanilla.service.action;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.service.action.DataValidator;
import io.tesler.vanilla.dto.VanillaDocDTO;
import io.tesler.vanilla.dto.VanillaDocDTO_;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class VanillaDataValidator implements DataValidator<VanillaDocDTO> {

	@Override
	public List<String> validate(BusinessComponent bc, DataResponseDTO data, VanillaDocDTO entityDto) {
		if (data.isFieldChanged(VanillaDocDTO_.testDateTime)) {
			return Collections.singletonList("Сработала валидация");
		}
		return Collections.emptyList();
	}

}
