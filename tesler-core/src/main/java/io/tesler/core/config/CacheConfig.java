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

package io.tesler.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;


@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	public static final String NOTIFICATION_SETTINGS = "notificationSettings";

	public static final String WORKFLOW_CACHE = "workflow";

	public static final String USER_CACHE = "userCache";

	public static final String REQUEST_CACHE = "requestCache";

	public static final String LINKED_DICTIONARY_RULES = "linkedDictionaryRules";

	public static final String SPECIFICATION_CACHE = "specifications";

	public static final String UI_CACHE = "widgetcache";

	private final ApplicationContext applicationContext;

	@Bean(name = USER_CACHE)
	public Cache userCache() {
		return new ConcurrentMapCache(USER_CACHE);
	}

	@Bean(name = REQUEST_CACHE)
	@RequestScope
	public Cache requestCache() {
		return new ConcurrentMapCache(REQUEST_CACHE);
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
		compositeCacheManager.setCacheManagers(buildCacheManagers());
		compositeCacheManager.setFallbackToNoOpCache(true);
		return compositeCacheManager;
	}

	protected List<CacheManager> buildCacheManagers() {
		List<CacheManager> result = new ArrayList<>();
		result.add(buildUnExpirableCacheManager(
				NOTIFICATION_SETTINGS,
				LINKED_DICTIONARY_RULES,
				WORKFLOW_CACHE,
				SPECIFICATION_CACHE,
				UI_CACHE
		));
		result.add(buildRequestAwareCacheManager(USER_CACHE, REQUEST_CACHE));
		return result;
	}

	protected CacheManager buildUnExpirableCacheManager(String... names) {
		return new ConcurrentMapCacheManager(names);
	}

	protected CacheManager buildRequestAwareCacheManager(String... names) {
		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
		simpleCacheManager.setCaches(Arrays.stream(names)
				.map(RequestAwareCacheDecorator::new)
				.collect(Collectors.toList())
		);
		simpleCacheManager.initializeCaches();
		return simpleCacheManager;
	}

	class RequestAwareCacheDecorator implements Cache {

		private final String name;

		private final NoOpCache noOpCache;

		private RequestAwareCacheDecorator(String name) {
			this.name = name;
			this.noOpCache = new NoOpCache(name);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getNativeCache() {
			return getDelegate().getNativeCache();
		}

		@Override
		public ValueWrapper get(Object key) {
			return getDelegate().get(key);
		}

		@Override
		public <T> T get(Object key, Class<T> type) {
			return getDelegate().get(key, type);
		}

		@Override
		public <T> T get(Object key, Callable<T> valueLoader) {
			return getDelegate().get(key, valueLoader);
		}

		@Override
		public void put(Object key, Object value) {
			getDelegate().put(key, value);
		}

		@Override
		public ValueWrapper putIfAbsent(Object key, Object value) {
			return getDelegate().putIfAbsent(key, value);
		}

		@Override
		public void evict(Object key) {
			getDelegate().evict(key);
		}

		@Override
		public void clear() {
			getDelegate().clear();
		}

		private Cache getDelegate() {
			if (RequestContextHolder.getRequestAttributes() == null) {
				return noOpCache;
			}
			return applicationContext.getBean(name, Cache.class);
		}

	}

}
