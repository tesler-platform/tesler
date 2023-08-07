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

package io.tesler.core.service.action;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;

@FunctionalInterface
public interface ActionAvailableChecker<D extends BcDescription> {

	ActionAvailableChecker NOT_NULL_ID = (bc) -> bc.getId() != null;

	ActionAvailableChecker NOT_NULL_PARENT_ID = (bc) -> bc.getParentName() == null || bc.getParentId() != null;

	ActionAvailableChecker ALWAYS_TRUE = (bc) -> true;

	ActionAvailableChecker ALWAYS_FALSE = (bc) -> false;

	@SafeVarargs
	static <T extends BcDescription> ActionAvailableChecker<T> and(ActionAvailableChecker<T>... checkers) {
		return new And<>(checkers);
	}

	boolean isAvailable(BusinessComponent<D> bc);

	class And<D extends BcDescription> implements ActionAvailableChecker<D> {

		private final ActionAvailableChecker<D>[] availableCheckers;

		@SafeVarargs
		public And(final ActionAvailableChecker<D>... availableCheckers) {
			this.availableCheckers = availableCheckers;
		}

		@Override
		public boolean isAvailable(final BusinessComponent<D> bc) {
			for (final ActionAvailableChecker<D> availableChecker : availableCheckers) {
				if (!availableChecker.isAvailable(bc)) {
					return false;
				}
			}
			return true;
		}

	}

}
