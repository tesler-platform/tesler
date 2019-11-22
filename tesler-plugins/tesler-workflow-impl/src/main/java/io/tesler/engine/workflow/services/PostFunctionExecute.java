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

import static io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType.POST_FUNCTION;
import static com.google.common.collect.ImmutableMap.builder;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.engine.workflow.function.DefaultPostFunction;
import io.tesler.engine.workflow.function.PostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
final class PostFunctionExecute {

	private final WorkflowSettings<?> workflowSettings;

	private final WorkflowDaoImpl workflowDao;

	private final ConditionCheck conditionCheck;

	private final PostFunction<? extends WorkflowableTask, ? extends WorkflowPostFunction> defaultPostFunction;

	private final Map<LOV, PostFunction<? extends WorkflowableTask, ? extends WorkflowPostFunction>> postFunctions;

	PostFunctionExecute(
			final WorkflowSettings<?> workflowSettings,
			final WorkflowDaoImpl workflowDao,
			final ConditionCheck conditionCheck,
			final DefaultPostFunction defaultPostFunction,
			final List<PostFunction<? extends WorkflowableTask, ? extends WorkflowPostFunction>> postFunctions) {
		this.workflowSettings = workflowSettings;
		this.workflowDao = workflowDao;
		this.conditionCheck = conditionCheck;
		this.defaultPostFunction = defaultPostFunction;

		final Builder<LOV, PostFunction<? extends WorkflowableTask, ? extends WorkflowPostFunction>> builder = builder();
		for (PostFunction<? extends WorkflowableTask, ? extends WorkflowPostFunction> postFunction : postFunctions) {
			if (postFunction.getType() != null) {
				builder.put(postFunction.getType(), postFunction);
			}
		}
		this.postFunctions = builder.build();
	}

	/**
	 * Выполняет указанные для перехода пост-функции
	 *
	 * @param task активность
	 * @param transition переход
	 * @return Список пост экшнов, которые должен выполнить фронт
	 */
	List<PostAction> execute(final BcDescription bcDescription, final WorkflowableTask task,
			final WorkflowTransition transition) {
		log.debug(
				"Проверка условий для выполнения пост-функций перехода '{}' id: {} для активности id: {}",
				transition.getName(),
				transition.getId(),
				task.getId()
		);
		final List<PostAction> result = new ArrayList<>();
		final val conditionGroups = workflowDao.getTransitionConditionGroups(transition, POST_FUNCTION);
		if (conditionGroups.isEmpty()) {
			log.debug("Список групп условий для выполнения пост-функций перехода пуст, пост-функции будут выполнены");
		}
		for (final WorkflowTransitionConditionGroup conditionGroup : conditionGroups) {
			log.debug(
					"Проверка группы условий для выполнения пост-функций перехода '{}' id: {}",
					conditionGroup.getName(),
					conditionGroup.getId()
			);
			final val conditions = workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), conditionGroup);
			if (conditionCheck.isAvailable(task, conditions, transition)) {
				final List<? extends WorkflowPostFunction> postFunctions = workflowDao.getPostFunctions(
						workflowSettings.getPostFunctionExtensionClass(), conditionGroup
				);
				for (WorkflowPostFunction wfPostFunction : postFunctions) {
					log.debug("Выполнение пост-функции '{}'", wfPostFunction.getActionCd());
					final PostFunction postFunction = this.postFunctions.getOrDefault(
							wfPostFunction.getActionCd(),
							defaultPostFunction
					);
					result.addAll(postFunction.invoke(bcDescription, task, wfPostFunction));
				}
			}
		}
		return result;
	}

}
