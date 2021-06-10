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

import io.tesler.api.service.LocaleService;
import io.tesler.api.service.session.CoreSessionService;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;


@Component
public class EnhancedLocaleResolver extends CookieLocaleResolver {

	private final CoreSessionService coreSessionService;

	private final LocaleService localeService;

	public EnhancedLocaleResolver(CoreSessionService coreSessionService, LocaleService localeService) {
		this.coreSessionService = coreSessionService;
		this.localeService = localeService;
		setRejectInvalidCookies(false);
		setLanguageTagCompliant(false);
		setCookieName("locale");
	}

	@Override
	protected Locale parseLocaleValue(String localeValue) {
		Locale locale = super.parseLocaleValue(localeValue);
		if (locale == null || !localeService.isLanguageSupported(locale.getLanguage())) {
			return null;
		}
		return locale;
	}

	@Override
	protected Locale determineDefaultLocale(HttpServletRequest request) {
		Locale locale = coreSessionService.getLocale(super.determineDefaultLocale(request));
		if (localeService.isLanguageSupported(locale.getLanguage())) {
			return locale;
		}
		return localeService.getDefaultLocale();
	}

	@Override
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
		return coreSessionService.getTimeZone(super.determineDefaultTimeZone(request));
	}

}
