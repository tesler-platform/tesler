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

package io.tesler.core.util;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.exception.ServerException;
import io.tesler.core.service.DTOMapper;
import io.tesler.model.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DmnEngine {

	private static final String DMN_LOAD_ERROR = "DMN правило задано некорректно.";

	private final DTOMapper dtoMapper;

	private final org.camunda.bpm.dmn.engine.DmnEngine dmnEngine = DmnEngineConfiguration
			.createDefaultDmnEngineConfiguration().buildEngine();

	private final Map<String, DmnDecision> decisionMap = new ConcurrentHashMap<>();

	public DmnDecisionTableResult evaluate(String dmnSchema, BaseEntity entity,
			Class<? extends DataResponseDTO> dtoClass) {
		return dmnEngine.evaluateDecisionTable(
				parseDecision(dmnSchema),
				Variables.fromMap(buildVariableMap(entity, dtoClass))
		);
	}

	private DmnDecision parseDecision(String dmnSchema) {
		if (dmnSchema == null) {
			throw new IllegalArgumentException(DMN_LOAD_ERROR);
		}
		return decisionMap.computeIfAbsent(dmnSchema, s -> {
			InputStream dmnStream = IOUtils.toInputStream(s, StandardCharsets.UTF_8);
			List<DmnDecision> dmnDecisions = dmnEngine.parseDecisions(dmnStream);
			if (dmnDecisions.isEmpty()) {
				throw new IllegalArgumentException(DMN_LOAD_ERROR);
			}
			return dmnDecisions.get(0);
		});
	}

	private Map<String, Object> buildVariableMap(BaseEntity entity, Class<? extends DataResponseDTO> dtoClass) {
		final DataResponseDTO dto = dtoMapper.entityToDto(entity, dtoClass);
		Map<String, Object> result = new HashMap<>();
		result.put("helper", new DmnDataHelper());
		for (Field field : FieldUtils.getAllFieldsList(dtoClass)) {
			if (!field.isAnnotationPresent(JsonIgnore.class)) {
				try {
					Object value = FieldUtils.readField(field, dto, true);
					if (value instanceof LocalDateTime) {
						result.put(field.getName(), DateTimeUtil.toDate((LocalDateTime) value));
					} else if (value instanceof BigDecimal) {
						result.put(field.getName(), ((BigDecimal) value).doubleValue());
					} else {
						result.put(field.getName(), value);
					}
				} catch (Exception e) {
					throw new ServerException("Ошибка подготовки данных для выполнения DMN правила", e);
				}
			}
		}
		return result;
	}

	@Getter
	private static class DmnDataHelper {

		private final Date today = DateTimeUtil.toDate(DateTimeUtil.now());

	}

}
