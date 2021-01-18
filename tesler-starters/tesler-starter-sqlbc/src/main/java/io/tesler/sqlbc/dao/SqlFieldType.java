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

package io.tesler.sqlbc.dao;

import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SqlFieldType {

	STRING(String.class, false),
	CLOB(String.class, false),
	BIG_DECIMAL(BigDecimal.class, false),
	BOOLEAN(Boolean.class, false),
	BYTE(Byte.class, false),
	SHORT(Short.class, false),
	INTEGER(Integer.class, false),
	LONG(Long.class, false),
	FLOAT(Float.class, false),
	DOUBLE(Double.class, false),
	DATE(Timestamp.class, true),
	TIME(Timestamp.class, true),
	TIMESTAMP(Timestamp.class, true);

	private final Class<?> javaClass;

	private final boolean chronological;

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Holder {

		private static final Map<Integer, SqlFieldType> TYPES = ImmutableMap.<Integer, SqlFieldType>builder()
				.put(Types.CHAR, STRING)
				.put(Types.VARCHAR, STRING)
				.put(Types.CLOB, CLOB)
				.put(Types.LONGVARCHAR, STRING)
				.put(Types.NUMERIC, BIG_DECIMAL)
				.put(Types.DECIMAL, BIG_DECIMAL)
				.put(Types.BIT, BOOLEAN)
				.put(Types.TINYINT, BYTE)
				.put(Types.SMALLINT, SHORT)
				.put(Types.INTEGER, INTEGER)
				.put(Types.BIGINT, LONG)
				.put(Types.REAL, FLOAT)
				.put(Types.FLOAT, FLOAT)
				.put(Types.DOUBLE, DOUBLE)
				.put(Types.DATE, DATE)
				.put(Types.TIME, TIME)
				.put(Types.TIMESTAMP, TIMESTAMP)
				.build();

		public static SqlFieldType getFromSqlType(int sqlType) {
			if (!TYPES.containsKey(sqlType)) {
				throw new IllegalArgumentException(String.format("Неподдерживаемый sql-тип: %d", sqlType));
			}
			return TYPES.get(sqlType);
		}

	}

}
