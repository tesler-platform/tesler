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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.service.LocaleService;
import io.tesler.api.service.session.CoreSessionService;
import io.tesler.core.controller.http.FillLogParametersInterceptor;
import io.tesler.core.controller.param.resolvers.LocaleParameterArgumentResolver;
import io.tesler.core.controller.param.resolvers.PageParameterArgumentResolver;
import io.tesler.core.controller.param.resolvers.QueryParametersResolver;
import io.tesler.core.controller.param.resolvers.TimeZoneParameterArgumentResolver;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@ControllerScan({"io.tesler.core.controller"})
@AllArgsConstructor
public class APIConfig implements WebMvcConfigurer {

	protected final FillLogParametersInterceptor debugModeInterceptor;

	@Qualifier("teslerObjectMapper")
	protected final ObjectMapper objectMapper;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new PageParameterArgumentResolver());
		argumentResolvers.add(new QueryParametersResolver());
		argumentResolvers.add(new TimeZoneParameterArgumentResolver());
		argumentResolvers.add(new LocaleParameterArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(debugModeInterceptor).addPathPatterns("/**");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(268435456L);
		resolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
		return resolver;
	}

	@Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	public LocaleResolver localeResolver(CoreSessionService coreSessionService, LocaleService localeService) {
		return new EnhancedLocaleResolver(coreSessionService, localeService);
	}

}
