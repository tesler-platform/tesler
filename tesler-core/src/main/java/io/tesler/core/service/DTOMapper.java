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

import static java.util.Collections.emptyMap;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.CrudmaActionHolder;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.dto.mapper.DtoConstructorService;
import io.tesler.core.ui.BcUtils;
import io.tesler.model.core.api.EntitySerializationEvent;
import io.tesler.model.core.entity.BaseEntity;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DTOMapper {

	private final ApplicationEventPublisher applicationEventPublisher;

	private final DtoConstructorService dtoConstructorService;

	private final TransactionService txService;

	private final BcUtils bcUtils;

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			boolean flushRequired, Map<String, Object> attributes) {
		return entityToDto(entity, dtoClass, bcUtils.getDtoFieldsForCurrentScreen(bc), flushRequired, attributes);
	}

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			boolean flushRequired) {
		return entityToDto(entity, dtoClass, bcUtils.getDtoFieldsForCurrentScreen(bc), flushRequired, emptyMap());
	}

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass) {
		return entityToDto(bc, entity, dtoClass, isFlushRequired(), emptyMap());
	}

	/**
	 * Creates a dto with the required set of fields for the current screen
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(BcIdentifier bc, E entity, Class<D> dtoClass,
			Map<String, Object> attributes) {
		return entityToDto(bc, entity, dtoClass, isFlushRequired(), attributes);
	}

	/**
	 * Creates a dto with a complete set of fields
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass) {
		return entityToDto(entity, dtoClass, bcUtils.getDtoFields(dtoClass), isFlushRequired(), emptyMap());
	}

	/**
	 * Creates a dto with a given set of fields
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> fields, boolean flushRequired) {
		return entityToDto(entity, dtoClass, fields, flushRequired, emptyMap());
	}

	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			DtoField<D, ?> field) {
		return entityToDto(entity, dtoClass, Collections.singleton(field));
	}

	/**
	 * Creates a dto with a given set of fields
	 */
	public <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> fields) {
		return entityToDto(entity, dtoClass, fields, isFlushRequired());
	}

	private <E extends BaseEntity, D extends DataResponseDTO> D entityToDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> fields, boolean flushRequired, final Map<String, Object> attributes) {
		if (flushRequired) {
			sendSerializationEvent(entity);
		}
		D result = createDto(entity, dtoClass, fields, attributes);
		setVstamp(result, entity);
		return result;
	}

	private <E extends BaseEntity, D extends DataResponseDTO> D createDto(E entity, Class<D> dtoClass,
			Set<DtoField<D, ?>> dtoFields, final Map<String, Object> attributes) {
		return dtoConstructorService.create(entity, dtoClass, dtoFields, attributes);
	}

	private void setVstamp(Object dto, BaseEntity entity) {
		if (!(dto instanceof DataResponseDTO)) {
			return;
		}
		DataResponseDTO responseDTO = (DataResponseDTO) dto;
		responseDTO.setVstamp(entity.getVstamp());
		txService.invokeAfterCompletion(Invoker.of(() -> responseDTO.setVstamp(entity.getVstamp())));
	}

	private void sendSerializationEvent(BaseEntity entity) {
		applicationEventPublisher.publishEvent(new EntitySerializationEvent(this, entity));
	}

	private boolean isFlushRequired() {
		CrudmaActionType action = CrudmaActionHolder.getActionType();
		return action != null && action.isFlushRequired();
	}

}
