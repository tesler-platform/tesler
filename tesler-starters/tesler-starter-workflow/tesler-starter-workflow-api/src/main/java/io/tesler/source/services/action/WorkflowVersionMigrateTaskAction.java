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

package io.tesler.source.services.action;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.service.PluginAware;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.MessageType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.service.action.ResponseServiceAction;
import io.tesler.core.util.session.SessionService;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowableTask;
import io.tesler.source.dto.WorkflowVersionDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowVersionMigrateTaskAction extends ResponseServiceAction<WorkflowVersionDto> {

	private final JpaDao jpaDao;

	private final AsyncTaskMigration asyncTaskMigration;

	@Override
	public String getButtonName() {
		return "Мигрировать все задачи и шаблоны на эту версию";
	}

	@Override
	public boolean isAvailable(final BusinessComponent bc) {
		return bc.getId() != null && !jpaDao.findById(WorkflowVersion.class, bc.getIdAsLong()).isDraft();
	}

	@Override
	public ActionResultDTO<WorkflowVersionDto> invoke(final BusinessComponent bc, final WorkflowVersionDto data) {
		asyncTaskMigration.invokeAsync(bc.getIdAsLong());
		return new ActionResultDTO<>(data).setAction(PostAction.showMessage(
				MessageType.INFO,
				errorMessage("info.workflow_migration_has_been_started")
		));
	}

	@Slf4j
	@Service
	@PluginAware
	@RequiredArgsConstructor
	public static class AsyncTaskMigration {

		private final JpaDao jpaDao;

		private final SessionService sessionService;

		private final WorkflowableTaskDao<?> workflowableTaskDao;

		private final WorkflowDao workflowDao;

		private final TransactionService txService;

		@Async
		void invokeAsync(final Long versionId) {
			try {
				txService.invokeInTx(Invoker.of(() -> doInvoke(versionId)));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		private void doInvoke(Long versionId) {
			final WorkflowVersion version = jpaDao.findById(WorkflowVersion.class, versionId);
			final List<? extends WorkflowableTask> tasks = workflowableTaskDao.getOtherVersionTasks(version);

			int migrated = 0;
			int skipped = 0;
			for (final WorkflowableTask task : tasks) {
				final WorkflowTransition newAutomaticTransition = getNewAutomaticTransition(version, task);
				final WorkflowStep newStep = getNewStep(version, task, newAutomaticTransition);
				boolean shouldSkip = (task.getWorkflowTask() != null && newStep == null)
						|| (task.getAutomaticTransitionName() != null && newAutomaticTransition == null);
				if (shouldSkip) {
					skipped++;
					continue;
				}
				workflowDao.setWorkflowStep(task.getWorkflowTask(), newStep);
				task.setAutomaticTransitionName(
						Optional.ofNullable(newAutomaticTransition).map(WorkflowTransition::getName).orElse(null)
				);
				migrated++;
			}
		}


		private WorkflowTransition getNewAutomaticTransition(final WorkflowVersion version, final WorkflowableTask task) {
			if (task.getAutomaticTransitionName() == null) {
				return null;
			}
			return workflowDao.getTransitionByName(version, task.getAutomaticTransitionName());
		}

		private WorkflowStep getNewStep(final WorkflowVersion version, final WorkflowableTask task,
				final WorkflowTransition newAutomaticTransition) {
			if (newAutomaticTransition != null) {
				return newAutomaticTransition.getSourceStep();
			} else if (task.getWorkflowTask() != null) {
				return workflowDao.getStepByName(version, task.getWorkflowTask().getStepName());
			} else {
				return null;
			}
		}

	}

}
