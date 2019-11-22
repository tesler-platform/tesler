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

import static io.tesler.core.controller.param.SortType.ASC;

import java.util.function.BiFunction;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class SortParameter extends AbstractQueryParameter {

	@Getter
	private final SortType type;

	@Getter
	private final Integer priority;

	public SortParameter(String name, SortType type, Integer priority) {
		super(name);
		this.type = type;
		this.priority = priority;
	}

	@Override
	public <R> R apply(BiFunction<String, String, R> biFunction) {
		return biFunction.apply(String.format("_sort.%d.%s", getPriority(), getType().name().toLowerCase()), getName());
	}

	public static class Builder implements ParameterBuilder<SortParameter> {

		private static final ParameterBuilder<SortParameter> INSTANCE = new Builder();

		public static ParameterBuilder<SortParameter> getInstance() {
			return INSTANCE;
		}

		@Override
		public Class<SortParameter> getParameterType() {
			return SortParameter.class;
		}

		@Override
		public boolean matches(String key, String value) {
			return key.startsWith("_sort.");
		}

		@Override
		public SortParameter buildParameter(String key, String value) {
			if (matches(key, value)) {
				String field = key.replace("_sort.", "");
				String[] entryKey = field.split("\\.");
				SortType type = SortType.of(entryKey[1], ASC);
				Integer priority = Integer.valueOf(entryKey[0]);
				return new SortParameter(value, type, priority);
			}
			return null;
		}

	}

}
