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

package io.tesler.core.crudma.state;

import io.tesler.core.crudma.bc.BusinessComponent;

/**
 * Tesler component that allows change state of current Business component between http requests.
 * Used for share changes between readOnly requests (type of requests, when no changes was stored in persistence layer (e.g. database))
 *
 * @see BcState
 * @see BusinessComponent
 * @see io.tesler.core.crudma.CrudmaGateway
 */
public interface BcStateAware {

	/**
	 * Obtain state for business component instance
	 * @param bc is Business component state key (name and record identifier)
	 * @return a state for current business component and current client
	 */
	BcState getState(BusinessComponent bc);

	/**
	 * Clear all state records for current client
	 */
	void clear();

	/**
	 * Change state for business component instance
	 * @param bc is Business component state key (name and record identifier)
	 * @param state is BCState that must be changed
	 */
	void set(BusinessComponent bc, BcState state);

	/**
	 * Obtain information if this record was already persisted.
	 * This method is necessary to determine whether to create a record from current state.
	 * @param bc is Business component state key (name and record identifier)
	 */
	boolean isPersisted(BusinessComponent bc);

}
