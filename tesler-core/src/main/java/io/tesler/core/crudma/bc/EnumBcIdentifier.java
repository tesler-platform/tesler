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

package io.tesler.core.crudma.bc;

import io.tesler.api.util.MapUtils;
import io.tesler.core.crudma.bc.impl.BcDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;


public interface EnumBcIdentifier extends BcIdentifier {

	BcDescription getBcDescription();

	@Override
	default String getName() {
		return getBcDescription().getName();
	}

	@Override
	default String getParentName() {
		return getBcDescription().getParentName();
	}

	default boolean isBc(BcIdentifier other) {
		if (other == null) {
			return false;
		}
		return new EqualsBuilder()
				.append(getName(), other.getName())
				.append(getParentName(), other.getParentName())
				.isEquals();
	}

	default boolean isNotBc(BcIdentifier other) {
		return !isBc(other);
	}

	default BcDescription buildDescription(String parentName, Class<?> serviceClass, boolean refresh) {
		return BcDescriptionBuilder.build(((Enum) this).name(), parentName, serviceClass, refresh);
	}

	class Holder<T extends Enum<T> & EnumBcIdentifier> {

		private final Map<String, T> associations;

		public Holder(Class<T> type) {
			associations = MapUtils.of(type, T::name);
		}

		public T get(String bcName) {
			return associations.get(bcName);
		}

		public T get(BcIdentifier bcIdentifier) {
			return get(bcIdentifier.getName());
		}

		public List<String> getAllBc() {
			return new ArrayList<>(associations.keySet());
		}

	}

}
