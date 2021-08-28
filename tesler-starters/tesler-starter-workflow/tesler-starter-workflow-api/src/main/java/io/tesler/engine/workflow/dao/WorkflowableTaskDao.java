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

package io.tesler.engine.workflow.dao;

import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.time.LocalDateTime;
import java.util.List;


public interface WorkflowableTaskDao<E extends WorkflowableTask> {

	WorkflowableTask getTask(Long id);

	E getTask(WorkflowTask workflowTask);

	/**
	 * Возвращает активности для которых на указанную дату истекло время выполнения шага
	 *
	 * @param date дата для поиска
	 * @return активности для которых истекло время выполнения шага
	 */
	List<E> getTasksWithStepTermOverdue(LocalDateTime date);

	List<E> findAllLinksWithAutoClosed(WorkflowableTask task);

	List<WorkflowTask> getPendingTransitionWorkflowTasks();

	/**
	 * Возвращает задачи той-же модели переходов имеющие версии отличные от указанной
	 *
	 * @param version версия модели переходов
	 * @return задачи имеющие версии отличные от указанной
	 */
	List<E> getOtherVersionTasks(WorkflowVersion version);

	boolean isClosedChild(WorkflowableTask task);

}
