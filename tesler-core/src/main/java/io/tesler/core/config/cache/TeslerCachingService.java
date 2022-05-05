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

import static io.tesler.core.config.cache.CacheConfig.REQUEST_CACHE;
import static io.tesler.core.config.cache.CacheConfig.TESLER_CACHE_RESOLVER;
import static io.tesler.core.config.cache.CacheConfig.UI_CACHE;
import static io.tesler.core.config.cache.CacheConfig.USER_CACHE;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheResolver = TESLER_CACHE_RESOLVER)
public class TeslerCachingService {

	@CacheEvict(cacheNames = UI_CACHE, allEntries = true)
	public void evictUiCache() {

	}

	@CacheEvict(cacheNames = USER_CACHE, allEntries = true)
	public void evictUserCache() {

	}

	@CacheEvict(cacheNames = REQUEST_CACHE, allEntries = true)
	public void evictRequestCache() {

	}

}
