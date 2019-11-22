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

import io.tesler.core.crudma.bc.BcSupplier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier.Holder;
import java.util.List;


public abstract class AbstractEnumBcSupplier<T extends Enum<T> & EnumBcIdentifier> implements BcSupplier {

	private final Holder<T> holder;

	public AbstractEnumBcSupplier(Class<T> type) {
		holder = new Holder<>(type);
	}

	public AbstractEnumBcSupplier(Holder<T> holder) {
		this.holder = holder;
	}

	@Override
	public List<String> getAllBcNames() {
		return holder.getAllBc();
	}

	@Override
	public BcDescription getBcDescription(String bcName) {
		T serviceAssociation = holder.get(bcName);
		if (serviceAssociation != null) {
			return serviceAssociation.getBcDescription();
		}
		return null;
	}

}
