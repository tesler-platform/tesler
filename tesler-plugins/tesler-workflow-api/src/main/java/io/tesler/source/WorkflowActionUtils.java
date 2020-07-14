/*-
 * #%L
 * IO Tesler - Workflow API
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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.service.action.ActionScope;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.ListUtils;

@UtilityClass
public final class WorkflowActionUtils {

	private static final String WF_TRANSITION_ID = "wf_transition_id_";

	private static final ActionGroup WITHOUT_GROUP = new ActionGroup(null, null, null);

	public static String actionNameFromTransitionId(final Long transitionId) {
		return WF_TRANSITION_ID + transitionId;
	}

	public static Long transitionIdFromActionName(final String actionName) {
		return Long.valueOf(actionName.replace(WF_TRANSITION_ID, ""));
	}

	public static boolean isTransitionAction(final String actionName) {
		return actionName.startsWith(WF_TRANSITION_ID);
	}

	public static List<ActionDTO> getActions(final List<WorkflowTransition> transitions) {
		final Map<ActionGroup, List<WorkflowTransition>> transitionGroup = transitions.stream().collect(groupingBy(
				transition -> ofNullable(transition.getWorkflowTransitionGroup()).map(ActionGroup::new).orElse(WITHOUT_GROUP),
				mapping(Function.identity(), Collectors.toList())
		));
		final List<ActionDTO> actionsWithoutGroup = getActionsWithoutGroup(transitionGroup.remove(WITHOUT_GROUP));
		return ListUtils.union(getActionWithGroup(transitionGroup), actionsWithoutGroup);
	}

	private static List<ActionDTO> getActionWithGroup(final Map<ActionGroup, List<WorkflowTransition>> transitionGroup) {
		return transitionGroup.entrySet().stream()
				.map(entry -> {
					ActionDTO actionDTO = new ActionDTO(
							entry.getKey().getText(),
							entry.getKey().getText(),
							entry.getKey().getMaxGroupVisualButtonsCount(),
							getActionsWithoutGroup(entry.getValue())
					);
					actionDTO.setAvailable(true);
					actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
					return actionDTO;
				})
				.collect(Collectors.toList());
	}

	private static List<ActionDTO> getActionsWithoutGroup(final List<WorkflowTransition> transitions) {
		if (transitions != null) {
			return transitions.stream()
					.map(transition -> {
						ActionDTO actionDTO = new ActionDTO(actionNameFromTransitionId(transition.getId()), transition.getName());
						actionDTO.setAvailable(true);
						actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
						return actionDTO;
					})
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private static class ActionGroup {

		private final String type;

		private final String text;

		private final Integer maxGroupVisualButtonsCount;

		private ActionGroup(final WorkflowTransitionGroup transitionGroup) {
			this(
					transitionGroup.getNameButtonYet(),
					transitionGroup.getDescription(),
					transitionGroup.getMaxShowButtonsInGroup()
			);
		}

	}

}
