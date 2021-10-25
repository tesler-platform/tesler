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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class TeslerCacheResolver implements CacheResolver {

	private final CacheManager teslerCachesManager;

	@NotNull
	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		Set<String> cacheNames = context.getOperation().getCacheNames();
		if (cacheNames == null) {
			return Collections.emptyList();
		}
		List<Cache> result = new ArrayList<>(cacheNames.size());
		cacheNames.forEach(cacheName -> {
					Cache cache = teslerCachesManager.getCache(cacheName);
					if (cache == null) {
						log.warn(("Cannot find cache named '" + cacheName + "' for " + context.getOperation()));
					} else {
						result.add(cache);
					}
				});
		return result;
	}

}
