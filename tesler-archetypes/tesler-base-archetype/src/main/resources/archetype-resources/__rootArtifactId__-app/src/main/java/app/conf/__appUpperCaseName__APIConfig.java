package ${package}.app.conf;

import io.tesler.core.config.APIConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Import(APIConfig.class)
@ComponentScan({"${package}.controller"})
public class ${appUpperCaseName}APIConfig extends WebMvcConfigurerAdapter {

}
