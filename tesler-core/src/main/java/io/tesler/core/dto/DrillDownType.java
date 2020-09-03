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
import io.tesler.core.service.action.DrillDownTypeSpecifier;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DrillDownType implements DrillDownTypeSpecifier {

	/**
	 * Transition inside the application. Current tab
	 */
	INNER("inner"),

	/**
	 * Transition by a relative link while keeping the protocol, host, port. Current tab
	 */
	RELATIVE("relative"),

	/**
	 * Transition by a relative link while keeping the protocol, host, port. New tab
	 */
	RELATIVE_NEW("relativeNew"),

	/**
	 * Transition by an absolute link to an external source. Current tab.
	 */
	EXTERNAL("external"),

	/**
	 * Transition by an absolute link to an external source. New tab.
	 */
	EXTERNAL_NEW("externalNew");

	private static final Map<String, DrillDownType> TYPES = MapUtils.of(DrillDownType.class, DrillDownType::getValue);

	String value;

	public static DrillDownType of(final String type) {
		return TYPES.get(type);
	}

}
