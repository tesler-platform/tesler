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
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class BindParameter extends AbstractQueryParameter {

	@Getter
	private final SearchOperation operation;

	@Getter
	private final String stringValue;

	@Getter
	private final String sqlParameter;

	public BindParameter(String name, SearchOperation operation, String stringValue) {
		super(name);
		this.operation = operation;
		this.sqlParameter = getSQLParameter(name, operation);
		this.stringValue = stringValue;
	}

	private String getSQLParameter(String name, SearchOperation operation) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getName());
		if (operation != null) {
			stringBuilder.append("_").append(operation.getOperationName());
		}
		return stringBuilder.toString();
	}

	@Override
	public <R> R apply(BiFunction<String, String, R> biFunction) {
		StringBuilder stringBuilder = new StringBuilder("_bind.");
		stringBuilder.append(getName());
		if (operation != null) {
			stringBuilder.append(".").append(operation.getOperationName());
		}
		return biFunction.apply(stringBuilder.toString(), stringValue);
	}

	public <T> T getValue(final Class<T> clazz) {
		return TypeConverter.to(clazz, stringValue);
	}

	public String getBooleanValue() {
		return (TypeConverter.toBoolean(stringValue)) ? "Y" : "N";
	}

	public String getStringValuesAsString() {
		return String.join(",", QueryParameter.getListValue(stringValue, String.class));
	}

	public static class Builder implements ParameterBuilder<BindParameter> {

		private static final ParameterBuilder<BindParameter> INSTANCE = new Builder();

		public static ParameterBuilder<BindParameter> getInstance() {
			return INSTANCE;
		}

		@Override
		public Class<BindParameter> getParameterType() {
			return BindParameter.class;
		}

		@Override
		public boolean matches(String key, String value) {
			return key.startsWith("_bind.");
		}

		@Override
		public BindParameter buildParameter(String key, String value) {
			if (matches(key, value)) {
				String bind = key.replace("_bind.", "");
				String[] split = bind.split("\\.");
				SearchOperation operation = split.length == 1 ? null : SearchOperation.of(split[1]);
				return new BindParameter(split[0], operation, value);
			}
			return null;
		}

	}

}
