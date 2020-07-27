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

package io.tesler.core.service;

import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.data.HistoricityDto;
import io.tesler.model.core.entity.HistoricityEntity;
import java.util.Arrays;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Getter;

@Getter
public abstract class HistoricityKey<E extends HistoricityEntity, D extends HistoricityDto> {

	private final List<KeyAttribute<E, D, ?>> attributes;

	/**
	 * @param attributes key attributes
	 */
	@SafeVarargs
	public HistoricityKey(final KeyAttribute<E, D, ?>... attributes) {
		this.attributes = Arrays.asList(attributes);
	}

	public interface AttrValueSupplier<T> {

		T get(BusinessComponent bc);

	}

	@Getter
	public static class KeyAttribute<E, D, T> {

		private final SingularAttribute<E, T> attribute;

		private final AttrValueSupplier<T> valueSupplier;

		private final DtoField<D, ?>[] dtoFields;

		/**
		 * @param attribute attribute
		 * @param valueSupplier function to get attribute value
		 * @param dtoFields dto fields, which changing will change the attribute value
		 */
		@SafeVarargs
		public KeyAttribute(
				final SingularAttribute<E, T> attribute,
				final AttrValueSupplier<T> valueSupplier,
				final DtoField<D, ?>... dtoFields) {
			this.attribute = attribute;
			this.valueSupplier = valueSupplier;
			this.dtoFields = dtoFields;
		}

	}

}
