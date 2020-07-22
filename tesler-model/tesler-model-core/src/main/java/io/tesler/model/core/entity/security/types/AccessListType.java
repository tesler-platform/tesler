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
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Access List Type
 */
@Getter
@RequiredArgsConstructor
public enum AccessListType {

	/**
	 * individual for each entity
	 */
	PRIVATE(parseInt(Values.PRIVATE)),
	/**
	 * used by multiple entities
	 */
	SHARED(parseInt(Values.SHARED)),
	/**
	 * template
	 */
	TEMPLATE(parseInt(Values.TEMPLATE));

	private static final Map<Integer, AccessListType> ALL_TYPES = MapUtils
			.of(AccessListType.class, AccessListType::getIntValue);

	private final int intValue;

	public static AccessListType of(int intValue) {
		return ALL_TYPES.getOrDefault(intValue, PRIVATE);
	}

	/**
	 * String constants to use as a discriminator
	 */
	static class Values {

		public static final String PRIVATE = "0";

		public static final String SHARED = "1";

		public static final String TEMPLATE = "2";

	}

}
