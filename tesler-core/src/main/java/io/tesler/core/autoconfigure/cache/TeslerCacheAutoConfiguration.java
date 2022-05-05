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

package io.tesler.core.autoconfigure.cache;

import com.google.common.collect.ImmutableList;
import io.tesler.core.autoconfigure.AutoConfiguration;
import io.tesler.core.config.cache.CacheConfig;
import io.tesler.core.config.cache.CacheManagerBasedCacheResolver;
import io.tesler.core.config.cache.TeslerCaches;
import io.tesler.core.config.cache.TeslerRequestAwareCacheHolder;
import io.tesler.core.metahotreload.MetaHotReloadService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * <p>Auto-configuration class which declares "teslerCacheResolver" bean of type CacheResolver.</p>
 * <p>As it says in the Spring documentation:</p>
 * <p><strong>Auto-configuration is non-invasive. At any point, you can start to define your own configuration to replace specific parts of the auto-configuration.</strong></p>
 * <p>It follows that if you declare a bean with name "teslerCacheResolver" and type {@link CacheResolver CacheResolver} in a project, which defines dependency on Tesler, this autoconfiguration will be ignored, otherwise it will create the "teslerCacheResolver" bean {@link #teslerCacheResolver defined below}.
 * A bean named "teslerCacheResolver" is used by Tesler in all annotations which are related to Spring cache abstraction.</p>
 * <p>{@link AutoConfiguration @AutoConfiguration} is nothing but alias for {@link org.springframework.context.annotation.Configuration @Configuration},
 * it is used to exclude a class from component scanning (see {@link io.tesler.core.config.BeanScan @BeanScan}).</p>
 *
 * @see CacheManagerBasedCacheResolver
 */
@AutoConfiguration
@ConditionalOnClass({CacheManager.class})
@ConditionalOnMissingBean(name = "teslerCacheResolver")
@ConditionalOnBean({CacheAspectSupport.class})
@AutoConfigureAfter(CacheAutoConfiguration.class)
@RequiredArgsConstructor
public class TeslerCacheAutoConfiguration {

	private final ApplicationContext applicationContext;

	private final CacheProperties cacheProperties;

	@Bean
	public CacheResolver teslerCacheResolver(MetaHotReloadService metaHotReloadService) {
		metaHotReloadService.loadMeta();
		if (CacheType.NONE.equals(cacheProperties.getType())) {
			return new CacheManagerBasedCacheResolver(new NoOpCacheManager());
		}
		CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
		compositeCacheManager.setCacheManagers(buildCacheManagers());
		compositeCacheManager.setFallbackToNoOpCache(true);
		return new CacheManagerBasedCacheResolver(compositeCacheManager);
	}

	@Bean
	@RequestScope
	public TeslerRequestAwareCacheHolder requestCache() {
		return new TeslerRequestAwareCacheHolder(new ConcurrentMapCache(CacheConfig.REQUEST_CACHE));
	}

	protected List<CacheManager> buildCacheManagers() {
		List<CacheManager> result = new ArrayList<>();
		result.add(buildUnExpirableCacheManager(
				TeslerCaches.getSimpleCacheNames().toArray(new String[0])
		));
		result.add(buildRequestAwareCacheManager(TeslerCaches.getRequestCacheName()));
		return result;
	}

	protected CacheManager buildUnExpirableCacheManager(String... names) {
		return new ConcurrentMapCacheManager(names);
	}

	protected CacheManager buildRequestAwareCacheManager(String cacheName) {
		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
		simpleCacheManager.setCaches(
				ImmutableList.of(new RequestAwareCacheDecorator(cacheName))
		);
		simpleCacheManager.initializeCaches();
		return simpleCacheManager;
	}

	class RequestAwareCacheDecorator implements Cache {

		private final String name;

		private final NoOpCache noOpCache;

		RequestAwareCacheDecorator(String name) {
			this.name = name;
			this.noOpCache = new NoOpCache(name);
		}

		@NotNull
		@Override
		public String getName() {
			return name;
		}

		@NotNull
		@Override
		public Object getNativeCache() {
			return getDelegate().getNativeCache();
		}

		@Override
		public ValueWrapper get(@NotNull Object key) {
			return getDelegate().get(key);
		}

		@Override
		public <T> T get(@NotNull Object key, Class<T> type) {
			return getDelegate().get(key, type);
		}

		@Override
		public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
			return getDelegate().get(key, valueLoader);
		}

		@Override
		public void put(@NotNull Object key, Object value) {
			getDelegate().put(key, value);
		}

		@Override
		public ValueWrapper putIfAbsent(@NotNull Object key, Object value) {
			return getDelegate().putIfAbsent(key, value);
		}

		@Override
		public void evict(@NotNull Object key) {
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
			return applicationContext.getBean(name, TeslerRequestAwareCacheHolder.class).getCache();
		}

	}

}
