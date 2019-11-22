/*-
 * #%L
 * IO Tesler - Liquibase
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

package io.tesler.db.migration.liquibase.spring;

import java.io.IOException;
import liquibase.logging.LogService;
import liquibase.logging.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

@Slf4j
public class SpringLiquibase extends liquibase.integration.spring.SpringLiquibase {

	private static final String MANIFEST_MF = "META-INF/MANIFEST.MF";

	@Override
	protected SpringResourceOpener createResourceOpener() {
		return new SpringResourceOpenerCustom(getChangeLog());
	}

	public class SpringResourceOpenerCustom extends SpringResourceOpener {

		public SpringResourceOpenerCustom(String parentFile) {
			super(parentFile);
		}

		@Override
		protected void init() {
			super.init();
			try {
				// правильное вычисление ресурсов - ищем все jar в classpath
				Resource[] resources = getResources(String.format("classpath*:/%s", MANIFEST_MF));
				for (Resource res : resources) {
					addResource(res);
				}
			} catch (IOException e) {
				LogService.getLog(getClass()).warning(LogType.LOG, "Error initializing SpringLiquibase", e);
			}
		}

		private void addResource(Resource resource) throws IOException {
			if (resource == null) {
				return;
			}
			String externalForm = resource.getURL().toExternalForm();
			externalForm = externalForm.replace(MANIFEST_MF, "");
			addRootPath(externalForm);
		}

		private void addRootPath(String path) {
			if (!super.getRootPaths().contains(path)) {
				super.getRootPaths().add(path);
			}
		}

		private Resource[] getResources(String foundPackage) throws IOException {
			return ResourcePatternUtils.getResourcePatternResolver(getResourceLoader()).getResources(foundPackage);
		}

	}

}
