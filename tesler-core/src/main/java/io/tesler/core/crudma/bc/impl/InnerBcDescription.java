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

package io.tesler.core.crudma.bc.impl;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.impl.inner.InnerCrudmaService;
import io.tesler.core.service.ResponseService;
import io.tesler.model.core.entity.BaseEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Slf4j
public final class InnerBcDescription extends BcDescription {

	private final Class<? extends ResponseService> serviceClass;

	private final Class<? extends BaseEntity> entity;

	private  final Class<? extends DataResponseDTO> dto;

	public InnerBcDescription(String name, String parentName,
			Class<? extends ResponseService> serviceClass, boolean refresh) {
		super(name, parentName, InnerCrudmaService.class, refresh);
		this.serviceClass = serviceClass;
		if (this.serviceClass != null) {
			Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(serviceClass, ResponseService.class);
			Type[] types = Stream.of(ResponseService.class.getTypeParameters())
					.map(typeArguments::get)
					.toArray(Type[]::new);
			this.entity = (Class<? extends BaseEntity>) types[1];
			this.dto = (Class<? extends DataResponseDTO>) types[0];
		} else {
			this.entity = null;
			this.dto = null;
			log.warn("{}({}) is not configured with a service class", this.getName(), this.getClass().getSimpleName());
		}
	}

}
