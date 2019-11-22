/*-
 * #%L
 * IO Tesler - Vanilla APP
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

package io.tesler.vanilla;

import io.tesler.api.task.SecurityContextAwareSchedulingTaskExecutor;
import io.tesler.api.task.SecurityContextAwareTaskScheduler;
import io.tesler.core.config.APIConfig;
import io.tesler.core.config.CoreApplicationConfig;
import io.tesler.core.config.UIConfig;
import io.tesler.vanilla.config.VanillaPersistenceConfig;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


@Import({
		CoreApplicationConfig.class,
		VanillaPersistenceConfig.class
})
@ImportAutoConfiguration({
		ServletWebServerFactoryAutoConfiguration.class,
		DataSourceAutoConfiguration.class
})
public class StandaloneApplicationConfig {

	@Primary
	@Bean(name = "primaryDS")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public SchedulingTaskExecutor taskExecutor() {
		return new SecurityContextAwareSchedulingTaskExecutor(
				new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10))
		);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new SecurityContextAwareTaskScheduler<>(
				new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(10))
		);
	}

	@Bean
	public ServletRegistrationBean ui() {
		return createRegistration("ui", UIConfig.class, "/ui", "/ui/*");
	}

	@Bean
	public ServletRegistrationBean api() {
		return createRegistration("api", APIConfig.class, "/api/v1/*");
	}

	private ServletRegistrationBean createRegistration(String name, Class<?> configClass, String... urlMappings) {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(configClass);
		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, urlMappings);
		servletRegistrationBean.setName(name);
		return servletRegistrationBean;
	}

}
