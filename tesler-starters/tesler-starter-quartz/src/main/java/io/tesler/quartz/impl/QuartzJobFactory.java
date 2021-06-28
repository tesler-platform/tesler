/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.quartz.impl;

import static io.tesler.api.service.session.InternalAuthorizationService.VANILLA;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.model.core.dao.JpaDao;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.PostConstruct;

import io.tesler.quartz.model.ScheduledJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component("quartzJobFactory")
public class QuartzJobFactory extends AdaptableJobFactory {

	private final JpaDao jpaDao;

	private final InternalAuthorizationService authzService;

	private final TransactionService txService;

	private final ApplicationContext applicationContext;

	private Authentication authentication;

	@PostConstruct
	public void init() {
		authentication = authzService.createAuthentication(VANILLA);
	}

	@Override
	public Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		return PrivilegeUtil.runPrivileged(() -> {
			String jobName = bundle.getJobDetail().getKey().getName();
			ScheduledJob scheduledJob = null;
			if (NumberUtils.isParsable(jobName)) {
				long id = NumberUtils.toLong(jobName);
				scheduledJob = jpaDao.findById(ScheduledJob.class, id);
			}
			if (scheduledJob == null) {
				return super.createJobInstance(bundle);
			}
			Object job = applicationContext.getBean(scheduledJob.getService().getKey());
			if (job instanceof ScheduledService) {
				return new DelegatingJob((ScheduledService) job, scheduledJob);
			}
			return job;
		});
	}

	class DelegatingJob implements Job {

		private final ScheduledService delegate;

		private final ScheduledJob job;

		private Throwable deferredException;

		DelegatingJob(ScheduledService delegate, ScheduledJob job) {
			this.delegate = Objects.requireNonNull(delegate, "ScheduledService must not be null");
			this.job = Objects.requireNonNull(job, "ScheduledJob must not be null");
		}

		public void execute(JobExecutionContext context) throws JobExecutionException {
			PrivilegeUtil.runPrivileged(() -> {
				authzService.loginAs(authentication);
				doExecute();
				if (deferredException != null) {
					throw new JobExecutionException(deferredException);
				}
				return null;
			});
		}

		private void doExecute() {
			boolean success = false;
			long start = System.currentTimeMillis();
			long duration = 0;
			LocalDateTime lastLaunchDate = DateTimeUtil.now();

			try {
				// todo: запускаем вне транзакции,
				// пусть сам сервис решает что ему делать
				txService.invokeNoTx(() -> {
					delegate.execute(job);
					return null;
				});
				success = true;
				duration = System.currentTimeMillis() - start;
			} catch (Error e) {
				log.error(e.getMessage(), e);
				throw e;
			} catch (Throwable t) {
				deferredException = t;
				log.error(t.getMessage(), t);
			} finally {
				updateJobInfo(success, duration, lastLaunchDate);
			}
		}

		private void updateJobInfo(boolean success, long duration, LocalDateTime lastLaunchDate) {
			txService.invokeInTx(() -> {
				ScheduledJob job = jpaDao.findById(ScheduledJob.class, this.job.getId());
				job.setLastLaunchDate(lastLaunchDate);
				job.setLaunchCnt(job.getLaunchCnt() + 1);
				if (success) {
					job.setLaunchFailedLastCnt(0);
					job.setLastSuccessLaunchDate(lastLaunchDate);
					job.setLastSuccessLaunchDuration(duration);
					job.setLastLaunchStatus(CoreDictionaries.LaunchStatus.SUCCESS);
				} else {
					job.setLaunchFailedCnt(job.getLaunchFailedCnt() + 1);
					job.setLaunchFailedLastCnt(job.getLaunchFailedLastCnt() + 1);
					job.setLastLaunchStatus(CoreDictionaries.LaunchStatus.FAILED);
				}
				return null;
			});
		}

	}

}
