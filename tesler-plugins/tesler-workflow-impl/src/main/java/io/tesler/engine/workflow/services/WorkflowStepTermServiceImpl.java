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

import io.tesler.core.util.DateTimeUtil;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.model.workflow.entity.WorkflowableTask;
import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Extension
@RequiredArgsConstructor
public class WorkflowStepTermServiceImpl implements WorkflowStepTermService {

	private final WorkflowableTaskDao<?> workflowableTaskDao;

	private final WorkflowEngine workflowEngine;

	/**
	 * Находит активности для которых истек срок выполнения шага и выполняет заданные для них переходы
	 */
	@Override
	@Transactional
	public void invokeOverdueTransitions() {
		for (final WorkflowableTask task : workflowableTaskDao.getTasksWithStepTermOverdue(DateTimeUtil.now())) {
			workflowEngine.invokeAutoTransition(task, task.getWorkflowTask().getWorkflowStep().getOverdueTransition());
		}
	}

}
