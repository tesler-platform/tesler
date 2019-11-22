/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.util;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MapUtils {

	public static <E extends Enum<E>, K> Map<K, E> of(Class<E> enumClass, Function<? super E, ? extends K> keyMapper) {
		return EnumSet.allOf(enumClass).stream().collect(Collectors.toMap(keyMapper, Function.identity()));
	}

	public static <C, K> Map<K, C> of(Collection<C> collection, Function<? super C, ? extends K> keyMapper) {
		return collection.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
	}

}
