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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.tesler.api.util.MapUtils;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FieldType {

	NUMBER("number"),
	INPUT("input"),
	HINT("hint"),
	MONTH_YEAR("monthYear"),
	DATE("date"),
	DATETIME("dateTime"),
	DATETIME_WITH_SECONDS("dateTimeWithSeconds"),
	CHECKBOX("checkbox"),
	CHECKBOX_SQL("checkboxSql"),
	DMN("DMN"),
	PICKLIST("pickList"),
	INLINE_PICKLIST("inline-pickList"),
	DICTIONARY("dictionary"),
	HIDDEN("hidden"),
	TEXT("text"),
	PERCENT("percent"),
	FILE_UPLOAD("fileUpload"),
	MONEY("money"),
	COMBO_CONDITION("combo-condition"),
	RICHTEXT("richText"),
	PRINTFORM("printForm"),
	MULTIVALUE("multivalue"),
	MULTIVALUE_HOVER("multivalueHover"),
	MULTIFIELD("multifield"),
	DIFFTEXT("diffText");

	private static final Map<String, FieldType> VALUES = MapUtils.of(FieldType.class, FieldType::getValue);

	@Getter(onMethod = @__(@JsonValue))
	private final String value;

	@JsonCreator
	public static FieldType of(final String stringValue) {
		final FieldType value = VALUES.get(stringValue);
		if (value == null) {
			throw new IllegalArgumentException(String.format("Неверное значение для перечисления - %s", stringValue));
		}
		return value;
	}

}
