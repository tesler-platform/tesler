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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableMap.builder;
import static java.util.Objects.nonNull;

import io.tesler.api.data.dictionary.CoreDictionaries.WfTransitionValidate;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType;
import io.tesler.core.dto.PreInvokeEvent;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.exception.UnconfirmedException;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.engine.workflow.validation.TransitionValidator;
import io.tesler.engine.workflow.validation.UnsupportedValidator;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionValidation;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
final class TransitionValidate {

	private final WorkflowSettings<?> workflowSettings;

	private final WorkflowDaoImpl workflowDao;

	private final ConditionCheck conditionCheck;

	private final TransitionCheck transitionCheck;

	private final PreInvoker preInvoker;

	private final TransitionValidator<? extends WorkflowableTask, ? extends WorkflowTransitionValidation> defaultTransitionValidator;

	private final Map<LOV, TransitionValidator<? extends WorkflowableTask, ? extends WorkflowTransitionValidation>> transitionValidators;

	TransitionValidate(
			final WorkflowSettings<?> workflowSettings,
			final WorkflowDaoImpl workflowDao,
			final ConditionCheck conditionCheck,
			final TransitionCheck transitionCheck,
			final PreInvoker preInvoker,
			final UnsupportedValidator defaultTransitionValidator,
			final List<TransitionValidator<? extends WorkflowableTask, ? extends WorkflowTransitionValidation>> transitionValidators) {
		this.workflowSettings = workflowSettings;
		this.workflowDao = workflowDao;
		this.conditionCheck = conditionCheck;
		this.transitionCheck = transitionCheck;
		this.preInvoker = preInvoker;
		this.defaultTransitionValidator = defaultTransitionValidator;

		final Builder<LOV, TransitionValidator<? extends WorkflowableTask, ? extends WorkflowTransitionValidation>> builder = builder();
		for (TransitionValidator<? extends WorkflowableTask, ? extends WorkflowTransitionValidation> transitionValidator : transitionValidators) {
			if (transitionValidator.getType() != null) {
				builder.put(transitionValidator.getType(), transitionValidator);
			}
		}
		this.transitionValidators = builder.build();
	}

	/**
	 * Проверяет возможность перехода для активности
	 *
	 * @param task активность
	 * @param transition переход
	 * @param fullValidation проводить ли полную проверку возможности перехода
	 * @param confirmedPreInvokeKeys список подтвержденных проверок
	 * @throws BusinessException содержащий список причин если переход невозможен
	 */
	void validate(final WorkflowableTask task, final WorkflowTransition transition, boolean fullValidation,
			final Collection<String> confirmedPreInvokeKeys) {
		log.debug("Проверка возможности перехода '{}' id: {}", transition.getName(), transition.getId());
		check(task, transition, fullValidation);
		if (fullValidation) {
			final List<String> messages = new ArrayList<>();
			final List<PreInvokeEvent> preInvokeEvents = new ArrayList<>();
			final val conditionGroups = workflowDao.getTransitionConditionGroups(transition, ConditionGroupType.VALIDATION);
			if (conditionGroups.isEmpty()) {
				log.debug("Список групп условий для проверки возможности перехода пуст, переход возможен");
			}
			for (final WorkflowTransitionConditionGroup conditionGroup : conditionGroups) {
				log.debug(
						"Проверка группы условий для проверки возможности перехода '{}' id: {}",
						conditionGroup.getName(),
						conditionGroup.getId()
				);
				final val conditions = workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), conditionGroup);
				if (conditionCheck.isAvailable(task, conditions, transition)) {
					final List<? extends WorkflowTransitionValidation> validations = workflowDao.getValidations(
							workflowSettings.getTransitionValidationExtensionClass(), conditionGroup
					);
					for (final WorkflowTransitionValidation transitionValidation : validations) {
						if (Objects.equals(transitionValidation.getValidCd(), WfTransitionValidate.TRANSITION_PRE_INVOKE)) {
							preInvokeEvents.add(preInvoker.invoke(task, transitionValidation, confirmedPreInvokeKeys));
						} else {
							final TransitionValidator validator = transitionValidators.getOrDefault(
									transitionValidation.getValidCd(), defaultTransitionValidator
							);
							final List<String> errors = validator.validate(task, transitionValidation);
							if (errors.isEmpty()) {
								log.debug(
										"Проверка возможности перехода {} id: {} пройдена успешно",
										transitionValidation.getValidCd().getKey(),
										transitionValidation.getId()
								);
							} else {
								log.debug(
										"Проверка возможности перехода {} id: {} завершилась с ошибкой",
										transitionValidation.getValidCd().getKey(),
										transitionValidation.getId()
								);
								messages.addAll(errors);
							}
						}
					}
				}
			}
			if (!messages.isEmpty()) {
				log.debug("Проверка возможности перехода не пройдена, переход невозможен");
				throw new BusinessException().addPopup(messages);
			}
			executePreInvokeEvents(preInvokeEvents);
		}
	}

	private void check(final WorkflowableTask task, final WorkflowTransition transition, final boolean checkConditions) {
		if (isPendingTransitionTask(task)) {
			log.debug("Переход выполняется, другие переходы недоступны");
			throw new BusinessException().addPopup(errorMessage("error.pending_transition_exists"));
		}
		final WorkflowStep currentStep = workflowDao.getCurrentStep(task);
		if (!(transition != null
				&& equal(currentStep, transition.getSourceStep())
				&& equal(currentStep.getWorkflowVersion(), transition.getDestinationStep().getWorkflowVersion())
				&& (!checkConditions || transitionCheck.isAvailable(task, transition)))) {
			log.debug("Переход не доступен из шага '{}'.", currentStep.getName());
			throw new BusinessException().addPopup(
					errorMessage("error.transition_unavailable_for_source", currentStep.getName())
			);
		}
	}

	private void executePreInvokeEvents(final List<PreInvokeEvent> preInvokeEvents) {
		if (nonNull(preInvokeEvents)) {
			preInvokeEvents.removeIf(Objects::isNull);
			if (!preInvokeEvents.isEmpty()) {
				log.debug("Будет показано диалоговое окно. Переход не выполнен.");
				throw new UnconfirmedException().addPreInvokeEvents(preInvokeEvents);
			}
		}
	}

	boolean isPendingTransitionTask(final WorkflowableTask task) {
		return task.getWorkflowTask() != null && task.getWorkflowTask().getPendingTransition() != null;
	}

}
