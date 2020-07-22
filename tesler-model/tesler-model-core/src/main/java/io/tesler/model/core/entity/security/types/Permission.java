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
 * Access rights type
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

	/**
	 * No access
	 */
	NONE(parseInt(Values.NONE)),
	/**
	 * reading
	 */
	READ(parseInt(Values.READ)),
	/**
	 * writing
	 */
	WRITE(parseInt(Values.WRITE)),
	/**
	 * deletion
	 */
	DELETE(parseInt(Values.DELETE));

	private static final Map<Integer, Permission> ALL_PERMISSIONS = MapUtils
			.of(Permission.class, Permission::getIntValue);

	private final int intValue;

	public static Permission of(int intValue) {
		return ALL_PERMISSIONS.getOrDefault(intValue, NONE);
	}

	/**
	 * String constants to use as a discriminator
	 */
	public static class Values {

		public static final String NONE = "0";

		public static final String READ = "1";

		public static final String WRITE = "2";

		public static final String DELETE = "3";


	}

}
