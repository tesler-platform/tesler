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

package io.tesler.source.services;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.exception.BusinessException;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTask_;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowableTask;
import io.tesler.model.workflow.entity.WorkflowableTask_;
import java.util.List;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class AbstractWorkflowableTaskDao<E extends WorkflowableTask> implements WorkflowableTaskDao<E> {

	protected final WorkflowSettings<E> workflowSettings;

	protected final JpaDao jpaDao;

	@Override
	public E getTask(Long id) {
		final E task = jpaDao.findById(workflowSettings.getEntityClass(), id);
		if (task == null) {
			throw new BusinessException().addPopup(errorMessage("error.task_not_found", id));
		}
		return task;
	}

	@Override
	public E getTask(WorkflowTask workflowTask) {
		return jpaDao.getSingleResultOrNull(workflowSettings.<E>getEntityClass(), (root, query, cb) -> cb.equal(
				root.get(WorkflowableTask_.workflowTask), workflowTask
		));
	}

	@Override
	public List<WorkflowTask> getPendingTransitionWorkflowTasks() {
		return jpaDao.getList(
				workflowSettings.getEntityClass(),
				WorkflowTask.class,
				(root, cb) -> root.get(WorkflowableTask_.workflowTask),
				(root, query, cb) -> root.get(WorkflowableTask_.workflowTask).get(WorkflowTask_.pendingTransition).isNotNull()
		);
	}

	@Override
	public List<E> getOtherVersionTasks(final WorkflowVersion version) {
		return jpaDao.getList(workflowSettings.<E>getEntityClass(), (root, query, cb) -> cb.and(
				cb.equal(
						root.get(WorkflowableTask_.workflowTask).get(WorkflowTask_.workflowName),
						version.getWorkflow().getName()
				),
				cb.not(
						cb.equal(
								root.get(WorkflowableTask_.workflowTask).get(WorkflowTask_.version),
								version.getVersion()
						)
				)
		));
	}

}
