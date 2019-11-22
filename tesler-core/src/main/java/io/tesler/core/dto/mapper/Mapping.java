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
import io.tesler.constgen.DtoField;
import io.tesler.model.core.entity.BaseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Mapping<E extends BaseEntity, D extends DataResponseDTO> {

	private final Map<ValueSupplier<? super E, ? super D, ?>, Optional<?>> cache = new HashMap<>();

	private final Map<String, Object> attributes = new HashMap<>();

	private final RequestValueCache requestCache;

	private final Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>> valueSuppliers;

	private final E entity;

	@SuppressWarnings({"OptionalAssignedToNull", "unchecked"})
	public <V> Optional<V> get(final ValueSupplier<? super E, ? super D, V> valueSupplier) {
		Optional<?> value = cache.get(valueSupplier);
		if (value == null) {
			value = Optional.ofNullable(valueSupplier.get(this, entity));
			cache.put(valueSupplier, value);
		}
		return (Optional<V>) value;
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> get(final DtoField<? super D, ? super V> field) {
		final ValueSupplier<? super E, ? super D, ?> valueSupplier = valueSuppliers.get(field);
		if (valueSupplier == null) {
			return Optional.empty();
		}
		return (Optional<V>) get(valueSupplier);
	}

	public <V> Optional<V> get(final RequestValueSupplier<? super E, ? super D, V> valueSupplier) {
		final Object key = valueSupplier.getKeySupplier().get(this, entity);
		return requestCache.computeIfAbsent(valueSupplier, key, () -> valueSupplier.getValueSupplier().get(this, entity));
	}

	@SuppressWarnings({"unused", "unchecked"})
	public <V> Optional<V> getAttribute(final String name) {
		return (Optional<V>) Optional.ofNullable(attributes.get(name));
	}

	void addAttribute(final String name, final Object value) {
		attributes.put(name, value);
	}

}
