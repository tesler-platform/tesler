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

package io.tesler.core.dto;

import io.tesler.api.util.MapUtils;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DrillDownType {

	/**
	 * Переход внутри приложения. Текущая вкладка.
	 */
	INNER("inner"),

	/**
	 * Переход по относительной ссылке с сохранением протокола, хоста, порта. Текущая вкладка.
	 */
	RELATIVE("relative"),

	/**
	 * Переход по относительной ссылке с сохранением протокола, хоста, порта. Новая вкладка.
	 */
	RELATIVE_NEW("relativeNew"),

	/**
	 * Переход по абсолютной ссылке во внешний источник. Текущая вкладка.
	 */
	EXTERNAL("external"),

	/**
	 * Переход по абсолютной ссылке во внешний источник. Новая вкладка.
	 */
	EXTERNAL_NEW("externalNew");

	private static final Map<String, DrillDownType> TYPES = MapUtils.of(DrillDownType.class, DrillDownType::getValue);

	String value;

	public static DrillDownType of(final String type) {
		return TYPES.get(type);
	}

}
