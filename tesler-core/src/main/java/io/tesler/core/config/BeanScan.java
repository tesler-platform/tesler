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

import io.tesler.api.util.spring.ServiceBasedComponentExcludeFilter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(
		includeFilters = {
				@Filter(value = {Aspect.class}, type = FilterType.ANNOTATION)
		},
		excludeFilters = {
				@Filter(value = {Controller.class, ControllerAdvice.class}, type = FilterType.ANNOTATION),
				@Filter(value = {
						WebMvcConfigurer.class,
						WebSecurityConfigurerAdapter.class
				}, type = FilterType.ASSIGNABLE_TYPE),
				@Filter(value = ServiceBasedComponentExcludeFilter.class, type = FilterType.CUSTOM),
		}
)
public @interface BeanScan {

	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] value() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "useDefaultFilters")
	boolean useDefaultFilters() default true;

}
