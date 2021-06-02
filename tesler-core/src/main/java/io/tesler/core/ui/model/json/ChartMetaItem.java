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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public final class ChartMetaItem {

	@JsonProperty(value = "default")
	private Boolean isDefault;

	private Engine engine;

	private String name;

	private Map<String, Object> meta;

	private List<String> hiddenBCBinds;

	@RequiredArgsConstructor
	public enum Engine {

		HIGHCHARTS("highcharts"),
		HIGHMAPS("highmaps"),
		GANTCHARTS("ganttcharts");

		private static final Map<String, Engine> VALUES = MapUtils.of(Engine.class, Engine::getValue);

		@JsonValue
		public String getValue() {
			return value;
		}

		private final String value;

		@JsonCreator
		public static Engine of(final String stringValue) {
			final Engine value = VALUES.get(stringValue);
			if (value == null) {
				throw new IllegalArgumentException(String.format("Неверное значение для перечисления - %s", stringValue));
			}
			return value;
		}

	}

}
