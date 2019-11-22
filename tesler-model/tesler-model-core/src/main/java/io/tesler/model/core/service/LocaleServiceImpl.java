/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.service;

import io.tesler.api.data.dictionary.CoreDictionaries.SystemPref;
import io.tesler.api.service.LocaleService;
import io.tesler.api.system.ISystemSettingChangeEventListener;
import io.tesler.api.system.SystemSettingChangedEvent;
import io.tesler.api.system.SystemSettings;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.Getter;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;


@Service(LocaleService.SERVICE_NAME)
public class LocaleServiceImpl implements LocaleService, ISystemSettingChangeEventListener {

	private final SystemSettings systemSettings;

	@Getter
	private Set<String> supportedLanguages;

	public LocaleServiceImpl(SystemSettings systemSettings) {
		this.systemSettings = systemSettings;
		this.supportedLanguages = checkLanguages(systemSettings.getListValue(SystemPref.SUPPORTED_LANGUAGES));
	}

	@Override
	public void onApplicationEvent(SystemSettingChangedEvent event) {
		if (SystemPref.SUPPORTED_LANGUAGES.equals(event.getSetting())) {
			supportedLanguages = checkLanguages(systemSettings.getListValue(SystemPref.SUPPORTED_LANGUAGES));
		}
	}

	@Override
	public boolean isLanguageSupported(String language) {
		return supportedLanguages.contains(language);
	}

	private Set<String> checkLanguages(List<String> supportedLanguages) {
		if (supportedLanguages.isEmpty()) {
			throw new IllegalStateException("Please specify SUPPORTED_LANGUAGES in system settings");
		}
		LocaleService.defaultLocale.set(LocaleUtils.toLocale(supportedLanguages.get(0)));
		LocaleContextHolder.setDefaultLocale(defaultLocale.get());
		return Collections.unmodifiableSet(new LinkedHashSet<>(supportedLanguages));
	}

	@Override
	public Locale getDefaultLocale() {
		return LocaleService.defaultLocale.get();
	}


}
