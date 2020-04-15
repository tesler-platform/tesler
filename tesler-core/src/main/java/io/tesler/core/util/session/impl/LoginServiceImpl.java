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

package io.tesler.core.util.session.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.data.dictionary.CoreDictionaries.SystemPref;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.system.SystemSettings;
import io.tesler.core.dto.LoggedUser;
import io.tesler.core.dto.data.view.ScreenDTO;
import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.core.service.UIService;
import io.tesler.core.service.ViewService;
import io.tesler.core.service.ResponsibilitiesService;
import io.tesler.core.service.impl.UserRoleService;
import io.tesler.core.util.session.LoginService;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper objectMapper;

	private final SessionService sessionService;

	private final UserRoleService userRoleService;

	private final ResponsibilitiesService respService;

	private final SystemSettings systemSettings;

	private final UIService uiService;

	private final ViewService viewService;

	/**
	 * Получить информацию о пользователе
	 *
	 * @param role Роль
	 * @return LoggedUser
	 */
	@Override
	public LoggedUser getLoggedUser(String role) {

		sessionService.setSessionUserInternalRole(role);

		User user = sessionService.getSessionUser();
		LOV activeUserRole = sessionService.getSessionUserRole();

		return LoggedUser.builder()
				.sessionId(sessionService.getSessionId())
				.user(user)
				.activeRole(activeUserRole.getKey())
				.roles(userRoleService.getUserRoles(user))
				.screens(getScreens(user, activeUserRole))
				.userSettings(uiService.getUserSettings())
				.featureSettings(this.getFeatureSettings())
				.systemUrl(systemSettings.getValue(SystemPref.SYSTEM_URL))
				.language(LocaleContextHolder.getLocale().getLanguage())
				.timezone(LocaleContextHolder.getTimeZone().getID())
				.build();
	}

	/**
	 * Получить список доступных экранов пользователя в соответствии с ролью
	 *
	 * @param user Пользователь
	 * @param userRole Роль
	 * @return JsonNode
	 */
	private List<ScreenResponsibility> getScreens(User user, LOV userRole) {
		try {
			List<ScreenResponsibility> result = new ArrayList<>();
			String screens = respService.getListScreensByUser(user, userRole);
			if (StringUtils.isNotBlank(screens)) {
				result.addAll(objectMapper.readValue(screens, ScreenResponsibility.LIST_TYPE_REFERENCE));
			}
			List<ScreenResponsibility> commonScreens = uiService.getCommonScreens();
			if (commonScreens != null) {
				result.addAll(commonScreens);
			}
			result.forEach(resp -> {
				String screenName = resp.getName();
				ScreenDTO screenDto = viewService.getScreen(screenName);
				resp.setMeta(screenDto);
			});
			return result;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	/**
	 * Получить список доступных функциональностей
	 *
	 * @return List
	 */
	public Collection<SimpleDictionary> getFeatureSettings() {
		return systemSettings.select(key -> key.startsWith("FEATURE_"))
				.map(p -> new SimpleDictionary(p.getKey(), p.getValue()))
				.collect(Collectors.toList());
	}

}
