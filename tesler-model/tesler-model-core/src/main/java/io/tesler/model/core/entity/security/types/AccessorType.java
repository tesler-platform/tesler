/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.entity.security.types;

import static java.lang.Integer.parseInt;

import io.tesler.api.util.MapUtils;
import io.tesler.model.core.entity.security.Accessor;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Accessor type
 */
@Getter
@RequiredArgsConstructor
public enum AccessorType {

	/**
	 * User
	 */
	USER(parseInt(Values.USER)),
	/**
	 * group
	 */
	GROUP(parseInt(Values.GROUP));

	private static final Map<Integer, AccessorType> ALL_TYPES = MapUtils
			.of(AccessorType.class, AccessorType::getIntValue);

	private final int intValue;

	public static AccessorType of(int intValue) {
		return ALL_TYPES.getOrDefault(intValue, USER);
	}

	public Accessor toAccessor(Long id) {
		return new Accessor(this, id);
	}

	/**
	 * String constants to use as a discriminator
	 */
	public static class Values {

		public static final String USER = "0";

		public static final String GROUP = "1";

	}

}
