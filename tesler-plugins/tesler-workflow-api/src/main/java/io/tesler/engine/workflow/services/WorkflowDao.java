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
import io.tesler.model.workflow.entity.Workflow;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionHistory;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.math.BigDecimal;
import java.util.List;
import org.pf4j.ExtensionPoint;


public interface WorkflowDao extends ExtensionPoint {

	/**
	 * Возвращает текущий шаг для указанной активности.
	 *
	 * @param task активность
	 * @return текущий шаг активности
	 */
	WorkflowStep getCurrentStep(WorkflowableTask task);

	/**
	 * Возвращает шаг со статусом HIDDEN для указанной версии модели переходов.
	 *
	 * @param version версия модели переходов
	 * @return шаг со статусом HIDDEN
	 */
	WorkflowStep getHiddenStep(WorkflowVersion version);

	/**
	 * Возвращает начальный шаг для модели переходов соответствующей указанному проекту и типу активности.
	 *
	 * @param project проект
	 * @param taskType тип активности
	 * @return начальный шаг модели переходов
	 */
	WorkflowStep getInitialStep(Project project, LOV taskType);

	/**
	 * Проверяет является ли указанный шак начальным для своей модели переходов.
	 *
	 * @param step шаг
	 * @return является ли указанный шак начальным
	 */
	boolean isInitialStep(WorkflowStep step);

	/**
	 * Возвращает шаг указанной версии модели переходов по его уникальному идентификатору.
	 *
	 * @param version версия модели переходов
	 * @param uuid уникальный идентификатор шага
	 * @return шаг указанной версии модели переходов
	 */
	WorkflowStep getStepByUuid(WorkflowVersion version, String uuid);

	/**
	 * Возвращает все переходы указанной модели переходов уникальные идентификаторы которых равны заданному.
	 *
	 * @param workflow модель переходов
	 * @param uuid уникальный идентификатор перехода
	 * @return список переходов указанной модели переходов
	 */
	List<WorkflowTransition> getTransitionsByUuid(Workflow workflow, String uuid);

	/**
	 * Возвращает переход указанной версии модели переходов по его уникальному идентификатору.
	 *
	 * @param version версия модели переходов
	 * @param uuid уникальный идентификатор перехода
	 * @return переход указанной версии модели переходов
	 */
	WorkflowTransition getTransitionByUuid(WorkflowVersion version, String uuid);

	/**
	 * Возвращает переход последней версии модели переходов по его уникальному идентификатору.
	 *
	 * @param uuid уникальный идентификатор перехода
	 * @return переход последней версии модели переходов
	 */
	WorkflowTransition getLastWorkflowTransitionByUuid(String uuid);

	/**
	 * Возвращает переход активной версии модели переходов по его уникальному идентификатору.
	 *
	 * @param uuid уникальный идентификатор перехода
	 * @return переход активной версии модели переходов
	 */
	WorkflowTransition getActiveWorkflowTransitionByUuid(String uuid);

	/**
	 * Возвращает переход активной модели переходов соответствующей указанному типу активности между шагами с заданными статусами.
	 *
	 * @param taskType тип активности
	 * @param sourceStepTaskStatus статус начального шага
	 * @param destinationStepTaskStatus статус конечного шага
	 * @return переход модели переходов
	 */
	WorkflowTransition getTransition(LOV taskType, LOV sourceStepTaskStatus, LOV destinationStepTaskStatus);

	/**
	 * Возвращает последнюю запись истории переходов для указанной активности соответствующую переходу у заданный шаг.
	 *
	 * @param task активность
	 * @param destinationStep конечный шаг перехода
	 * @return последнюю запись истории переходов
	 */
	WorkflowTransitionHistory getLastTransitionHistoryByDestinationStep(WorkflowTask task, WorkflowStep destinationStep);

	/**
	 * Возвращает последнюю запись истории переходов для указанной активности.
	 *
	 * @param task активность
	 * @return последнюю запись истории переходов
	 */
	WorkflowTransitionHistory getLastTransitionHistory(WorkflowTask task);

	/**
	 * Создает дефолтные пост-функции для указанной группы условий перехода.
	 *
	 * @param transitionConditionGroup группа условий перехода
	 */
	void createDefaultPostFunctions(WorkflowTransitionConditionGroup transitionConditionGroup);

	/**
	 * Удаляет указанную пост-функцию со всеми дочерними сущностями.
	 *
	 * @param postFunction пост-функция
	 */
	void deletePostFunction(WorkflowPostFunction postFunction);

	/**
	 * Удаляет указанную группу условий перехода со всеми дочерними сущностями.
	 *
	 * @param transitionConditionGroup группа условий перехода
	 */
	void deleteTransitionConditionGroup(WorkflowTransitionConditionGroup transitionConditionGroup);

	/**
	 * Возвращает версию модели переходов указанной активности.
	 *
	 * @param task активность
	 * @return версию модели переходов указанной активности
	 */
	WorkflowVersion getWorkflowVersion(WorkflowableTask task);

	/**
	 * Возвращает список типов TASK_TYPE, для которых не созданы модели переходов в указанном проекте.
	 *
	 * @param project проект
	 * @return список типов TASK_TYPE, для которых не созданы модели переходов в указанном проекте
	 */
	List<LOV> getTaskTypesNotInWf(Project project);

	/**
	 * Возвращает максимальный номер версии для указанной модели переходов.
	 *
	 * @param workflow модель переходов
	 * @return максимальный номер версии для указанной модели переходов
	 */
	BigDecimal getMaxVersion(Workflow workflow);

	/**
	 * Возвращает номер следующей версии для указанной модели переходов.
	 *
	 * @param workflow модель переходов
	 * @param majorVersion должна ли быть следующая версия мажорной
	 * @return номер следующей версии для указанной модели переходов
	 */
	BigDecimal getNextVersion(Workflow workflow, boolean majorVersion);

}
