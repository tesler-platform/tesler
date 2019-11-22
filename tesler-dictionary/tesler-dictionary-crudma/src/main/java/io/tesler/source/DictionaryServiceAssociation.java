/*-
 * #%L
 * IO Tesler - Dictionary Crudma
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

package io.tesler.source;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.impl.inner.TranslationCrudmaService;
import io.tesler.source.services.data.AudDictionaryService;
import io.tesler.source.services.data.DictionaryItemService;
import io.tesler.source.services.data.DictionaryTypeDescService;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Component;

@Getter
public enum DictionaryServiceAssociation implements EnumBcIdentifier {

	// @formatter:off

	adminDictionaryType(DictionaryTypeDescService.class),
		adminDictionaryItem(adminDictionaryType, DictionaryItemService.class),
			adminDictionaryItemTranslation(adminDictionaryItem, TranslationCrudmaService.class),
			adminDictionaryItemHistory(adminDictionaryItem, AudDictionaryService.class)
	;
	// @formatter:on

	public static final Holder<DictionaryServiceAssociation> Holder = new Holder<>(DictionaryServiceAssociation.class);

	private final BcDescription bcDescription;

	DictionaryServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	DictionaryServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	DictionaryServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	DictionaryServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	DictionaryServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	DictionaryServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Override
	public String getName() {
		return bcDescription.getName();
	}

	@Override
	public String getParentName() {
		return bcDescription.getParentName();
	}

	public boolean isBc(BcIdentifier other) {
		if (other == null) {
			return false;
		}
		return new EqualsBuilder()
				.append(getName(), other.getName())
				.append(getParentName(), other.getParentName())
				.isEquals();
	}

	public boolean isNotBc(BcIdentifier other) {
		return !isBc(other);
	}

	@Component
	public static class DictionaryBcSupplier extends AbstractEnumBcSupplier<DictionaryServiceAssociation> {

		public DictionaryBcSupplier() {
			super(DictionaryServiceAssociation.Holder);
		}

	}

}
