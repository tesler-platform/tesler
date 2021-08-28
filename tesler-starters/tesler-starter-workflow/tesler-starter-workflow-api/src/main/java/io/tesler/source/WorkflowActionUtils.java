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

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.service.action.ActionScope;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@UtilityClass
public class WorkflowActionUtils {

	private final String WF_TRANSITION_ID = "wf_";

	private final TransitionActionGroup WITHOUT_GROUP = new TransitionActionGroup(null, null, null, null);

	public boolean isTransitionAction(final String actionName) {
		return actionName.startsWith(WF_TRANSITION_ID);
	}

	public String actionNameFromTransitionName(final WorkflowTransition transition) {
		return WF_TRANSITION_ID + transition.getName();
	}

	public String transitionNameFromActionName(final String actionName) {
		return actionName.replace(WF_TRANSITION_ID, "");
	}

	public List<ActionDTO> getActions(final List<WorkflowTransition> transitions) {
		final Map<TransitionActionGroup, List<WorkflowTransition>> transitionGroup = transitions.stream()
				.collect(groupingBy(
						transition -> ofNullable(transition.getWorkflowTransitionGroup()).map(TransitionActionGroup::new)
								.orElse(WITHOUT_GROUP),
						mapping(Function.identity(), Collectors.toList())
				));
		List<TransitionAction> result = new ArrayList<>();
		Optional.ofNullable(transitionGroup.remove(WITHOUT_GROUP)).ifPresent(workflowTransitions -> workflowTransitions
				.forEach(transition -> result.add(new TransitionAction(transition))));
		transitionGroup.forEach((key, value) -> result.add(new TransitionAction(key, value)));
		return result.stream()
				.sorted(comparing(TransitionAction::getSeq, nullsLast(naturalOrder())))
				.map(TransitionAction::getActionDTO).collect(Collectors.toList());
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private static class TransitionAction {

		private final Long seq;

		private final ActionDTO actionDTO;

		private TransitionAction(final WorkflowTransition workflowTransition) {
			actionDTO = new ActionDTO(actionNameFromTransitionName(workflowTransition), workflowTransition.getText());
			actionDTO.setAvailable(true);
			actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
			actionDTO.setIcon(workflowTransition.getIconCode());
			seq = workflowTransition.getSeq();
		}

		private TransitionAction(TransitionActionGroup transitionActionGroup,
				List<WorkflowTransition> workflowTransitions) {
			List<TransitionAction> groupedActions = new ArrayList<>();
			workflowTransitions.forEach(transition -> groupedActions.add(new TransitionAction(transition)));
			List<ActionDTO> actionDTOS = groupedActions.stream()
					.sorted(comparing(TransitionAction::getSeq, nullsLast(naturalOrder())))
					.map(TransitionAction::getActionDTO).collect(Collectors.toList());
			actionDTO = new ActionDTO(
					transitionActionGroup.getType(),
					transitionActionGroup.getText(),
					transitionActionGroup.getMaxGroupVisualButtonsCount(),
					actionDTOS,
					null,
					false
			);
			actionDTO.setAvailable(true);
			actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
			seq = transitionActionGroup.getSeq();
		}

	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private static class TransitionActionGroup {

		private final String type;

		private final String text;

		private final Integer maxGroupVisualButtonsCount;

		private final Long seq;

		private TransitionActionGroup(final WorkflowTransitionGroup transitionGroup) {
			this(
					transitionGroup.getNameButtonYet(),
					transitionGroup.getDescription(),
					transitionGroup.getMaxShowButtonsInGroup(),
					transitionGroup.getSeq()
			);
		}

	}

}
