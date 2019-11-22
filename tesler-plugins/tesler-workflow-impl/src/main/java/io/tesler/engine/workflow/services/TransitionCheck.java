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

import static io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType.CONDITION;

import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
final class TransitionCheck {

	private final ConditionCheck conditionCheck;

	private final WorkflowSettings<?> workflowSettings;

	private final WorkflowDaoImpl workflowDao;

	boolean isAvailable(final WorkflowableTask task, final WorkflowTransition transition) {
		log.debug(
				"Проверка доступности перехода '{}' id: {} для активности id: {}",
				transition.getName(),
				transition.getId(),
				task.getId()
		);
		final List<WorkflowTransitionConditionGroup> conditionGroups = workflowDao.getTransitionConditionGroups(
				transition, CONDITION
		);
		if (conditionGroups.isEmpty()) {
			log.debug("Список групп условий перехода пуст, переход доступен");
			return true;
		}
		for (final WorkflowTransitionConditionGroup conditionGroup : conditionGroups) {
			log.debug("Проверка группы условий перехода '{}' id: {}", conditionGroup.getName(), conditionGroup.getId());
			final val conditions = workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), conditionGroup);
			if (conditionCheck.isAvailable(task, conditions, transition)) {
				log.debug("Переход доступен");
				return true;
			}
		}
		log.debug("Переход недоступен");
		return false;
	}

}
