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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CellStyle {

	private Align align;

	private Boolean bold;

	private Boolean cursive;

	private Boolean expanded;

	private String hintKey;

	@RequiredArgsConstructor
	public enum Align {

		LEFT("left"),
		RIGHT("right"),
		CENTER("center");

		private static final Map<String, Align> VALUES = MapUtils.of(Align.class, Align::getValue);

		@Getter(onMethod = @__(@JsonValue))
		private final String value;

		@JsonCreator
		public static Align of(final String stringValue) {
			final Align value = VALUES.get(stringValue);
			if (value == null) {
				throw new IllegalArgumentException(String.format("Неверное значение для перечисления - %s", stringValue));
			}
			return value;
		}

	}

}
