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

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BusinessComponent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public abstract class BcDisabler {

	public abstract Collection<BcIdentifier> getSupportedBc();

	public abstract boolean isBcDisabled(BusinessComponent bc);

	public final void disableActions(final Iterable<ActionDTO> actionDTO) {
		Iterator<ActionDTO> actionDTOIterator = actionDTO.iterator();
		while (actionDTOIterator.hasNext()) {
			final ActionDTO next = actionDTOIterator.next();
			final List<ActionDTO> actions = next.getActions();
			if (actions != null) {
				disableActions(actions);
			} else if (isActionDisabled(next.getType())) {
				actionDTOIterator.remove();
			}
		}
	}

	protected abstract boolean isActionDisabled(String actionName);

}
