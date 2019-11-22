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

@FunctionalInterface
public interface ActionAvailableChecker {

	ActionAvailableChecker NOT_NULL_ID = (bc) -> bc.getId() != null;

	ActionAvailableChecker NOT_NULL_PARENT_ID = (bc) -> bc.getParentName() == null || bc.getParentId() != null;

	ActionAvailableChecker ALWAYS_TRUE = (bc) -> true;

	ActionAvailableChecker ALWAYS_FALSE = (bc) -> false;

	static ActionAvailableChecker and(ActionAvailableChecker... checkers) {
		return new And(checkers);
	}

	boolean isAvailable(BusinessComponent bc);

	class And implements ActionAvailableChecker {

		private final ActionAvailableChecker[] availableCheckers;

		public And(final ActionAvailableChecker... availableCheckers) {
			this.availableCheckers = availableCheckers;
		}

		@Override
		public boolean isAvailable(final BusinessComponent bc) {
			for (final ActionAvailableChecker availableChecker : availableCheckers) {
				if (!availableChecker.isAvailable(bc)) {
					return false;
				}
			}
			return true;
		}

	}

}
