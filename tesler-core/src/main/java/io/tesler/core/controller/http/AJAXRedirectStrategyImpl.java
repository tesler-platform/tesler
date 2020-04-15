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

import static io.tesler.api.system.SystemSettings.systemSettings;

import io.tesler.api.data.dictionary.CoreDictionaries.SystemPref;
import io.tesler.api.data.dto.RedirectDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;


@Component
public class AJAXRedirectStrategyImpl extends DefaultRedirectStrategy implements AJAXRedirectStrategy {

	public static final String UI_HASH = "#/";

	public static final String SLASH = "/";

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper objectMapper;

	private final RequestMatcher ajaxRequestMatcher;

	public AJAXRedirectStrategyImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.ajaxRequestMatcher = createAJAXRequestMatcher();
	}

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		String redirectUrl = calculateRedirectUrl(request, url);
		if (!isAjaxRequest(request)) {
			doSendRedirect(request, response, redirectUrl);
		}
		RedirectDTO redirect = new RedirectDTO(response.encodeRedirectURL(redirectUrl));
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), redirect);
	}

	@SneakyThrows
	protected void doSendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
		response.setHeader(
				HttpHeaders.LOCATION,
				response.encodeRedirectURL(url)
		);
		response.sendError(HttpStatus.FOUND.value());
	}

	public String calculateRedirectUrl(HttpServletRequest request, String url) {
		// начинается со / или schema://
		if (UrlUtils.isValidRedirectUrl(url)) {
			return url;
		}
		String uiLocation = getUILocation(request);
		if (uiLocation == null) {
			uiLocation = request.getContextPath();
		}
		if (!uiLocation.endsWith(SLASH)) {
			uiLocation += SLASH;
		}
		if (StringUtils.isBlank(url)) {
			return uiLocation;
		}
		return uiLocation + url;
	}

	@Override
	public boolean isAjaxRequest(HttpServletRequest request) {
		return ajaxRequestMatcher.matches(request);
	}

	@Override
	public String getReferrer(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.REFERER);
	}

	@Override
	public String getUILocation(HttpServletRequest request) {
		if (!isAjaxRequest(request)) {
			return getSystemUrl();
		}
		String referrer = getReferrer(request);
		if (StringUtils.isBlank(referrer)) {
			return getSystemUrl();
		}
		return addUiHash(referrer);
	}

	@Override
	public String getSystemUrl() {
		return addUiHash(systemSettings().getValue(SystemPref.SYSTEM_URL));
	}

	private String addUiHash(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (!url.endsWith(UI_HASH)) {
			return url + UI_HASH;
		}
		return url;
	}

	private RequestMatcher createAJAXRequestMatcher() {
		MediaTypeRequestMatcher jsonRequest = new MediaTypeRequestMatcher(
				new HeaderContentNegotiationStrategy(),
				MediaType.APPLICATION_JSON
		);
		jsonRequest.setUseEquals(true);
		jsonRequest.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
		RequestMatcher xmlHttpRequest = new RequestHeaderRequestMatcher(
				"X-Requested-With",
				"XMLHttpRequest"
		);
		RequestMatcher clientIdRequest = new RequestHeaderRequestMatcher(
				"ClientId",
				null
		);
		return new OrRequestMatcher(
				jsonRequest,
				clientIdRequest,
				xmlHttpRequest
		);
	}

}
