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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@RequiredArgsConstructor
public abstract class DtoConstructor<E extends BaseEntity, D extends DataResponseDTO> {

	private final Class<E> entityClass;

	private final Class<D> dtoClass;

	private final LazyInitializer<Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>>> valueSuppliers = new LazyInitializer<Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>>>() {
		@Override
		protected Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>> initialize() {
			return buildValueSuppliers();
		}
	};

	@Autowired
	private SessionService sessionService;

	protected RequestValueSupplier<BaseEntity, DataResponseDTO, User> currentUser = new RequestValueSupplier<>(
			(mapping, entity) -> sessionService.getSessionUser()
	);

	protected RequestValueSupplier<BaseEntity, DataResponseDTO, LOV> currentUserRole = new RequestValueSupplier<>(
			(mapping, entity) -> sessionService.getSessionUserRole()
	);

	protected abstract Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>> buildValueSuppliers();

	@SneakyThrows
	public Map<DtoField<? super D, ?>, ValueSupplier<? super E, ? super D, ?>> getValueSuppliers() {
		return valueSuppliers.get();
	}

}
