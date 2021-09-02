/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2020 Tesler Contributors
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

package io.tesler.quartz.config;

import io.tesler.quartz.impl.QuartzJobFactory;
import io.tesler.quartz.impl.QuartzSchedulerListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

class SchedulerConfigTest {

	@Mock
	Environment environment;

	@Mock
	ApplicationContext applicationContext;

	@Mock
	QuartzJobFactory quartzJobFactory;

	@Mock
	QuartzSchedulerListener quartzSchedulerListener;

	@Mock
	PlatformTransactionManager transactionManager;

	@Mock
	TaskExecutor taskExecutor;

	SchedulerProperties schedulerProperties = new SchedulerProperties();

	SchedulerConfig schedulerConfig;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		schedulerConfig = new SchedulerConfig(
				environment,
				applicationContext,
				quartzJobFactory,
				quartzSchedulerListener,
				transactionManager,
				taskExecutor,
				schedulerProperties
		);
	}

	@Test
	void testQuartzScheduler() {
		SchedulerFactoryBean result = schedulerConfig.quartzScheduler(null, Database.DEFAULT);
		Assertions.assertEquals(SchedulerFactoryBean.class, result.getClass());
	}

	@Test
	void testQuartzSchedulerService() {
		TransactionProxyFactoryBean result = schedulerConfig.quartzSchedulerService(
				schedulerConfig.quartzScheduler(null, Database.DEFAULT)
		);
		Assertions.assertEquals(TransactionProxyFactoryBean.class, result.getClass());
	}

}
