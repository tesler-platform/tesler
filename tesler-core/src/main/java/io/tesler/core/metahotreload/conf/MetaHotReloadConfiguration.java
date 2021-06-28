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

package io.tesler.core.metahotreload.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.core.metahotreload.MetaHotReloadService;
import io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties;
import io.tesler.core.metahotreload.service.*;
import io.tesler.model.core.dao.JpaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(MetaConfigurationProperties.class)
@Configuration
public class MetaHotReloadConfiguration {

	@Bean
	MetaResourceReaderService metaResourceReaderService(
			ApplicationContext applicationContext,
			MetaConfigurationProperties config,
			@Qualifier("teslerObjectMapper") ObjectMapper objMapper) {
		return new MetaResourceReaderService(applicationContext, config, objMapper);
	}

	@Bean
	public MetaHotReloadService refreshMeta(
			MetaResourceReaderService metaResourceReaderService,
			InternalAuthorizationService authzService,
			TransactionService txService,
			JpaDao jpaDao,
			WidgetUtil widgetUtil,
			WidgetPropertyUtil widgetPropertyUtil,
			ViewAndViewWidgetUtil viewAndViewWidgetUtil,
			ScreenAndNavigationGroupAndNavigationViewUtil screenAndNavigationGroupAndNavigationViewUtil) {
		return new MetaHotReloadServiceImpl(
				metaResourceReaderService,
				authzService,
				txService,
				jpaDao,
				widgetUtil,
				widgetPropertyUtil,
				viewAndViewWidgetUtil,
				screenAndNavigationGroupAndNavigationViewUtil);
	}
}
