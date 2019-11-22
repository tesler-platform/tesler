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

import io.tesler.api.exception.ServerException;
import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TypeConverter {

	private static final Map<Class<?>, Function<String, Object>> VALUE_MAPPERS = ImmutableMap.<Class<?>, Function<String, Object>>builder()
			.put(String.class, TypeConverter::toString)
			.put(Boolean.class, TypeConverter::toBoolean)
			.put(Boolean.TYPE, TypeConverter::toBoolean)
			.put(Integer.class, TypeConverter::toInteger)
			.put(Integer.TYPE, TypeConverter::toInteger)
			.put(Long.class, TypeConverter::toLong)
			.put(Long.TYPE, TypeConverter::toLong)
			.put(Double.class, TypeConverter::toDouble)
			.put(Double.TYPE, TypeConverter::toDouble)
			.put(Float.class, TypeConverter::toFloat)
			.put(BigDecimal.class, TypeConverter::toBigDecimal)
			.put(Byte.class, TypeConverter::toByte)
			.put(Short.class, TypeConverter::toShort)
			.put(LocalDateTime.class, TypeConverter::toLocalDateTime)
			.put(java.sql.Timestamp.class, TypeConverter::toSqlTimestamp)
			.build();

	public static String toString(final String stringValue) {
		return stringValue;
	}

	public static Integer toInteger(final String stringValue) {
		return Integer.valueOf(stringValue);
	}

	public static Long toLong(final String stringValue) {
		return Long.valueOf(stringValue);
	}

	public static Double toDouble(final String stringValue) {
		return Double.valueOf(stringValue);
	}

	public static Float toFloat(final String stringValue) {
		return Float.valueOf(stringValue);
	}

	public static BigDecimal toBigDecimal(final String stringValue) {
		return new BigDecimal(stringValue);
	}

	public static Byte toByte(final String stringValue) {
		return Byte.valueOf(stringValue);
	}

	public static Short toShort(final String stringValue) {
		return Short.valueOf(stringValue);
	}

	public static Boolean toBoolean(final String stringValue) {
		return Boolean.valueOf(stringValue);
	}

	public static LocalDateTime toLocalDateTime(final String stringValue) {
		return DateTimeUtil.stringToDateTime(stringValue);
	}

	public static LocalDateTime toLocalDateTimeTzAware(final String stringValue) {
		return toLocalDateTime(stringValue).with(DateTimeUtil.fromSession());
	}

	public static Timestamp toSqlTimestamp(final String stringValue) {
		return Timestamp.valueOf(toLocalDateTime(stringValue));
	}

	public static Timestamp toSqlTimestampTzAware(final String stringValue) {
		return Timestamp.valueOf(toLocalDateTimeTzAware(stringValue));
	}

	public <T> T to(final Class<T> clazz, final String stringValue) {
		return (T) VALUE_MAPPERS.getOrDefault(clazz, parameter -> {
			throw new ServerException(String
					.format("Невозможно преобразовать значение \"%s\" к классу %s", stringValue, clazz));
		}).apply(stringValue);
	}

}
