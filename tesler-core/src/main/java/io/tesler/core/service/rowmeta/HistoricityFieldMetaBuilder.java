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

package io.tesler.core.service.rowmeta;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.data.HistoricityDto;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.HistoricityKey;
import io.tesler.core.service.HistoricityKey.KeyAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public abstract class HistoricityFieldMetaBuilder<T extends HistoricityDto> extends FieldMetaBuilder<T> {

	private final Class<? extends HistoricityKey<?, T>> historicityKeyClass;

	@Autowired
	protected ApplicationContext applicationContext;

	public HistoricityFieldMetaBuilder(final Class<? extends HistoricityKey<?, T>> historicityKeyClass) {
		this.historicityKeyClass = historicityKeyClass;
	}

	private HistoricityKey<?, T> getHistoricityKey() {
		return applicationContext.getBean(historicityKeyClass);
	}

	@Override
	public final void buildRowDependentMeta(final RowDependentFieldsMeta<T> fields, final BusinessComponent bc) {
		super.buildRowDependentMeta(fields, bc);
	}

	@Override
	public final void buildIndependentMeta(final FieldsMeta<T> fields, final BusinessComponent bc) {
		super.buildIndependentMeta(fields, bc);
	}

	@Override
	public final void buildExtremeRowDependentMeta(
			final RowDependentFieldsMeta<T> fields,
			final ExtremeBcDescription bcDescription,
			final Long id,
			final Long parentId) {
		super.buildExtremeRowDependentMeta(fields, bcDescription, id, parentId);
	}

	public final void buildRowDependentMeta(
			final RowDependentFieldsMeta<T> fields,
			final InnerBcDescription bcDescription,
			final Long id,
			final Long parentId) {
		dependentMeta(fields, bcDescription, id, parentId);
	}

	public final void buildIndependentMeta(
			final FieldsMeta<T> fields,
			final InnerBcDescription bcDescription,
			final Long parentId) {
		independentMeta(fields, bcDescription, parentId);
		for (final KeyAttribute<?, T, ?> attribute : getHistoricityKey().getAttributes()) {
			fields.setRequired(attribute.getDtoFields());
		}
	}

	protected abstract void dependentMeta(
			final RowDependentFieldsMeta<T> fields,
			final InnerBcDescription bcDescription,
			final Long id,
			final Long parentId);

	protected abstract void independentMeta(
			final FieldsMeta<T> fields,
			final InnerBcDescription bcDescription,
			final Long parentId);

}
