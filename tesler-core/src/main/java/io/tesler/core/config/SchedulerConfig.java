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

package io.tesler.core.config;

import io.tesler.core.ext.quartz.QuartzJobFactory;
import io.tesler.core.ext.quartz.QuartzSchedulerListener;
import io.tesler.core.util.db.ProxyAwarePrivilegedDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;


@RequiredArgsConstructor
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig {

	private final Environment environment;

	private final ApplicationContext applicationContext;

	private final QuartzJobFactory quartzJobFactory;

	private final QuartzSchedulerListener quartzSchedulerListener;

	private final PlatformTransactionManager transactionManager;

	private final TaskExecutor taskExecutor;

	@Bean
	public SchedulerFactoryBean quartzScheduler(@Qualifier("primaryDS") DataSource primaryDS) {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setAutoStartup(!environment.acceptsProfiles("quartzDisable"));
		schedulerFactoryBean.setStartupDelay(600);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setApplicationContext(applicationContext);
		schedulerFactoryBean.setTaskExecutor(taskExecutor);
		schedulerFactoryBean.setJobFactory(quartzJobFactory);
		schedulerFactoryBean.setTransactionManager(transactionManager);
		schedulerFactoryBean.setSchedulerName("teslerScheduler");
		schedulerFactoryBean.setDataSource(
				new TransactionAwareDataSourceProxy(
						new ProxyAwarePrivilegedDataSource(primaryDS)
				)
		);
		Properties quartzProperties = new Properties();
		quartzProperties.setProperty("org.quartz.scheduler.skipUpdateCheck", String.valueOf(true));
		quartzProperties.setProperty("org.quartz.jobStore.class", LocalDataSourceJobStore.class.getName());
		quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		schedulerFactoryBean.setQuartzProperties(quartzProperties);
		schedulerFactoryBean.setSchedulerListeners(quartzSchedulerListener);
		return schedulerFactoryBean;
	}

	@Bean
	public TransactionProxyFactoryBean quartzSchedulerService(SchedulerFactoryBean quartzScheduler) {
		TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
		transactionProxyFactoryBean.setTransactionManager(transactionManager);
		transactionProxyFactoryBean.setTarget(quartzScheduler.getObject());
		Properties properties = new Properties();
		properties.setProperty("*", "PROPAGATION_REQUIRED");
		transactionProxyFactoryBean.setTransactionAttributes(properties);
		return transactionProxyFactoryBean;
	}

}
