#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Import(io.tesler.core.config.APIConfig.class)
@ComponentScan({"${package}.controller"})
public class ApplicationAPIConfig extends WebMvcConfigurerAdapter {

}
