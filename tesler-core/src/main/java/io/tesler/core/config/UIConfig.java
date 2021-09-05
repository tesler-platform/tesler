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

import io.tesler.core.config.properties.UIProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@EnableConfigurationProperties(UIProperties.class)
@EnableWebMvc
public class UIConfig implements WebMvcConfigurer {

	@Autowired
	private UIProperties uiProperties;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (uiProperties.getUseServletContextPath()) {
			registry.addResourceHandler("/**").addResourceLocations("classpath:/ui/");
		} else {
			registry.addResourceHandler(uiProperties.getPath() + "/**").addResourceLocations("classpath:/ui/");
		}
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		if (uiProperties.getUseServletContextPath()) {
			registry.addRedirectViewController("/ui", "/ui/");
			registry.addViewController("/").setViewName("index");
			registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		} else {
			registry.addRedirectViewController("/", uiProperties.getPath() + "/");
			registry.addRedirectViewController(uiProperties.getPath(), uiProperties.getPath() + "/");
			registry.addViewController(uiProperties.getPath() + "/").setViewName("index");
			registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		}
	}

	@Bean
	public FreeMarkerViewResolver freemarkerViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setExposeRequestAttributes(true);
		resolver.setCache(true);
		resolver.setCacheUnresolved(false);
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html;charset=UTF-8");
		return resolver;
	}

}
