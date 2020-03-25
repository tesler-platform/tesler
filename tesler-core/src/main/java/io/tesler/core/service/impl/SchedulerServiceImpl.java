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

package io.tesler.core.service.impl;

import io.tesler.api.exception.ServerException;
import io.tesler.core.ext.quartz.QuartzStatefulNoOpJob;
import io.tesler.core.service.SchedulerService;
import io.tesler.model.core.entity.ScheduledJob;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

	public static final ITriggerFilter CRON_TRIGGER_FILTER = trigger -> trigger instanceof CronTrigger;

	@Autowired
	@Qualifier("quartzSchedulerService")
	private Scheduler scheduler;

	@Override
	public void onBoot(ScheduledJob job) {
		try {
			JobDetail detail = scheduler.getJobDetail(new JobKey(job.getId().toString()));
			if (detail == null && job.isActive()) {
				scheduleJob(job);
				if (job.isLaunchOnCreate()) {
					launchNow(job);
				}
			}
		} catch (Exception ex) {
			throw new ServerException(ex.getMessage(), ex);
		}
	}

	@Override
	public void scheduleJob(ScheduledJob job) {
		try {
			JobBuilder jobBuilder = JobBuilder.newJob().ofType(QuartzStatefulNoOpJob.class);
			jobBuilder.withIdentity(job.getId().toString());
			// нужно обязательно, в противном случае
			// при протухании триггера кварц удалит
			// и триггер и джобу
			jobBuilder.storeDurably();

			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();
			ScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

			JobDetail jobDetail = jobBuilder.build();
			Trigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

			Map<JobDetail, Set<? extends Trigger>> jobData = new HashMap<>();
			jobData.put(jobDetail, Collections.singleton(trigger));

			// мы (пока) не используем расписание отличное от cron
			// поэтому все что отличается - это не наше, поэтому
			// с остальным ничего не делаем

			// поскольку расписание у джобы может меняться
			// то прежде чем ее планировать удаляем тригеры
			removeTriggers(job, CRON_TRIGGER_FILTER);
			scheduler.scheduleJobs(jobData, true);
			log.info("Job {} (ID:{}) has been scheduled successfully", job.getService().getKey(), job.getId());

			if (!job.isActive()) {
				pauseJob(job, CRON_TRIGGER_FILTER);
			} else {
				resumeJob(job, CRON_TRIGGER_FILTER);
			}

		} catch (Exception ex) {
			throw new ServerException(ex.getMessage(), ex);
		}
	}

	@Override
	public void removeJob(ScheduledJob job) {
		try {
			scheduler.deleteJob(new JobKey(job.getId().toString()));
			log.info("Job {} (ID: {}) has been deleted", job.getService().getKey(), job.getId());
		} catch (Exception ex) {
			throw new ServerException(ex.getMessage(), ex);
		}
	}

	@Override
	public void launchNow(ScheduledJob job) {
		try {
			// возможна ситуация, что мы пытаемся вызвать джобу,
			// однако при этом таблицы кварца еще пустые
			JobDetail detail = scheduler.getJobDetail(new JobKey(job.getId().toString()));
			if (detail == null) {
				scheduleJob(job);
			}
			scheduler.triggerJob(new JobKey(job.getId().toString()));
			log.info("Job {} (ID: {}) has been launched immediately", job.getService().getKey(), job.getId());
		} catch (Exception ex) {
			throw new ServerException(ex.getMessage(), ex);
		}
	}

	public String validateCronExpression(String cronExpression) {
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser cronParser = new CronParser(cronDefinition);
		cronParser.parse(cronExpression);
		return cronExpression;
	}

	private void resumeJob(ScheduledJob job, ITriggerFilter filter) throws Exception {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(job.getId().toString()));
		for (Trigger trigger : triggers) {
			if (filter.matches(trigger)) {
				scheduler.resumeTrigger(trigger.getKey());
				log.debug(
						"Job {} (ID: {}): trigger {} has been resumed",
						job.getService().getKey(),
						job.getId(),
						trigger.toString()
				);
			}
		}
		log.info("Job {} (ID: {}) has been resumed", job.getService().getKey(), job.getId());
	}

	private void pauseJob(ScheduledJob job, ITriggerFilter filter) throws Exception {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(job.getId().toString()));
		for (Trigger trigger : triggers) {
			if (filter.matches(trigger)) {
				scheduler.pauseTrigger(trigger.getKey());
				log.debug(
						"Job {} (ID: {}): trigger {} has been paused",
						job.getService().getKey(),
						job.getId(),
						trigger.toString()
				);
			}
		}
		log.info("Job {} (ID: {}) has been paused", job.getService().getKey(), job.getId());
	}

	private void removeTriggers(ScheduledJob job, ITriggerFilter filter) throws Exception {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(job.getId().toString()));
		for (Trigger trigger : triggers) {
			if (filter.matches(trigger)) {
				scheduler.unscheduleJob(trigger.getKey());
				log.debug(
						"Job {} (ID: {}): trigger {} has been unscheduled",
						job.getService().getKey(),
						job.getId(),
						trigger.toString()
				);
			}
		}
		log.info("Job {} (ID: {}) has been unscheduled", job.getService().getKey(), job.getId());
	}

	private interface ITriggerFilter {

		boolean matches(Trigger trigger);

	}

}
