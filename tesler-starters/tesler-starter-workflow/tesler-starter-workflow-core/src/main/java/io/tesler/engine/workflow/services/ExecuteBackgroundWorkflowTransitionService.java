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

import static io.tesler.api.data.dictionary.CoreDictionaries.SystemPref.WF_BACKGROUND_TRANSITION_THREADS_COUNT;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.system.SystemSettings;
import io.tesler.api.util.Invoker;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.PendingTransition;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTask_;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.tesler.quartz.impl.ScheduledService;
import io.tesler.quartz.model.ScheduledJob;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.LockOptions;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Extension
@Service("regularExecuteBackgroundWorkflowTransition")
public class ExecuteBackgroundWorkflowTransitionService implements ExtensionPoint, ScheduledService {

	private final WorkflowEngine workflowEngine;

	private final WorkflowDao workflowDao;

	private final WorkflowableTaskDao<?> workflowableTaskDao;

	private final Optional<WorkflowNotificationService> workflowNotificationService;

	private final JpaDao jpaDao;

	private final TransactionService transactionService;

	private final InternalAuthorizationService authorizationService;

	private final ExecutorService executorService;

	public ExecuteBackgroundWorkflowTransitionService(
			final WorkflowEngine workflowEngine,
			final WorkflowableTaskDao<?> workflowableTaskDao,
			final Optional<WorkflowNotificationService> workflowNotificationService,
			final JpaDao jpaDao, TransactionService transactionService,
			final InternalAuthorizationService authorizationService,
			final WorkflowDao workflowDao,
			final SystemSettings systemSettings) {
		this.workflowEngine = workflowEngine;
		this.workflowableTaskDao = workflowableTaskDao;
		this.workflowNotificationService = workflowNotificationService;
		this.workflowDao = workflowDao;
		this.jpaDao = jpaDao;
		this.transactionService = transactionService;
		this.authorizationService = authorizationService;
		this.executorService = Executors.newFixedThreadPool(
				NumberUtils.toInt(systemSettings.getValue(WF_BACKGROUND_TRANSITION_THREADS_COUNT), 1),
				new ThreadFactoryBuilder().setNameFormat("wfBackgroundTransition-%d").setDaemon(true).build()
		);
	}

	@Override
	@SneakyThrows
	@Transactional
	public void execute(final ScheduledJob job) {
		cleanPendingTransitionTable();
		final List<Callable<Void>> callables = new ArrayList<>();
		for (WorkflowTask workflowTask : workflowableTaskDao.getPendingTransitionWorkflowTasks()) {
			callables.add(new TransitionInvoke(
					workflowEngine,
					workflowDao,
					workflowTask.getPendingTransition().getUser().getLogin(),
					workflowTask.getPendingTransition().getUserRole(),
					workflowTask.getId()
			));
		}
		executorService.invokeAll(callables);
	}

	private void cleanPendingTransitionTable() {
		transactionService.invokeInNewTx(Invoker.of(() -> {
			jpaDao.delete(PendingTransition.class, (root, query, cb) -> {
				final Subquery<PendingTransition> subquery = query.subquery(PendingTransition.class);
				final Root<WorkflowTask> subqueryRoot = subquery.from(WorkflowTask.class);
				subquery.select(subqueryRoot.get(WorkflowTask_.pendingTransition));
				subquery.where(subqueryRoot.get(WorkflowTask_.pendingTransition).isNotNull());
				return root.in(subquery).not();
			});
		}));
	}

	@RequiredArgsConstructor
	private class TransitionInvoke implements Callable<Void> {

		private final WorkflowEngine workflowEngine;

		private final WorkflowDao workflowDao;

		private final String userLogin;

		private final LOV userRole;

		private final Long workflowTaskId;

		@Override
		public Void call() {
			PrivilegeUtil.runPrivileged(Invoker.of(() -> {
				authorizationService.loginAs(userLogin, userRole);
				try {
					transactionService.invokeInNewTx(Invoker.of(() -> {
						final WorkflowTask workflowTask = jpaDao.findById(WorkflowTask.class, workflowTaskId);
						jpaDao.lockAndRefresh(workflowTask, LockOptions.WAIT_FOREVER);
						final PendingTransition pendingTransition = workflowTask.getPendingTransition();
						if (pendingTransition != null) {
							workflowEngine.forceInvokeAutoTransition(
									workflowableTaskDao.getTask(workflowTask),
									pendingTransition.getTransition()
							);
							workflowTask.setPendingTransition(null);
						}
					}));
				} catch (Exception e) {
					final UUID uuid = UUID.randomUUID();
					log.error(uuid.toString() + ": Ошибка выполнения фонового перехода.", e);
					try {
						transactionService.invokeInNewTx(Invoker.of(() -> {
							final WorkflowTask workflowTask = jpaDao.findById(WorkflowTask.class, workflowTaskId);
							jpaDao.lockAndRefresh(workflowTask, LockOptions.WAIT_FOREVER);
							final PendingTransition pendingTransition = workflowTask.getPendingTransition();
							if (pendingTransition != null) {
								workflowNotificationService.ifPresent(
										notificationService -> notificationService.sendBackgroundTransitionErrorMessage(
												uuid,
												workflowableTaskDao.getTask(workflowTask),
												pendingTransition.getTransition()
										)
								);
								workflowDao.setWorkflowStep(workflowTask, pendingTransition.getTransition().getSourceStep());
								workflowTask.setPendingTransition(null);
							}
						}));
					} catch (Exception e2) {
						log.error(uuid.toString() + ": Ошибка при возврате на исходный шаг фонового перехода.", e2);
					}
				}
			}));
			return null;
		}

	}

}

