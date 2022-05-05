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

package io.tesler.core.config.cache;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TeslerCaches {

	public static List<String> getSimpleCacheNames() {
		return Arrays.stream(Caches.values())
				.filter(cacheSpec -> !cacheSpec.isRequestScope())
				.map(Caches::name)
				.collect(Collectors.toList());
	}

	public static String getRequestCacheName() {
		return Caches.requestCache.name();
	}


	@Getter
	public enum Caches {
		linkedDictionaryRules,
		specifications,
		widgetcache,
		notificationSettings,
		workflow,
		requestCache(true),
		userCache;

		private final boolean requestScope;

		Caches(boolean requestScope) {
			this.requestScope = requestScope;
		}

		Caches() {
			this.requestScope = false;
		}
	}

}
