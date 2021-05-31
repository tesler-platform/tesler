#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import ${package}.conf.ApplicationConfig;
import ${package}.conf.RedirectConfig;
import ${package}.conf.SecurityConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
				.sources(
						ApplicationConfig.class,
						RedirectConfig.class,
						SecurityConfig.class
				).build().run(args);
	}

}
