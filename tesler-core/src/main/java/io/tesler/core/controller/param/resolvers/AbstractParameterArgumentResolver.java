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

package io.tesler.core.controller.param.resolvers;

import java.lang.reflect.Array;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;


public abstract class AbstractParameterArgumentResolver implements HandlerMethodArgumentResolver {

	protected static String getParameterValue(Object object) {
		Object value = object;
		if (value != null && value.getClass().isArray()) {
			if (Array.getLength(value) == 0) {
				return null;
			}
			value = Array.get(value, 0);
		}

		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}


}
