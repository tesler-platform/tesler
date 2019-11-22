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

package io.tesler.core.controller.http;

import io.tesler.api.service.session.CoreSessionService;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Component
public class FillLogParametersInterceptor extends HandlerInterceptorAdapter {

	public static final String REQUEST_ID = "requestId";

	public static final String USER_ID = "userId";

	public static final String URI = "uri";

	public static final String USER_ID_VALUE_PREFIX = "USER";

	@Autowired
	private CoreSessionService coreSessionService;

	@Override
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler
	) throws Exception {
		final String token;
		if (!StringUtils.isEmpty(REQUEST_ID) && !StringUtils.isEmpty(request.getHeader(REQUEST_ID))) {
			token = request.getHeader(REQUEST_ID);
		} else {
			token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		}
		String sessionUserId = USER_ID_VALUE_PREFIX + coreSessionService.getSessionUserId();
		MDC.put(REQUEST_ID, token);
		MDC.put(USER_ID, sessionUserId);
		MDC.put(URI, request.getRequestURI());
		if (!StringUtils.isEmpty(REQUEST_ID)) {
			response.addHeader(REQUEST_ID, token);
		}
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		MDC.remove(REQUEST_ID);
		MDC.remove(USER_ID);
		MDC.remove(URI);
		super.postHandle(request, response, handler, modelAndView);
	}

}
