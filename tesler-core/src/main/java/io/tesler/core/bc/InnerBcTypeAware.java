/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.bc;


import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.service.ResponseFactory;
import io.tesler.model.core.entity.BaseEntity;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class InnerBcTypeAware {

	private final Map<String, InnerBcTypes> types;

	public InnerBcTypeAware(final BcRegistry bcRegistry, final ResponseFactory respFactory) {
		types = bcRegistry.select(InnerBcDescription.class).collect(Collectors.toMap(
				BcDescription::getName,
				bcDescription -> new InnerBcTypes(respFactory.getResponseServiceParameters(bcDescription))
		));
	}

	public Class<? extends BaseEntity> getTypeOfEntity(final InnerBcDescription bcDescription) {
		return types.get(bcDescription.getName()).getEntity();
	}

	public Class<? extends DataResponseDTO> getTypeOfDto(final InnerBcDescription bcDescription) {
		return types.get(bcDescription.getName()).getDto();
	}

	@Getter
	private static class InnerBcTypes {

		private final Class<? extends BaseEntity> entity;

		private final Class<? extends DataResponseDTO> dto;

		InnerBcTypes(final Type[] responseServiceTypes) {
			this.entity = (Class<? extends BaseEntity>) responseServiceTypes[1];
			this.dto = (Class<? extends DataResponseDTO>) responseServiceTypes[0];
		}

	}

}
