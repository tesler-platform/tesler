#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.conf;

import io.tesler.core.config.BeanScan;
import io.tesler.core.config.CoreApplicationConfig;
import io.tesler.core.config.UIConfig;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.hibernate.dialect.PostgreSQL95Dialect;

@RequiredArgsConstructor
@Import({
		CoreApplicationConfig.class,
		PersistenceConfig.class
})
@ImportAutoConfiguration({
		org.springframework.boot.autoconfigure.tesler.liquibase.LiquibaseAutoConfiguration.class
})
@EnableAutoConfiguration(
		exclude = {
				org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class,
				ElasticsearchAutoConfiguration.class
		}
)
@BeanScan(value = {"${package}"})
public class ApplicationConfig implements SchedulingConfigurer {

	private static final Database primaryDatabase = Database.POSTGRESQL;

	private final Configuration configuration;

	@Bean
	public InstrumentationLoadTimeWeaver loadTimeWeaver() {
		return new InstrumentationLoadTimeWeaver();
	}

	@Primary
	@Bean(name = "primaryDS")
	public DataSource primaryDataSource(DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean(name = "primaryDatabase")
	public Database primaryDatabase() {
		return primaryDatabase;
	}

	@Bean(name = "vendorAdapter")
	public JpaVendorAdapter vendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabasePlatform(PostgreSQL95Dialect.class.getName());
		vendorAdapter.setDatabase(primaryDatabase);
		return vendorAdapter;
	}

	@Bean
	public SchedulingTaskExecutor taskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10));
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(10));
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	public ServletRegistrationBean ui() {
		return createRegistration(
				"ui",
				UIConfig.class,
				configuration.getUiPath(),
				String.format("%s/*", configuration.getUiPath())
		);
	}

	@Bean
	public ServletRegistrationBean api() {
		return createRegistration("api", ApplicationAPIConfig.class, "/api/v1/*");
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

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

	}

}
