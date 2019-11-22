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

package io.tesler.core.controller.param;

import io.tesler.api.util.MapUtils;
import io.tesler.core.exception.ClientException;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchOperation {

	/**
	 * Равно
	 */
	EQUALS("equals"),

	/**
	 * Больше
	 */
	GREATER_THAN("greaterThan"),

	/**
	 * Меньше
	 */
	LESS_THAN("lessThan"),

	/**
	 * Больше или равно
	 */
	GREATER_OR_EQUAL_THAN("greaterOrEqualThan"),

	/**
	 * Меньше или равно
	 */
	LESS_OR_EQUAL_THAN("lessOrEqualThan"),

	/**
	 * Содержит
	 */
	CONTAINS("contains"),

	/**
	 * Указано
	 */
	SPECIFIED("specified"),

	/**
	 * Указано булево значение, использовать для sql-сущностей
	 */
	SPECIFIED_BOOLEAN_SQL("specifiedBooleanSql"),

	/**
	 * Равно одному из списка
	 */
	EQUALS_ONE_OF("equalsOneOf"),

	/**
	 * Содержит один из списка
	 */
	CONTAINS_ONE_OF("containsOneOf"),

	/**
	 * Указано значение в итервалах
	 */
	INTERVALS("intervals");

	private static final Map<String, SearchOperation> OPERATIONS = MapUtils.of(
			SearchOperation.class, SearchOperation::getOperationName
	);

	private final String operationName;

	public static SearchOperation of(String operationName) {
		if (!OPERATIONS.containsKey(operationName)) {
			throw new ClientException("Неизвестная операция поиска: + " + operationName);
		}
		return OPERATIONS.get(operationName);
	}

}
