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

import static io.tesler.api.service.session.InternalAuthorizationService.VANILLA;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.session.CoreSessionService;
import io.tesler.api.service.session.TeslerUserDetails;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.controller.BcHierarchyAware;
import io.tesler.core.service.UIService;
import io.tesler.core.service.impl.UserRoleService;
import io.tesler.core.util.session.SessionService;
import io.tesler.core.util.session.SessionUser;
import io.tesler.core.util.session.UserExternalService;
import io.tesler.core.util.session.UserService;
import io.tesler.core.util.session.WebHelper;
import io.tesler.model.core.api.GroupService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.Department;
import io.tesler.model.core.entity.Division;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Вспомогательный класс для получения данных о текущем пользователе (имя, логин, роли и т.п)
 */
@Slf4j
@Service("sessionService")
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

	private final UIService uiService;

	private final Optional<List<UserExternalService>> userExternalServices;

	private final UserService userService;

	private final UserRoleService userRoleService;

	private final JpaDao jpaDao;

	private final CoreSessionService coreSessionService;

	private final BcHierarchyAware bcHierarchyAware;

	private final UserCache userCache;

	private final GroupService groupService;

	// если у нас транзакции нет, то здесь будут происходить
	// постоянные запросы к СУБД, поэтому кешируем
	@Override
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "#root.methodName")
	public User getSessionUser() {
		User user = getUserFromDetails(coreSessionService.getSessionUserDetails(true));
		if (user == null) {
			throw new SessionAuthenticationException("Not authorized");
		}
		return user;
	}

	/**
	 * get current User entity from CoreSessionService, if not found try to found User in
	 * UserExternalService's (that defined by client applications).
	 * @param fallbackToSystem - if enabled, empty authenticated user replaced with system VANILLA user
	 * @return User entity
	 */
	private User getSessionUserInternal(boolean fallbackToSystem) {
		TeslerUserDetails details = coreSessionService.getSessionUserDetails(false);
		if (details != null) {
			return getUserFromDetails(details);
		}
		SessionUser sessionUser = null;
		if (userExternalServices.isPresent()) {
			for (UserExternalService userExternalService : userExternalServices.get()) {
				sessionUser = userExternalService.getSessionUser();
				if (sessionUser != null) {
					break;
				}
			}
		}
		if (sessionUser == null) {
			throw new SessionAuthenticationException("Not authorized");
		}
		User user = userService.getUserByLogin(sessionUser.getId());
		if (user == null && fallbackToSystem) {
			// here the user has already authenticated
			// therefore it seems normal to replace it with a system user
			user = new User();
			user.setId(VANILLA.getId());
		}
		return user;
	}

	@Override
	public Department getSessionUserDepartment() {
		return getSessionUser().getDepartment();
	}

	@Override
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "#root.methodName")
	public LOV getSessionUserRole() {
		TeslerUserDetails userDetails = coreSessionService.getSessionUserDetails(true);
		HttpServletRequest request = WebHelper.getCurrentRequest().orElse(null);
		if (request == null) {
			return userDetails.getUserRole();
		}
		return calculateUserRole(request, userDetails);
	}

	private LOV calculateUserRole(HttpServletRequest request, TeslerUserDetails userDetails) {
		LOV mainRole = userDetails.getUserRole();
		String requestedRole = request.getHeader("RequestedUserRole");
		// в заголовке ничего не указано - возвращаем main
		if (StringUtils.isBlank(requestedRole)) {
			return mainRole;
		}
		// оптимизация: в заголовке совпадает с сессией - возвращаем main
		if (mainRole != null && requestedRole.equals(mainRole.getKey())) {
			return mainRole;
		}
		LOV currentRole = userRoleService.getMatchedRole(getUserFromDetails(userDetails), requestedRole);
		if (currentRole == null) {
			currentRole = userDetails.getUserRole();
		}
		return currentRole;
	}

	@Override
	public void setSessionUserTimezone(LOV timezone) {
		TeslerUserDetails userDetails = coreSessionService.getSessionUserDetails(true);
		if (timezone == null || userDetails == null) {
			return;
		}
		userDetails.setTimezone(timezone);
	}

	@Override
	public void setSessionUserLocale(LOV locale) {
		TeslerUserDetails userDetails = coreSessionService.getSessionUserDetails(true);
		if (locale == null || userDetails == null) {
			return;
		}
		userDetails.setLocale(locale);
	}

	@Override
	public void setSessionUserInternalRole(String role) {
		TeslerUserDetails userDetails = coreSessionService.getSessionUserDetails(true);
		if (role == null || role.isEmpty() || userDetails == null) {
			return;
		}
		User user = getUserFromDetails(userDetails);
		LOV matchedRole = userRoleService.getMatchedRole(user, role);
		if (matchedRole == null) {
			return;
		}
		userDetails.setUserRole(matchedRole);
		userRoleService.updateMainUserRole(user, matchedRole);
	}

	@Override
	public User getEffectiveSessionUser() {
		return getSessionUserInternal(true);
	}

	@Override
	public String getSessionIpAddress() {
		String forwardedFor = "X-Forwarded-For";
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		try {
			if (attrs.getRequest().getHeader(forwardedFor) == null
					|| attrs.getRequest().getHeader(forwardedFor).trim().length() == 0) {
				return attrs.getRequest().getRemoteAddr();
			} else {
				return attrs.getRequest().getHeader(forwardedFor);
			}
		} catch (Exception e) {
			log.warn("Cannot get user ip", e);
			return "";
		}
	}

	@Override
	public Map<String, Boolean> getResponsibilities() {
		return userCache.getResponsibilities(getSessionUser(), getSessionUserRole());
	}

	@Override
	public String getFirstViewFromResponsibilities(String... views) {
		return uiService.getFirstViewFromResponsibilities(getSessionUser(), getSessionUserRole(), views);
	}

	private User getUserFromDetails(final TeslerUserDetails userDetails) {
		return jpaDao.findById(User.class, userDetails.getId());
	}

	@Override
	public String getSessionId() {
		return coreSessionService.getSessionId();
	}

	@Override
	public Division getSessionUserDivision(LOV levelCd) {
		return getSessionUserRoles().stream()
				.filter(userRole -> Objects.equals(getSessionUserRole(), userRole.getInternalRoleCd()))
				.findFirst()
				.map(UserRole::getDivision)
				.map(division -> division.getParentByLevelCode(levelCd)).orElse(null);
	}

	@Override
	public Division getSessionUserDivision() {
		return getSessionUserRoles().stream()
				.filter(userRole -> Objects.equals(getSessionUserRole(), userRole.getInternalRoleCd()))
				.findFirst()
				.map(UserRole::getDivision)
				.orElse(null);
	}

	@Override
	public List<UserRole> getSessionUserRoles() {
		return getSessionUser().getUserRoleList();
	}

	/**
	 * Возвращает доступные вью текущего скрина
	 */
	@Override
	public Collection<String> getCurrentScreenViews() {
		return getViews(bcHierarchyAware.getScreenName());
	}

	@Override
	public List<String> getViews(final String screenName) {
		return userCache.getViews(screenName, getSessionUser(), getSessionUserRole());
	}

	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "#root.methodName")
	@Override
	public Set<Long> getAllUserGroups() {
		return groupService.getUserAllGroups(getSessionUser());
	}

	@Component
	@RequiredArgsConstructor
	public static class UserCache {

		private final UIService uiService;

		@Cacheable(
				cacheNames = {CacheConfig.USER_CACHE},
				key = "{#root.methodName, #user.id, #userRole}"
		)
		public Map<String, Boolean> getResponsibilities(final User user, final LOV userRole) {
			return uiService.getResponsibilities(
					user,
					userRole
			);
		}

		@Cacheable(
				cacheNames = {CacheConfig.USER_CACHE},
				key = "{#root.methodName, #screenName, #user.id, #userRole}"
		)
		public List<String> getViews(final String screenName, final User user, final LOV userRole) {
			return uiService.getViews(
					screenName,
					user,
					userRole
			);
		}

	}

}
