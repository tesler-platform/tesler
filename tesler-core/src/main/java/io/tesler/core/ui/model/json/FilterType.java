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

package io.tesler.core.ui.model.json;

import io.tesler.api.util.MapUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FilterType {

	EQUALS("equals"),
	GREATER_THAN("greaterThan"),
	LESS_THAN("lessThan"),
	GREATER_OR_EQUAL_THAN("greaterOrEqualThan"),
	LESS_OR_EQUAL_THAN("lessOrEqualThan"),
	CONTAINS("contains"),
	SPECIFIED("specified"),
	SPECIFIED_BOOLEAN_SQL("specifiedBooleanSql"),
	EQUALS_ONE_OF("equalsOneOf"),
	CONTAINS_ONE_OF("containsOneOf");

	private static final Map<String, FilterType> VALUES = MapUtils.of(FilterType.class, FilterType::getValue);

	@JsonValue
	public String getValue() {
		return value;
	}

	private final String value;

	@JsonCreator
	public static FilterType of(final String stringValue) {
		final FilterType value = VALUES.get(stringValue);
		if (value == null) {
			throw new IllegalArgumentException(String.format("Неверное значение для перечисления - %s", stringValue));
		}
		return value;
	}

}
