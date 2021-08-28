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

package io.tesler.engine.workflow.services;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.Project;
import io.tesler.model.workflow.entity.*;
import org.pf4j.ExtensionPoint;

import java.math.BigDecimal;
import java.util.List;


public interface WorkflowDao extends ExtensionPoint {

	/**
	 * Returns the current step for the specified activity.
	 *
	 * @param task task
	 * @return current step of task
	 */
	WorkflowStep getCurrentStep(WorkflowableTask task);

	/**
	 * Returns a step with the HIDDEN status for the specified version of the transition model.
	 *
	 * @param version version of the transition model
	 * @return step with the HIDDEN status
	 */
	WorkflowStep getHiddenStep(WorkflowVersion version);

	/**
	 * Returns the initial step for the transition model corresponding to the specified project and task type.
	 *
	 * @param project the project
	 * @param taskType task type
	 * @return initial step of the transition model
	 */
	WorkflowStep getInitialStep(Project project, LOV taskType);

	/**
	 * * Checks whether the specified step is the initial step for its transition model.
	 *
	 * @param step step
	 * @return whether the specified step is the initial one
	 */
	boolean isInitialStep(WorkflowStep step);

	/**
	 * Returns the step of the specified version of the transition model by its unique name.
	 *
	 * @param version version of the transition model
	 * @param name the unique name of the step
	 * @return step of the specified version of the transition model
	 */
	WorkflowStep getStepByName(WorkflowVersion version, String name);

	/**
	 * Returns all transitions of the specified transition model whose unique names are equal to the specified one.
	 *
	 * @param workflow transition model
	 * @param name unique transition name
	 * @return list of transitions for the specified transition model
	 */
	List<WorkflowTransition> getTransitionsByName(Workflow workflow, String name);

	/**
	 * Returns the transition of the specified version of the transition model by its unique name.
	 *
	 * @param version version of the transition model
	 * @param name unique transition name
	 * @return transition of the specified version of the transition model
	 */
	WorkflowTransition getTransitionByName(WorkflowVersion version, String name);

	/**
	 * Returns a transition from the latest version of the transition model by its unique name.
	 *
	 * @param name unique transition name
	 * @return transition of the latest version of the transition model
	 */
	WorkflowTransition getLastWorkflowTransitionByName(String name);

	/**
	 * Returns a transition from the active version of the transition model by its unique name.
	 *
	 * @param name unique name of the transition
	 * @return transition of the active version of the transition model
	 */
	WorkflowTransition getActiveWorkflowTransitionByName(String name);

	/**
	 * Returns the transition of the active transition model corresponding to the specified task type between steps with the specified statuses.
	 *
	 * @param taskType type of task
	 * @param sourceStepTaskStatus initial step status
	 * @param destinationStepTaskStatus the status of the final step
	 * @return transition of the transition model
	 */
	WorkflowTransition getTransition(LOV taskType, LOV sourceStepTaskStatus, LOV destinationStepTaskStatus);

	/**
	 * Returns the last transition history record for the specified task corresponding to the transition at the specified step.
	 *
	 * @param task task
	 * @param destinationStep destination step of the transition
	 * @return the last transition history record
	 */
	WorkflowTransitionHistory getLastTransitionHistoryByDestinationStep(WorkflowTask task,
			WorkflowStep destinationStep);

	/**
	 * Returns the last transition history record for the specified task.
	 *
	 * @param task task
	 * @return the last entry in the transition history
	 */
	WorkflowTransitionHistory getLastTransitionHistory(WorkflowTask task);

	/**
	 * Creates default post-functions for the specified group of transition conditions.
	 *
	 * @param transitionConditionGroup transition condition group
	 */
	void createDefaultPostFunctions(WorkflowTransitionConditionGroup transitionConditionGroup);

	/**
	 * Deletes the specified post-function with all child entities.
	 *
	 * @param postFunction post function
	 */
	void deletePostFunction(WorkflowPostFunction postFunction);

	/**
	 * Deletes the specified transition condition group with all child entities.
	 *
	 * @param transitionConditionGroup transition condition group
	 */
	void deleteTransitionConditionGroup(WorkflowTransitionConditionGroup transitionConditionGroup);

	/**
	 * Returns the version of the transition model for the specified activity.
	 *
	 * @param task activity
	 * @return version of the transition model for the specified activity
	 */
	WorkflowVersion getWorkflowVersion(WorkflowableTask task);

	/**
	 * Returns a list of TASK_TYPE types that do not have transition models created in the specified project.
	 *
	 * @param project the project
	 * @return list of TASK_TYPE types for which transition models were not created in the specified project
	 */
	List<LOV> getTaskTypesNotInWf(Project project);

	/**
	 * Returns the maximum version number for the specified transition model.
	 *
	 * @param workflow transition model
	 * @return maximum version number for the specified transition model
	 */
	BigDecimal getMaxVersion(Workflow workflow);

	/**
	 * Returns the next version number for the specified transition model.
	 *
	 * @param workflow transition model
	 * @param majorVersion should there be a next major version
	 * @return the next version number for the specified transition model
	 */
	BigDecimal getNextVersion(Workflow workflow, boolean majorVersion);

	/**
	 * Get workflow step, assigned on workflow task
	 *
	 * @return workflow step
	 */
	WorkflowStep getWorkflowStep(WorkflowTask workflowTask);

	/**
	 * Sets workflow step to workflow task
	 */
	void setWorkflowStep(WorkflowTask workflowTask, WorkflowStep workflowStep);

	/**
	 * @param sourceStep      source step
	 * @param destinationStep destination step
	 * @return transition with specified source and destination step
	 */
	WorkflowTransition getTransitionBetweenSteps(final WorkflowStep sourceStep, final WorkflowStep destinationStep);

	/**
	 * @param sourceStep source step
	 * @return list of transitions for current step
	 */
	List<WorkflowTransition> getTransitions(final WorkflowStep sourceStep);


	/**
	 * @param conditionClass class of entity related to WF_COND table specified in WorkflowSettings
	 * @param conditionGroup condition group of conditions
	 * @return list of conditions for a given WorkflowTransitionConditionGroup from workflow cache
	 */
	<C extends WorkflowCondition> List<C> getConditions(final Class<C> conditionClass,
			final WorkflowTransitionConditionGroup conditionGroup);

}
