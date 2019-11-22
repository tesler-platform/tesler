package ${package}.app.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "${rootArtifactId}app")
public class ${appUpperCaseName}Configuration {

	public static final String UI_PATH = "${rootArtifactId}app.ui-path";

	private String uiPath;

}
