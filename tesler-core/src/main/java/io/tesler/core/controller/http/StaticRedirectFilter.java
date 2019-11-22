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

import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.UrlUtils;


public class StaticRedirectFilter extends HeaderWriterFilter {

	public StaticRedirectFilter(String location) {
		this(location, true);
	}

	public StaticRedirectFilter(String location, boolean relative) {
		super(Collections.singletonList((req, res) -> sendRedirect(req, res, location, relative)));
	}

	@SneakyThrows
	private static void sendRedirect(HttpServletRequest req, HttpServletResponse res, String location, boolean relative) {
		String redirect = calculateRedirectUrl(req.getContextPath(), location, relative);
		res.setStatus(HttpStatus.FOUND.value());
		res.setHeader(HttpHeaders.LOCATION, redirect);
	}

	public static String calculateRedirectUrl(String contextPath, String url, boolean relative) {
		String result = url;
		if (!UrlUtils.isAbsoluteUrl(result)) {
			if (StringUtils.isBlank(result)) {
				result = "/";
			}
			if (!result.startsWith("/")) {
				result = "/" + result;
			}
			if (relative) {
				result = contextPath + result;
			}
		}
		return result;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		super.doFilterInternal(request, response, (req, res) -> {
			// nop
		});
	}

}
