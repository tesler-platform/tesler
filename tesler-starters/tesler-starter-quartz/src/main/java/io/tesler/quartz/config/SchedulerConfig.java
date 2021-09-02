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

package io.tesler.quartz.config;

import com.google.common.base.Objects;
import io.tesler.quartz.db.ProxyAwarePrivilegedDataSource;
import java.util.Properties;
import javax.sql.DataSource;

import io.tesler.quartz.impl.QuartzJobFactory;
import io.tesler.quartz.impl.QuartzSchedulerListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.jpa.vendor.Database;
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
@EnableConfigurationProperties(SchedulerProperties.class)
public class SchedulerConfig {

	private final Environment environment;

	private final ApplicationContext applicationContext;

	private final QuartzJobFactory quartzJobFactory;

	private final QuartzSchedulerListener quartzSchedulerListener;

	private final PlatformTransactionManager transactionManager;

	private final TaskExecutor taskExecutor;

	private final SchedulerProperties schedulerProperties;

	@Bean
	public SchedulerFactoryBean quartzScheduler(@Qualifier("primaryDS") DataSource primaryDS,
			@Qualifier("primaryDatabase") Database primaryDatabase) {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setAutoStartup(!environment.acceptsProfiles("quartzDisable"));
		schedulerFactoryBean.setStartupDelay((int) schedulerProperties.getStartupDelay().getSeconds());
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setApplicationContext(applicationContext);
		schedulerFactoryBean.setTaskExecutor(taskExecutor);
		schedulerFactoryBean.setJobFactory(quartzJobFactory);
		schedulerFactoryBean.setTransactionManager(transactionManager);
		schedulerFactoryBean.setSchedulerName(schedulerProperties.getSchedulerName());
		schedulerFactoryBean.setDataSource(
				new TransactionAwareDataSourceProxy(
						new ProxyAwarePrivilegedDataSource(primaryDS)
				)
		);
		Properties quartzProperties = new Properties();
		quartzProperties.setProperty("org.quartz.scheduler.skipUpdateCheck", String.valueOf(true));
		quartzProperties.setProperty("org.quartz.jobStore.class", LocalDataSourceJobStore.class.getName());
		quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		if (Objects.equal(primaryDatabase, Database.POSTGRESQL)) {
			quartzProperties
					.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		}
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
