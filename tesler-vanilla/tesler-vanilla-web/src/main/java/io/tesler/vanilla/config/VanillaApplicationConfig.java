/*-
 * #%L
 * IO Tesler - Vanilla Web
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

package io.tesler.vanilla.config;

import io.tesler.api.task.SecurityContextAwareSchedulingTaskExecutor;
import io.tesler.api.task.SecurityContextAwareTaskScheduler;
import io.tesler.core.config.CoreApplicationConfig;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskExecutor;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;


@Import({
		CoreApplicationConfig.class
})
public class VanillaApplicationConfig {

	private static final Database primaryDatabase = Database.ORACLE;

	@Primary
	@Bean(name = "primaryDS")
	public DataSource dataSource() throws Exception {
		JndiObjectFactoryBean objectFactoryBean = new JndiObjectFactoryBean();
		objectFactoryBean.setJndiName("jdbc/teslerDS");
		objectFactoryBean.setProxyInterface(DataSource.class);
		objectFactoryBean.afterPropertiesSet();
		return (DataSource) objectFactoryBean.getObject();
	}

	@Bean(name = "primaryDatabase")
	public Database primaryDatabase() {
		return primaryDatabase;
	}

	@Bean(name = "vendorAdapter")
	public JpaVendorAdapter vendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(primaryDatabase);
		return vendorAdapter;
	}

	@Bean
	public SchedulingTaskExecutor taskExecutor() throws NamingException {
		DefaultManagedTaskExecutor executor = new DefaultManagedTaskExecutor();
		executor.afterPropertiesSet();
		return new SecurityContextAwareSchedulingTaskExecutor(executor);
	}

	@Bean
	public TaskScheduler taskScheduler() throws NamingException {
		DefaultManagedTaskScheduler scheduler = new DefaultManagedTaskScheduler();
		scheduler.afterPropertiesSet();
		return new SecurityContextAwareTaskScheduler<>(scheduler);
	}


}
