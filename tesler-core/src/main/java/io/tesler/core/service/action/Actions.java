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

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.ActionsDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Actions<T extends DataResponseDTO> {

	final List<ActionDescription<T>> actionDefinitions;

	final List<ActionGroupDescription<T>> actionGroupDefinitions;

	public static <T extends DataResponseDTO> ActionsBuilder<T> builder() {
		return new ActionsBuilder<>();
	}

	public ActionDescription<T> getAction(String key) {
		List<ActionDescription<T>> allActions = new ArrayList<>(actionDefinitions);
		actionGroupDefinitions.forEach(group -> allActions.addAll(group.getActions()));
		return allActions.stream()
				.filter(actionDescription -> Objects.equals(key, actionDescription.getKey()))
				.findFirst().orElse(null);
	}

	public ActionsDTO toDto(BusinessComponent bc) {
		ActionsDTO result = new ActionsDTO();
		actionDefinitions.forEach(actionDescription -> result.addMethod(actionDescription, bc));

		for (ActionGroupDescription<T> group : actionGroupDefinitions) {
			List<ActionDTO> groupActionDtos = new ArrayList<>();
			List<ActionDescription<T>> groupActions = group.getActions();
			groupActions.forEach(actionDescription -> groupActionDtos.add(actionDescription.toDto(bc)));
			result.addGroup(group.getKey(), group.getText(), group.getMaxGroupVisualButtonsCount(), groupActionDtos, group.getIconCode(), group.isShowOnlyIcon());
		}
		return result;
	}

}
