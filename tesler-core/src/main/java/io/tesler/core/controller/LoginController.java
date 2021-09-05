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

package io.tesler.core.controller;

import static io.tesler.core.config.properties.APIProperties.TESLER_API_PATH_SPEL;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.LocaleService;
import io.tesler.api.service.session.CoreSessionService;
import io.tesler.api.util.locale.LocaleSpecification;
import io.tesler.api.util.tz.TimeZoneSpecification;
import io.tesler.core.dto.LoggedUser;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.util.session.LoginService;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(TESLER_API_PATH_SPEL)
public class LoginController {

	private final LocaleService localeService;

	private final LocaleResolver localeResolver;

	private final LoginService loginService;

	private final ResponseBuilder resp;

	private final CoreSessionService coreSessionService;

	/**
	 * Authenticate user in the application; actual authentication performed by Spring Security in client app
	 *
	 * @param request
	 * @param response
	 * @param role Required role; TODO: Used to switch role from UI, consider separate endpoint for that
	 * @param timezone
	 * @param locale Required locale TODO: Consider separate endpoint to switch language from UI
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/login")
	public LoggedUser get(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(name = "role", required = false) String role,
			TimeZoneSpecification timezone,
			LocaleSpecification locale) {
		resetLocaleContext(request, response, timezone, locale);
		return loginService.getLoggedUser(role);
	}

	/**
	 * Logout endpoint, actual session is usually cleared by client application by specifying this endpoint as
	 * `logoutUrl` of Spring Security configuration
	 * @return Empty list
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/logout")
	public ResponseDTO logout() {
		return resp.build(new ArrayList<>());
	}

	protected void resetLocaleContext(
			HttpServletRequest request,
			HttpServletResponse response,
			TimeZoneSpecification timezone,
			LocaleSpecification locale
	) {
		if (localeResolver instanceof LocaleContextResolver) {
			LocaleContextResolver localeContextResolver = (LocaleContextResolver) localeResolver;
			LocaleContext context = localeContextResolver.resolveLocaleContext(request);
			localeContextResolver.setLocaleContext(
					request,
					response,
					new SimpleTimeZoneAwareLocaleContext(
							getLocale(context, locale),
							getTimezone(context, timezone)
					)
			);
		}
	}

	protected Locale getLocale(LocaleContext context, LocaleSpecification locale) {
		List<Locale> candidates = new ArrayList<>();
		// explicitly specified in request
		Optional.ofNullable(locale).map(LocaleSpecification::getLocale).map(LOV::getKey)
				.map(StringUtils::parseLocaleString).ifPresent(candidates::add);
		// user settings
		Optional.ofNullable(coreSessionService.getLocale(null))
				.ifPresent(candidates::add);
		// browser information (cookies or Accept-language)
		Optional.of(context.getLocale()).ifPresent(candidates::add);
		return candidates.stream().filter(
				l -> localeService.isLanguageSupported(l.getLanguage())
		).findFirst().orElseGet(localeService::getDefaultLocale);
	}

	protected TimeZone getTimezone(LocaleContext context, TimeZoneSpecification timezone) {
		return Optional.ofNullable(timezone)
				.map(TimeZoneSpecification::getTimeZone)
				.map(LOV::getKey)
				.map(ZoneId::of)
				.map(TimeZone::getTimeZone)
				.orElseGet(() -> coreSessionService.getTimeZone(getTimeZone(context)));
	}

	private TimeZone getTimeZone(LocaleContext context) {
		if (context instanceof TimeZoneAwareLocaleContext) {
			return ((TimeZoneAwareLocaleContext) context).getTimeZone();
		}
		return null;
	}

}
