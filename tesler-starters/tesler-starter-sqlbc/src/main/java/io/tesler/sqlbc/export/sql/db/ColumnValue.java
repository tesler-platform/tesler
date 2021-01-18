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

package io.tesler.sqlbc.export.sql.db;

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
import io.tesler.sqlbc.dao.SqlFieldType;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.BooleanUtils;

@Getter
@ToString
@RequiredArgsConstructor
public class ColumnValue {

	private final SqlFieldType type;

	private final Object objectValue;

	public String getValueForInsert() {
		switch (type) {
			case STRING:
				return "'" + escapeString(objectValue) + "'";
			case CLOB:
				return Streams.stream(Splitter.fixedLength(2000).split(String.valueOf(objectValue)))
						.map(this::escapeString)
						.map(string -> "TO_CLOB('" + string + "')")
						.collect(Collectors.joining(" || "));
			case BOOLEAN:
				return BooleanUtils.isTrue((Boolean) objectValue) ? "1" : "0";
			case DATE:
			case TIME:
			case TIMESTAMP:
				return timestamp(objectValue);
			case BIG_DECIMAL:
			case BYTE:
			case SHORT:
			case INTEGER:
			case LONG:
			case FLOAT:
			case DOUBLE:
			default:
				return String.valueOf(objectValue);
		}
	}

	private String escapeString(final Object src) {
		return String.valueOf(src).replace("'", "''");
	}

	private String timestamp(final Object src) {
		return "TO_TIMESTAMP('" + String.valueOf(src) + "', 'YYYY-MM-DD HH24:MI:SS.FF')";
	}

}
