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

package io.tesler.core.service;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.model.core.entity.BaseEntity;
import java.util.Map;
import java.util.Set;

public interface DTOMapper {

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			boolean flushRequired, Map<String, Object> attributes);

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			boolean flushRequired);

	/**
	 * Creates a dto with the required set of fields for the current Response Service
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass);

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			Map<String, Object> attributes);

	/**
	 * Creates a dto with a full set of fields
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass);

	/**
	 * Creates a dto with a given set of fields
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> fields, boolean flushRequired);

	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			DtoField<D, ?> field);

	/**
	 * Creates a dto with a given set of fields
	 */
	<E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> fields);

}
