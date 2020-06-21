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

package io.tesler.core.ui.cache;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import io.tesler.core.dto.DTOUtils;
import java.util.Set;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public final class DtoFieldCache {

	public LoadingCache<Class<? extends DataResponseDTO>, Set<DtoField<DataResponseDTO, ?>>> dtoFieldsCache = CacheBuilder
			.newBuilder()
			.build(new DtoFieldCacheLoader());

	private final class DtoFieldCacheLoader<D extends DataResponseDTO> extends
			CacheLoader<Class<D>, Set<DtoField<D, ?>>> {

		@Override
		@SneakyThrows
		public Set<DtoField<D, ?>> load(final Class<D> dtoClass) {
			return DTOUtils.getAllFields(dtoClass);
		}

	}

}
