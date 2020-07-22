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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.DataResponseDTO_;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.BusinessError.Entity;
import io.tesler.core.dto.ValidatorsProvider;
import io.tesler.core.exception.BusinessException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Factory for services creation
 */
@Service
@RequiredArgsConstructor
public class ResponseFactory {

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper mapper;

	private final ApplicationContext ctx;

	@Autowired
	private ValidatorsProvider validatorsProvider;

	/**
	 * @param innerBcDescription information about BC;
	 * @return {@link ResponseService} common interface implemented by all services working with the controller
	 */
	public ResponseService getService(InnerBcDescription innerBcDescription) {
		return ctx.getBean(getServiceClass(innerBcDescription));
	}

	public DataResponseDTO getDTOFromMap(Map<String, Object> map, Class<?> clazz, BusinessComponent bc) {
		return getDTOFromMapInner(map, clazz, bc, false);
	}

	public DataResponseDTO getDTOFromMapIgnoreBusinessErrors(Map<String, Object> map, Class<?> clazz,
			BusinessComponent bc) {
		return getDTOFromMapInner(map, clazz, bc, true);
	}

	private DataResponseDTO getDTOFromMapInner(Map<String, Object> map, Class<?> clazz, BusinessComponent bc,
			boolean ignoreBusinessErrors) {
		DtoDeserializationHandler handler = new DtoDeserializationHandler();
		mapper.addHandler(handler);
		Object objectResult;
		try {
			objectResult = mapper.convertValue(map, clazz);
		} catch (IllegalArgumentException e) {
			throw new BusinessException().addPopup(errorMessage("error.dto_deserialization_error"));
		}

		if (!(objectResult instanceof DataResponseDTO)) {
			throw new IllegalArgumentException(clazz + " doesn't extend from " + DataResponseDTO.class);
		}
		Entity entity = null;
		Set<ConstraintViolation<Object>> violations = validatorsProvider.getValidator(clazz).validate(objectResult);
		Set<String> badFields = handler.getFields();
		if (!badFields.isEmpty() || !violations.isEmpty()) {
			entity = new Entity(bc);
			for (String fieldName : badFields) {
				entity.addField(fieldName, errorMessage("error.field_deserialization_error"));
			}
			if (!ignoreBusinessErrors) {
				for (ConstraintViolation<Object> violation : violations) {
					String fieldName = null;
					for (Path.Node node : violation.getPropertyPath()) {
						fieldName = node.getName();
					}
					entity.addField(fieldName, violation.getMessage());
				}
				throw new BusinessException()
						.setEntity(entity);
			}
		}

		Set<String> fields = new HashSet<>(map.keySet());
		// штамп времени и id за поле не считаем
		fields.remove(DataResponseDTO_.vstamp.getName());
		fields.remove(DataResponseDTO_.id.getName());
		DataResponseDTO result = (DataResponseDTO) objectResult;
		result.setChangedFields(fields);
		// Чтобы можно было назад возвращать
		result.setId(bc.getId());
		if (entity != null && !entity.getFields().isEmpty()) {
			result.setErrors(entity);
		}
		return result;
	}

	public Class getDTOFromService(InnerBcDescription innerBcDescription) {
		return (Class) getResponseServiceParameters(innerBcDescription)[0];
	}

	public Class getEntityFromService(InnerBcDescription innerBcDescription) {
		return (Class) getResponseServiceParameters(innerBcDescription)[1];
	}

	private Type[] getResponseServiceParameters(Class<? extends ResponseService> cls) {
		Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(cls, ResponseService.class);
		return Stream.of(ResponseService.class.getTypeParameters())
				.map(typeArguments::get)
				.toArray(Type[]::new);
	}

	public Type[] getResponseServiceParameters(InnerBcDescription innerBcDescription) {
		return getResponseServiceParameters(getServiceClass(innerBcDescription));
	}

	private Class<? extends ResponseService> getServiceClass(InnerBcDescription innerBcDescription) {
		if (ResponseService.class.isAssignableFrom(innerBcDescription.getServiceClass())) {
			return innerBcDescription.getServiceClass();
		}
		throw new IllegalArgumentException(
				"can't cast " + innerBcDescription.getServiceClass().toString() + " to " + ResponseService.class.toString()
		);
	}

	class DtoDeserializationHandler extends DeserializationProblemHandler {

		private final Set<String> fields = new HashSet<>();

		@Override
		public Object handleWeirdStringValue(
				DeserializationContext ctxt,
				Class<?> targetType,
				String valueToConvert,
				String failureMsg) {
			fields.add(ctxt.getParser().getParsingContext().getCurrentName());
			return null;
		}

		@Override
		public Object handleWeirdNumberValue(
				DeserializationContext ctxt,
				Class<?> targetType,
				Number valueToConvert,
				String failureMsg) {
			fields.add(ctxt.getParser().getParsingContext().getCurrentName());
			return null;
		}

		@Override
		public JavaType handleUnknownTypeId(
				DeserializationContext ctxt,
				JavaType baseType,
				String subTypeId,
				TypeIdResolver idResolver,
				String failureMsg) {
			fields.add(ctxt.getParser().getParsingContext().getCurrentName());
			return null;
		}

		public Set<String> getFields() {
			return fields;
		}

	}

}
