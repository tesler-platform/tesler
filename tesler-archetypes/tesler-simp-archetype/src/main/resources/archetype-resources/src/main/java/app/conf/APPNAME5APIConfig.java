#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.conf;

import io.tesler.core.config.APIConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Import(APIConfig.class)
@ComponentScan({"${package}.controller"})
public class APPNAME5APIConfig extends WebMvcConfigurerAdapter {

}
