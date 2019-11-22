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
import java.util.List;
import org.pf4j.ExtensionPoint;
import org.springframework.data.jpa.domain.Specification;


public interface WorkflowEngine extends ExtensionPoint {

	/**
	 * Устанавливает указанную активность в начальный шаг соответствующей модели переходов.
	 * <p/>
	 * Модель переходов выбирается в соответствии c проектом {@link WorkflowableTask#getProject()}
	 * и типом {@link WorkflowableTask#getTaskType()} активности.
	 *
	 * @param task активность
	 */
	void setInitialStep(WorkflowableTask task);

	/**
	 * Устанавливает указанную активность в заданный шаг соответствующей модели переходов.
	 *
	 * @param task активность
	 * @param step шаг модели переходов
	 */
	void setCustomStep(WorkflowableTask task, WorkflowStep step);

	/**
	 * Возвращает доступные переходы для указанной активности.
	 *
	 * @param task активность
	 * @return доступные переходы
	 */
	List<WorkflowTransition> getTransitions(WorkflowableTask task);

	/**
	 * Выполнение инициированного пользователем перехода для заданной активности.
	 *
	 * @param bcDescription описание бизнес-компонента активности
	 * @param task активность
	 * @param transition переход
	 * @param preInvokeParameters полученные у пользователя подтверждения перехода
	 * @return результат выполнения перехода
	 */
	TransitionResult invokeTransition(BcDescription bcDescription, WorkflowableTask task, WorkflowTransition transition,
			List<String> preInvokeParameters);

	/**
	 * Выполнение автоматического перехода для заданной активности.
	 *
	 * @param task активность
	 * @param transition переход
	 * @return результат выполнения перехода
	 */
	TransitionResult invokeAutoTransition(WorkflowableTask task, WorkflowTransition transition);

	/**
	 * Выполнение автоматического перехода для заданной активности без проверок его возможности.
	 *
	 * @param task активность
	 * @param transition переход
	 */
	void forceInvokeAutoTransition(WorkflowableTask task, WorkflowTransition transition);

	/**
	 * Выполнение автоматического перехода для заданной активности в шаг со статусом HIDDEN, если он есть в модели переходов.
	 *
	 * @param task активность
	 */
	void forceInvokeAutoTransitionToHiddenStep(WorkflowableTask task);

	/**
	 * Проверяет заблокировано ли редактирование дочерних бизнес-компонентов указанной активности.
	 *
	 * @param bcIdentifier идентификатор бизнес-компонента активности
	 * @param task активность
	 * @return заблокировано ли редактирование дочерних бизнес-компонентов
	 */
	boolean isChildBcDisabled(BcIdentifier bcIdentifier, WorkflowableTask task);

	/**
	 * Возвращает список недоступных для редактирования полей заданной активности.
	 *
	 * @param task активность
	 * @return список недоступных для редактирования полей
	 */
	List<String> getDisableFields(WorkflowableTask task);

	/**
	 * Возвращает спецификацию для поиска рекомендованных исполнителей для указанной активности.
	 *
	 * @param task активность
	 * @return спецификацию для поиска рекомендованных исполнителей
	 */
	Specification<User> getAssigneeRecommendationSpecification(WorkflowableTask task);

	/**
	 * Проверяет необходимо ли проверять обязательность заполнения полей для выполнения указанного перехода.
	 *
	 * @param transition переход
	 * @return необходимо ли проверять обязательность заполнения полей
	 */
	boolean checkRequiredFieldsForTransition(WorkflowTransition transition);

}
