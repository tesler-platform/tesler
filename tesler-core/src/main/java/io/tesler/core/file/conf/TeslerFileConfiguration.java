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

package io.tesler.core.file.conf;


import io.tesler.core.file.controller.TeslerFileController;
import io.tesler.core.file.controller.TeslerFileControllerSimple;
import io.tesler.core.file.service.TeslerFileService;
import io.tesler.core.file.service.TeslerFileServiceSimple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeslerFileConfiguration {

	@Bean
	@ConditionalOnMissingBean(TeslerFileController.class)
	TeslerFileController teslerFileController(TeslerFileService teslerFileService) {
		return new TeslerFileControllerSimple(teslerFileService);
	}

	@Bean
	@ConditionalOnMissingBean(TeslerFileService.class)
	TeslerFileService teslerFileService(@Value("${tesler.file.folder:}") String fileFolder) {
		return new TeslerFileServiceSimple(fileFolder);
	}
}
