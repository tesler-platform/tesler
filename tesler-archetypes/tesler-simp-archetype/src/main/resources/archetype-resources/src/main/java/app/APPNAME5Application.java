#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app;

import ${package}.app.conf.APPNAME5ApplicationConfig;
import ${package}.app.conf.APPNAME5RedirectConfig;
import ${package}.app.conf.APPNAME5SecurityConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class APPNAME5Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
				.sources(
						APPNAME5ApplicationConfig.class,
						APPNAME5RedirectConfig.class,
						APPNAME5SecurityConfig.class
				).build().run(args);
	}

}
