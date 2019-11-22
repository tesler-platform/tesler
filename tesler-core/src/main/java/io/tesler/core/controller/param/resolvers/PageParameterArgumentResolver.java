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

package io.tesler.core.controller.param.resolvers;

import io.tesler.api.data.PageSpecification;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


public class PageParameterArgumentResolver extends AbstractParameterArgumentResolver {

	public static PageSpecification extract(Map<String, ?> queryParametersMap) {
		String page = getParameterValue(queryParametersMap.get("_page"));
		String limit = getParameterValue(queryParametersMap.get("_limit"));

		if (limit == null && page == null) {
			return PageSpecification.DEFAULT;
		}

		return new PageSpecification(
				NumberUtils.toInt(page, PageSpecification.DEFAULT_PAGE_NUMBER) - 1,
				NumberUtils.toInt(limit, PageSpecification.DEFAULT_PAGE_SIZE),
				true
		);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PageSpecification.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		if (!supportsParameter(parameter)) {
			return PageSpecification.DEFAULT;
		}
		return extract(webRequest.getParameterMap());
	}


}
