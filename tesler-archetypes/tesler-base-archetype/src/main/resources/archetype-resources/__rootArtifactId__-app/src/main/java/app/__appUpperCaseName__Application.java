package ${package}.app;

import ${package}.app.conf.${appUpperCaseName}ApplicationConfig;
import ${package}.app.conf.${appUpperCaseName}RedirectConfig;
import ${package}.app.conf.${appUpperCaseName}SecurityConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ${appUpperCaseName}Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
				.sources(
						${appUpperCaseName}ApplicationConfig.class,
						${appUpperCaseName}RedirectConfig.class,
						${appUpperCaseName}SecurityConfig.class
				).build().run(args);
	}

}
