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

package io.tesler.core.dto.mapper;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.config.cache.CacheConfig;
import io.tesler.model.core.entity.BaseEntity;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RequestValueCache {

	@Cacheable(cacheResolver = CacheConfig.TESLER_CACHE_RESOLVER, 
			cacheNames = {CacheConfig.REQUEST_CACHE},
			key = "{#valueSupplier, #key}"
	)
	public <E extends BaseEntity, D extends DataResponseDTO, V> Optional<V> computeIfAbsent(
			final RequestValueSupplier<E, D, V> valueSupplier,
			final Object key,
			final Supplier<?> getter) {
		return (Optional<V>) Optional.ofNullable(getter.get());
	}

}
