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
	 * Equal
	 */
	EQUALS("equals"),

	/**
	 * Greater
	 */
	GREATER_THAN("greaterThan"),

	/**
	 * Less
	 */
	LESS_THAN("lessThan"),

	/**
	 * Greater or equal
	 */
	GREATER_OR_EQUAL_THAN("greaterOrEqualThan"),

	/**
	 * Less or equal
	 */
	LESS_OR_EQUAL_THAN("lessOrEqualThan"),

	/**
	 * Contains
	 */
	CONTAINS("contains"),

	/**
	 * Specified
	 */
	SPECIFIED("specified"),

	/**
	 * Boolean value specified, use for sql entities
	 */
	SPECIFIED_BOOLEAN_SQL("specifiedBooleanSql"),

	/**
	 * Equal to one of the list
	 */
	EQUALS_ONE_OF("equalsOneOf"),

	/**
	 * Contains one from the list
	 */
	CONTAINS_ONE_OF("containsOneOf"),

	/**
	 * Specified value in intervals
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
