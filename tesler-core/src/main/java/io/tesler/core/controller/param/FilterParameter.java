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

import io.tesler.core.util.TypeConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class FilterParameter extends AbstractQueryParameter {

	@Getter
	private final SearchOperation operation;

	@Getter
	private final String stringValue;

	public FilterParameter(String name, SearchOperation operation, String stringValue) {
		super(name);
		this.operation = operation;
		this.stringValue = stringValue;
	}

	@Override
	public <R> R apply(BiFunction<String, String, R> biFunction) {
		return biFunction.apply(String.format("%s.%s", getName(), operation.getOperationName()), stringValue);
	}

	public <T> T getValue(final Class<T> clazz) {
		return TypeConverter.to(clazz, stringValue);
	}

	public Integer getIntegerValue() {
		return TypeConverter.toInteger(stringValue);
	}

	public Long getLongValue() {
		return TypeConverter.toLong(stringValue);
	}

	public Double getDoubleValue() {
		return TypeConverter.toDouble(stringValue);
	}

	public BigDecimal getBigDecimalValue() {
		return TypeConverter.toBigDecimal(stringValue);
	}

	public Boolean getBooleanValue() {
		return TypeConverter.toBoolean(stringValue);
	}

	public LocalDateTime getDateValue() {
		return TypeConverter.toLocalDateTime(stringValue);
	}

	public List<String> getStringValuesAsList() {
		return QueryParameter.getListValue(stringValue, String.class);
	}

	public List<LocalDateTime> getDateValueAsList() {
		return QueryParameter.getListValue(stringValue, LocalDateTime.class);
	}

	public List<BigDecimal> getBigDecimalValuesAsList() {
		return QueryParameter.getListValue(stringValue, BigDecimal.class);
	}

	public List<Long> getLongValuesAsList() {
		return QueryParameter.getListValue(stringValue, Long.class);
	}

	public static class Builder implements ParameterBuilder<FilterParameter> {

		private static final ParameterBuilder<FilterParameter> INSTANCE = new Builder();

		public static ParameterBuilder<FilterParameter> getInstance() {
			return INSTANCE;
		}

		@Override
		public Class<FilterParameter> getParameterType() {
			return FilterParameter.class;
		}

		@Override
		public boolean matches(String key, String value) {
			// todo: на самом деле нужно переименовать параметры для поиска
			if (!key.startsWith("_")) {
				String[] entryKey = key.split("\\.");
				return entryKey.length == 2;
			}
			return false;
		}

		@Override
		public FilterParameter buildParameter(String key, String value) {
			if (matches(key, value)) {
				String[] entryKey = key.split("\\.");
				return new FilterParameter(entryKey[0], SearchOperation.of(entryKey[1]), value);
			}
			return null;
		}

	}

}
