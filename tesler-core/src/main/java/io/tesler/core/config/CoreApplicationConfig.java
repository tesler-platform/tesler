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

package io.tesler.core.config;

import io.tesler.core.metahotreload.conf.MetaHotReloadConfiguration;
import io.tesler.core.service.ResponsibilitiesService;
import io.tesler.core.service.impl.ResponsibilitiesServiceImpl;
import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.service.BaseEntityListenerDelegate;
import io.tesler.model.core.service.TeslerBaseEntityListenerDelegate;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;


@EnableAspectJAutoProxy
@BeanScan({"io.tesler"})
@EnableSpringConfigured
@ImportAutoConfiguration({
		MetaHotReloadConfiguration.class
})
public class CoreApplicationConfig {

	@Bean
	@ConditionalOnMissingBean
	public ResponsibilitiesService responsibilitiesService(JpaDao jpaDao) {
		return new ResponsibilitiesServiceImpl(jpaDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public BaseEntityListenerDelegate baseEntityListenerDelegate(CurrentUserAware currentUserAware) {
		return new TeslerBaseEntityListenerDelegate(currentUserAware);
	}
}
