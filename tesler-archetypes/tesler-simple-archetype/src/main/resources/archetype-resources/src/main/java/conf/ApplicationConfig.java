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
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@RequiredArgsConstructor
@Import({
		CoreApplicationConfig.class
})
@BeanScan(value = {"${package}"})
public class ApplicationConfig implements SchedulingConfigurer {

	private static final Database primaryDatabase = Database.POSTGRESQL;

	private final ApplicationConfigProperties appProps;

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("*")
						.allowedHeaders("*");
			}
		};
	}

	@Primary
	@Bean(name = "primaryDS")
	DataSource primaryDataSource(DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean(name = "primaryDatabase")
	Database primaryDatabase() {
		return primaryDatabase;
	}

	@Bean(name = "vendorAdapter")
	JpaVendorAdapter vendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabasePlatform(PostgreSQL95Dialect.class.getName());
		vendorAdapter.setDatabase(primaryDatabase);
		return vendorAdapter;
	}

	@Bean
	SchedulingTaskExecutor taskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10));
	}

	@Bean
	TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(10));
	}

	@Bean
	RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	ServletRegistrationBean ui() {
		return createRegistration("ui", UIConfig.class, appProps.getUiPath(), appProps.getUiPath() + "/*");
	}

	@Bean
	ServletRegistrationBean api() {
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

	@Bean
	FreeMarkerViewResolver freemarkerViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setExposeRequestAttributes(true);
		resolver.setCache(true);
		resolver.setCacheUnresolved(false);
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html;charset=UTF-8");
		return resolver;
	}
}
