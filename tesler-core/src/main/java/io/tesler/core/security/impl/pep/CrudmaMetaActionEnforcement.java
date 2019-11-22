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

package io.tesler.core.security.impl.pep;

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.api.security.attributes.IAttributeType;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.MetaContainer;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.security.impl.AttributeTypes;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class CrudmaMetaActionEnforcement extends
		AbstractPolicyEnforcement<CrudmaAction, MetaContainer> {

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

	@Override
	public Class<MetaContainer> getResultType() {
		return MetaContainer.class;
	}

	@Override
	protected IAttributeType getAttributeType() {
		return AttributeTypes.BC_ACTION;
	}

	@Override
	public MetaContainer transform(MetaContainer result, CrudmaAction crudmaAction, List<String> allowedValues) {
		result.transformMeta(meta -> {
			if (meta instanceof MetaDTO) {
				disableActions((MetaDTO) meta, allowedValues);
			}
			return meta;
		});
		return result;
	}

	private MetaDTO disableActions(MetaDTO result, List<String> allowed) {
		removeActions(result.getRow().getActions(), allowed);
		return result;
	}

	private void removeActions(Iterable<ActionDTO> actionDTO, List<String> allowed) {
		Iterator<ActionDTO> actionDTOIterator = actionDTO.iterator();
		while (actionDTOIterator.hasNext()) {
			ActionDTO next = actionDTOIterator.next();
			List<ActionDTO> actions = next.getActions();
			if (actions != null) {
				removeActions(actions, allowed);
			} else if (allowed.contains(next.getType())) {
				actionDTOIterator.remove();
			}
		}
	}

}
