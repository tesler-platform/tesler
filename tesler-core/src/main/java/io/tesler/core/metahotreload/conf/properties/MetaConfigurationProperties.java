/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.metahotreload.conf.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "tesler.meta")
public class MetaConfigurationProperties {

	private boolean enabled = false;

	@NotNull(message = "Path to meta files directory. Supports file: or classpath: prefix. "
			+ "Example of usage is: applicationContext.getResources(directory + widgetPath)")
	private String directory = "classpath*:db/migration/liquibase/data/latest";

	@NotBlank(message = "Path to widget files from MetaConfigurationProperties.directory")
	private String widgetPath = "/**/*.widget.json";

	@NotBlank(message = "Path to view files from MetaConfigurationProperties.directory")
	private String viewPath = "/**/*.view.json";

	@NotBlank(message = "Path to screen files from MetaConfigurationProperties.directory")
	private String screenPath = "/**/*.screen.json";

}
