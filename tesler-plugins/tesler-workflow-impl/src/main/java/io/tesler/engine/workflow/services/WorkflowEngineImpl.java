/*-
 * #%L
 * IO Tesler - Workflow Impl
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
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.TaskField;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStepField;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Extension
@RequiredArgsConstructor
public class WorkflowEngineImpl implements WorkflowEngine {

	private final WorkflowSettings<?> workflowSettings;

	private final JpaDao jpaDao;

	private final ConditionCheck conditionCheck;

	private final TransitionCheck transitionCheck;

	private final TransitionValidate transitionValidate;

	private final TransitionInvoke transitionInvoke;

	private final AssigneeRecommender assigneeRecommender;

	private final WorkflowDaoImpl workflowDao;

	@Override
	public void setInitialStep(final WorkflowableTask task) {
		setCustomStep(task, workflowDao.getInitialStep(task.getProject(), task.getTaskType()));
	}

	@Override
	public void setCustomStep(final WorkflowableTask task, final WorkflowStep step) {
		if (task.getWorkflowTask() == null) {
			task.setWorkflowTask(workflowDao.createWorkflowTask(step));
		} else {
			task.getWorkflowTask().setWorkflowStep(step);
		}
	}

	@Override
	public List<WorkflowTransition> getTransitions(final WorkflowableTask task) {
		log.debug("Получение списка возможных переходов для активности id: {}", task.getId());
		if (transitionValidate.isPendingTransitionTask(task)) {
			log.debug("Переход выполняется, другие переходы недоступны");
			return Collections.emptyList();
		}
		final WorkflowStep currentStep = workflowDao.getCurrentStep(task);
		log.debug("Текущий шаг активности '{}' id: {}", currentStep.getName(), currentStep.getId());
		return workflowDao.getTransitions(currentStep).stream()
				.filter(transition -> transitionCheck.isAvailable(task, transition))
				.collect(Collectors.toList());
	}

	@Override
	public TransitionResult invokeTransition(
			final BcDescription bcDescription,
			final WorkflowableTask task,
			final WorkflowTransition transition,
			final List<String> preInvokeParameters) {
		log.debug(
				"Выполнение перехода '{}' id: {} для активности id: {}", transition.getName(), transition.getId(), task.getId()
		);
		transitionValidate.validate(task, transition, true, preInvokeParameters);
		return transitionInvoke.invoke(bcDescription, task, transition);
	}

	@Override
	public TransitionResult invokeAutoTransition(WorkflowableTask task, WorkflowTransition transition) {
		log.debug(
				"Выполнение автоматического перехода '{}' id: {} для активности id: {}",
				transition.getName(),
				transition.getId(),
				task.getId()
		);
		transitionValidate.validate(task, transition, false, Collections.emptyList());
		return transitionInvoke.invoke(null, task, transition);
	}

	@Override
	public void forceInvokeAutoTransition(final WorkflowableTask task, final WorkflowTransition transition) {
		transitionInvoke.forceInvoke(null, task, transition);
	}

	@Override
	public void forceInvokeAutoTransitionToHiddenStep(final WorkflowableTask task) {
		final WorkflowTask workflowTask = task.getWorkflowTask();
		if (workflowTask != null) {
			final WorkflowStep currentStep = workflowTask.getWorkflowStep();
			final WorkflowStep hiddenStep = workflowDao.getHiddenStep(currentStep.getWorkflowVersion());
			if (hiddenStep != null) {
				final WorkflowTransition transition = workflowDao.getTransitionBetweenSteps(currentStep, hiddenStep);
				transitionInvoke.forceInvoke(null, task, transition);
			}
		}
	}

	@Override
	public boolean isChildBcDisabled(final BcIdentifier bcIdentifier, final WorkflowableTask task) {
		final WorkflowStep step = workflowDao.getCurrentStep(task);
		if (step == null) {
			return false;
		}
		return workflowDao.getWorkflowTaskChildBcAvailabilities(step).stream()
				.filter(childBcAvailability -> Objects.equals(childBcAvailability.getBcName(), bcIdentifier.getName()))
				.anyMatch(
						childBcAvailability -> conditionCheck.isAvailable(
								task,
								workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), childBcAvailability),
								null
						)
				);
	}

	@Override
	public List<String> getDisableFields(final WorkflowableTask task) {
		log.debug("Получение списка нередактируемых полей для активности id: {}", task.getId());
		final Stream<TaskField> disableFields;
		if (transitionValidate.isPendingTransitionTask(task)) {
			log.debug("Переход выполняется, редактирование недоступно");
			disableFields = jpaDao.getList(TaskField.class).stream();
		} else {
			final WorkflowStep currentStep = workflowDao.getCurrentStep(task);
			log.debug("Текущий шаг активности '{}' id: {}", currentStep.getName(), currentStep.getId());
			final List<WorkflowStepField> stepFields = workflowDao.getStepFields(currentStep);
			if (stepFields.isEmpty()) {
				log.debug("Список нередактируемых полей не настроен");
			}
			disableFields = stepFields.stream().filter(stepField -> stepField.getTaskField() != null).filter(
					stepField -> {
						log.debug("Проверка нередактируемости поля '{}'", stepField.getTaskField().getKey());
						final boolean available = conditionCheck.isAvailable(
								task, workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), stepField), null
						);
						log.debug(available ? "Поле нередактируемо" : "Поле редактируемо");
						return available;
					}
			).map(WorkflowStepField::getTaskField);
		}
		return disableFields.map(TaskField::getKey).collect(Collectors.toList());
	}

	@Override
	public Specification<User> getAssigneeRecommendationSpecification(final WorkflowableTask task) {
		return assigneeRecommender.recommend(task);
	}

	@Override
	public boolean checkRequiredFieldsForTransition(final WorkflowTransition transition) {
		return transition.getCheckRequiredFields();
	}

}
