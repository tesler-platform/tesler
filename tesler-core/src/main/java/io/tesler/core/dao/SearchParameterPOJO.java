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

package io.tesler.core.dao;

import static io.tesler.api.data.dictionary.DictionaryCache.dictionary;
import static lombok.AccessLevel.PRIVATE;

import io.tesler.api.data.Period;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.controller.param.SearchOperation;
import io.tesler.core.dto.LovUtils;
import io.tesler.core.exception.ClientException;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.core.util.filter.MultisourceSearchParameter;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.SearchParameterType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public class SearchParameterPOJO {

	@Getter
	private String field;

	@Getter
	private SearchOperation operator;

	@Getter
	private Object value;

	@Getter
	private SearchParameterType type;

	public static List<SearchParameterPOJO> mapSearchParamsToPOJO(Class dtoClazz, FilterParameters searchParams) {
		List<SearchParameterPOJO> result = new ArrayList<>();
		DictionaryCache dc = dictionary();
		searchParams.forEach(param -> {
			try {
				Field dtoField = ReflectionUtils.findField(dtoClazz, param.getName());
				if (dtoField == null) {
					throw new IllegalArgumentException(
							"Не найдено поле " + param.getName() + " в классе " + dtoClazz.getName());
				}
				MultisourceSearchParameter multisourceParameter = dtoField
						.getDeclaredAnnotation(MultisourceSearchParameter.class);
				if (multisourceParameter != null) {
					SearchParameterPOJO pojo = new SearchParameterPOJO();
					pojo.type = SearchParameterType.MULTISOURCE;
					pojo.field = param.getName();
					pojo.operator = param.getOperation();

					pojo.value = Stream.of(multisourceParameter.value())
							.map(par -> parseSingleParameter(dtoField, par, dc, param))
							.filter(par -> !par.isEmpty())
							.collect(Collectors.toList());
					result.add(pojo);
				} else {
					SearchParameter parameter = dtoField.getDeclaredAnnotation(SearchParameter.class);
					if (parameter == null) {
						throw new IllegalArgumentException(
								"Необходимо указать аннотацию @SearchParameter в DTO для поля: " + param.getName());
					}
					result.addAll(parseSingleParameter(dtoField, parameter, dc, param));
				}
			} catch (Exception e) {
				log.warn("Не удалось распарсить параметр фильтрации: " + param, e);
			}

		});
		return result;
	}

	private static List<SearchParameterPOJO> parseSingleParameter(Field dtoField, SearchParameter parameter,
			DictionaryCache dc, FilterParameter param) {
		List<SearchParameterPOJO> result = new ArrayList<>();
		if (parameter.suppressProcess()) {
			return result;
		}
		SearchParameterPOJO pojo = new SearchParameterPOJO();
		String dtoFieldName = param.getName();
		pojo.operator = param.getOperation();
		if ("id".equals(dtoFieldName)) {
			pojo.field = dtoFieldName;
			pojo.value = param.getLongValue();
			pojo.type = SearchParameterType.LONG;
		} else {
			boolean tzAware = FieldDTO.isTzAware(dtoField);
			pojo.type = parameter.type();
			if (!"".equals(parameter.name())) {
				pojo.field = parameter.name();
			} else {
				pojo.field = dtoFieldName;
			}
			if (pojo.operator == SearchOperation.SPECIFIED) {
				pojo.value = param.getBooleanValue();
			} else if (pojo.operator.equals(SearchOperation.CONTAINS_ONE_OF) ||
					pojo.operator.equals(SearchOperation.EQUALS_ONE_OF)) {
				switch (parameter.type()) {
					case STRING:
						pojo.value = param.getStringValuesAsList();
						break;
					case LOV:
						IDictionaryType type = LovUtils.getType(dtoField);
						if (type == null) {
							throw new ServerException("Необходимо указать аннотацию @Lov в DTO для поля типа LOV: " + dtoFieldName);
						}
						pojo.value = param.getStringValuesAsList().stream()
								.map(string -> type.lookupName(string))
								.collect(Collectors.toList());
						break;
					case DATE:
						List<Period<LocalDateTime>> periods = new ArrayList<>();
						for (LocalDateTime value : param.getDateValueAsList()) {
							LocalDateTime startValue = value.with(DateTimeUtil.asStartOfDay())
									.with(DateTimeUtil.fromSession(tzAware));
							LocalDateTime endValue = value.with(DateTimeUtil.asEndOfDay()).with(DateTimeUtil.fromSession(tzAware));
							periods.add(new Period<>(startValue, endValue));
						}
						pojo.value = periods;
						pojo.operator = SearchOperation.INTERVALS;
						break;
					case LONG:
						pojo.value = param.getLongValuesAsList();
						break;
					default:
						throw new ClientException("фильтрация по листу поддерживается только для строковых типов, дат и LOV-ов");
				}
			} else {
				switch (parameter.type()) {
					case STRING:
						pojo.value = param.getStringValue();
						break;
					case DATE_TIME:
						switch (pojo.operator) {
							case GREATER_OR_EQUAL_THAN:
							case LESS_THAN:
								pojo.value = param.getDateValue().with(DateTimeUtil.asStartOfDay())
										.with(DateTimeUtil.fromSession(tzAware));
								if (parameter.strict()) {
									pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								}
								break;
							case LESS_OR_EQUAL_THAN:
							case GREATER_THAN:
								pojo.value = param.getDateValue().with(DateTimeUtil.asEndOfDay())
										.with(DateTimeUtil.fromSession(tzAware));
								if (parameter.strict()) {
									pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								}
								break;
							case EQUALS:
								LocalDateTime startValue = param.getDateValue().withSecond(0);
								pojo.value = startValue.with(DateTimeUtil.fromSession(tzAware));
								pojo.operator = SearchOperation.GREATER_OR_EQUAL_THAN;
								SearchParameterPOJO pojoAdditional = new SearchParameterPOJO();
								pojoAdditional.operator = SearchOperation.LESS_THAN;
								pojoAdditional.field = pojo.field;

								pojoAdditional.value = LocalDateTime.of(
										startValue.toLocalDate(),
										startValue.toLocalTime().plusMinutes(1)
								).with(DateTimeUtil.fromSession(tzAware));
								result.add(pojoAdditional);
								break;
							default:
								pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								break;
						}
						break;
					case DATE:
						switch (pojo.operator) {
							case GREATER_OR_EQUAL_THAN:
							case LESS_THAN:
								pojo.value = param.getDateValue().with(DateTimeUtil.asStartOfDay())
										.with(DateTimeUtil.fromSession(tzAware));
								if (parameter.strict()) {
									pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								}
								break;
							case LESS_OR_EQUAL_THAN:
							case GREATER_THAN:
								pojo.value = param.getDateValue().with(DateTimeUtil.asEndOfDay())
										.with(DateTimeUtil.fromSession(tzAware));
								if (parameter.strict()) {
									pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								}
								break;
							case EQUALS:
								LocalDateTime startValue = param.getDateValue().with(DateTimeUtil.asStartOfDay());
								pojo.value = startValue.with(DateTimeUtil.fromSession(tzAware));
								pojo.operator = SearchOperation.GREATER_OR_EQUAL_THAN;
								SearchParameterPOJO pojoAdditional = new SearchParameterPOJO();
								pojoAdditional.operator = SearchOperation.LESS_THAN;
								pojoAdditional.field = pojo.field;

								pojoAdditional.value = startValue.with(DateTimeUtil.asEndOfDay())
										.with(DateTimeUtil.fromSession(tzAware));
								result.add(pojoAdditional);
								break;
							default:
								pojo.value = param.getDateValue().with(DateTimeUtil.fromSession(tzAware));
								break;
						}
						break;
					case LOV:
						IDictionaryType type = LovUtils.getType(dtoField);
						if (type == null) {
							throw new IllegalArgumentException(
									"Необходимо указать аннотацию @Lov в DTO для поля типа LOV: " + dtoFieldName);
						}
						pojo.value = type.lookupName(param.getStringValue());
						break;
					case LONG:
						pojo.value = param.getLongValue();
						break;
					case BOOLEAN:
						pojo.value = param.getBooleanValue();
						break;
					case BIG_DECIMAL:
						pojo.value = param.getBigDecimalValue();
				}
			}
		}
		result.add(pojo);

		return result;
	}

}
