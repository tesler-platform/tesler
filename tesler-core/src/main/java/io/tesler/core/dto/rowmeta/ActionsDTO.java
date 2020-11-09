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

package io.tesler.core.dto.rowmeta;

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.api.data.dto.rowmeta.ActionDTOListSerializer;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.service.action.ActionDescription;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.tesler.core.service.action.ActionIconSpecifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;


public class ActionsDTO implements Iterable<ActionDTO> {

	@Getter(onMethod_ = {@JsonValue, @JsonSerialize(using = ActionDTOListSerializer.class)})
	private List<ActionDTO> actions = new ArrayList<>();

	public ActionsDTO addMethod(ActionDescription actionDescription, BusinessComponent bc) {
		return addMethod(actions.size(), actionDescription, bc);
	}

	public ActionsDTO addMethod(int position, ActionDescription actionDescription, BusinessComponent bc) {
		actions.add(position, actionDescription.toDto(bc));
		return this;
	}

	public ActionsDTO addGroup(String type, String label, int maxGroupVisualButtonsCount, List<ActionDTO> actionDtos, ActionIconSpecifier icon, boolean showOnlyIcon) {
		actions.add(new ActionDTO(type, label, maxGroupVisualButtonsCount, actionDtos, icon.getActionIconCode(), showOnlyIcon));
		return this;
	}

	public void addAll(List<ActionDTO> actions) {
		this.actions.addAll(actions);
	}

	@Override
	public Iterator<ActionDTO> iterator() {
		return actions.iterator();
	}

}
