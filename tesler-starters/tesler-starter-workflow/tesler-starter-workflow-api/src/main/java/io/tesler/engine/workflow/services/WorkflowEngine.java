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


import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import org.pf4j.ExtensionPoint;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface WorkflowEngine extends ExtensionPoint {

	/**
	 * Sets the specified task to the initial step of the corresponding transition model.
	 * The transition model is selected according to the project {@link WorkflowableTask#getProject()}
	 * and type {@link WorkflowableTask#getTaskType()} of task.
	 *
	 * @param task workflow task
	 */
	void setInitialStep(WorkflowableTask task);

	/**
	 * Sets the specified task to the specified step of the corresponding transition model.
	 *
	 * @param task task
	 * @param step step of the transition model
	 */
	void setCustomStep(WorkflowableTask task, WorkflowStep step);

	/**
	 * Returns available transitions for the specified task.
	 *
	 * @param task tasj
	 * @return list of available transitions
	 */
	List<WorkflowTransition> getTransitions(WorkflowableTask task);

	/**
	 * Performing a user-initiated transition for a specific task
	 *
	 * @param bcDescription       description of the business component of a task
	 * @param task                task
	 * @param transition          transition
	 * @param preInvokeParameters the user's confirmation of the transition
	 * @return result of the transition
	 */
	TransitionResult invokeTransition(BcDescription bcDescription, WorkflowableTask task, WorkflowTransition transition,
			List<String> preInvokeParameters);

	/**
	 * Performing an automatic transition for the specified task.
	 *
	 * @param task       task
	 * @param transition transition
	 * @return result of the transition
	 */
	TransitionResult invokeAutoTransition(WorkflowableTask task, WorkflowTransition transition);

	/**
	 * Performing an automatic transition for a given task without checking its capability.
	 *
	 * @param task       task
	 * @param transition transition
	 */
	void forceInvokeAutoTransition(WorkflowableTask task, WorkflowTransition transition);

	/**
	 * Performing an automatic transition for a specified task to a step with the HIDDEN status, if it exists in the transition model.
	 *
	 * @param task task
	 */
	void forceInvokeAutoTransitionToHiddenStep(WorkflowableTask task);

	/**
	 * Checks whether editing of child business components of a specified task is blocked.
	 *
	 * @param bcIdentifier identifier of business component which is mapped to a task
	 * @param task         task
	 * @return whether editing of child business components is blocked
	 */
	boolean isChildBcDisabled(BcIdentifier bcIdentifier, WorkflowableTask task);

	/**
	 * Returns a list of fields that are not editable for a specified task.
	 *
	 * @param task task
	 * @return list of fields that can't be edited
	 */
	List<String> getDisableFields(WorkflowableTask task);

	/**
	 * Returns a specification for searching for recommended performers for a specified task.
	 *
	 * @param task task
	 * @return specification for searching for recommended performers
	 */
	Specification<User> getAssigneeRecommendationSpecification(WorkflowableTask task);

	/**
	 * Checks whether the required fields must be filled in to complete the specified transition..
	 *
	 * @param transition transition
	 * @return whether it is necessary to check filling of mandatory fields
	 */
	boolean checkRequiredFieldsForTransition(WorkflowTransition transition);

	/** Performing an automatic transition for a specified task without executing post functions and checking its capability.
	 * @param transition transition to be performed
	 * @param task task
	 * @return result of the transition
	 */
	TransitionResult forceInvokeAutoTransitionIgnorePostFunctions(WorkflowTransition transition, WorkflowableTask task);

}
