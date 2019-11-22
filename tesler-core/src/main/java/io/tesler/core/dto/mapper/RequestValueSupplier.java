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
import io.tesler.model.core.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class RequestValueSupplier<E extends BaseEntity, D extends DataResponseDTO, V> {

	private final KeySupplier<E, D> keySupplier;

	private final ValueSupplier<E, D, V> valueSupplier;

	public RequestValueSupplier(ValueSupplier<E, D, V> valueSupplier) {
		this((mapping, entity) -> null, valueSupplier);
	}

}
