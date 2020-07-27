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

package io.tesler.core.service.impl;

import static java.lang.String.format;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.exception.ServerException;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.LoginRole;
import io.tesler.model.core.entity.LoginRole_;
import io.tesler.model.core.entity.ProjectGroup;
import io.tesler.model.core.entity.ProjectGroupUser;
import io.tesler.model.core.entity.ProjectGroupUser_;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.UserRole;
import io.tesler.model.core.entity.UserRole_;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.hibernate.LockOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleService {

	private final JpaDao jpaDao;

	private final DictionaryCache dictionaryCache;

	/**
	 * Get the main user role (last active role)
	 *
	 * @param user User
	 * @return LOV
	 */
	public LOV getMainUserRoleKey(User user) {
		List<UserRole> userRoleList = getListByUser(user);
		return userRoleList != null ? userRoleList.stream()
				.filter(UserRole::getMain)
				.map(UserRole::getInternalRoleCd)
				.findFirst()
				.orElse(userRoleList.stream()
						.findFirst()
						.map(UserRole::getInternalRoleCd)
						.orElse(user.getInternalRole())) : user.getInternalRole();
	}

	/**
	 * Refresh the list of user roles
	 *
	 * @param userId User
	 * @param intUserRoleKeyList List of roles based on ESK groups
	 */
	public void upsertUserRoles(Long userId, List<String> intUserRoleKeyList) {
		User user = jpaDao.findById(User.class, userId);
		List<UserRole> userRoleList = updateUserRoles(user, intUserRoleKeyList);
		if (user.getDepartment() != null && user.getDepartment().getId() != null) {
			updateProjectGroupUserList(user, userRoleList);
		}
	}

	/**
	 * Get the main role based on ESK groups
	 *
	 * @param intUserRoleKeyList List of roles based on ESK groups
	 * @return String
	 */
	public String getMainUserRole(List<String> intUserRoleKeyList) {
		Map<String, Integer> roleKeyOrderMap = dictionaryCache.getAll("INTERNAL_ROLE").stream()
				.collect(Collectors.toMap(SimpleDictionary::getKey, SimpleDictionary::getDisplayOrder));
		String mainUserRole = null;
		if (intUserRoleKeyList != null) {
			mainUserRole = intUserRoleKeyList.stream()
					.filter(Objects::nonNull)
					.filter(userRole -> !userRole.startsWith("OPT_"))
					.filter(roleKeyOrderMap::containsKey)
					.min(Comparator.comparingInt(roleKeyOrderMap::get)).orElse(null);
		}
		return mainUserRole;
	}

	/**
	 * Get a list of user roles available for selection
	 *
	 * @param user User
	 * @return List
	 */
	public List<SimpleDictionary> getUserRoles(User user) {

		Map<String, String> dictRoleMap = dictionaryCache.getAll("INTERNAL_ROLE").stream()
				.collect(Collectors.toMap(SimpleDictionary::getKey, SimpleDictionary::getValue));

		List<UserRole> userRoleList = getListByUser(user);

		if (userRoleList == null || userRoleList.isEmpty()) {
			throw new ServerException(format("User [id=%s] has no roles!", user.getId()));
		}

		return userRoleList.stream()
				.filter(userRole -> userRole.getActive() && !userRole.getInternalRoleCd().getKey().contains("OPT_"))
				.map(userRole -> {
					String key = userRole.getInternalRoleCd().getKey();
					String value = dictRoleMap.get(key);
					return value != null ? new SimpleDictionary(key, value) : new SimpleDictionary(key, key);
				}).collect(Collectors.toList());
	}

	/**
	 * Update main role (main role is last role selected by user)
	 *
	 * @param user user
	 * @param mainUserRole main role
	 */
	public void updateMainUserRole(User user, LOV mainUserRole) {
		user.setInternalRole(mainUserRole);
		List<UserRole> userRoleList = getListByUser(user);
		if (userRoleList != null && mainUserRole != null) {
			userRoleList.stream()
					.filter(UserRole::getMain)
					.findFirst()
					.ifPresent(mur -> mur.setMain(false));
			userRoleList.stream()
					.filter(ur -> ur.getInternalRoleCd().getKey().equals(mainUserRole.getKey()))
					.findFirst()
					.ifPresent(ur -> ur.setMain(true));
		}
	}

	public LOV getMatchedRole(User user, String roleName) {
		List<UserRole> userRoleList = getListByUser(user);
		if (userRoleList.stream().anyMatch(ur -> ur.getActive() && ur.getInternalRoleCd().getKey().equals(roleName))) {
			return new LOV(roleName);
		}
		return null;
	}

	/**
	 * Update the list of user roles (including OPT_% options)
	 *
	 * @param user user
	 * @param intUserRoleKeyList list of role codes from ESK
	 * @return List
	 */
	private List<UserRole> updateUserRoles(User user, List<String> intUserRoleKeyList) {
		List<UserRole> userRoleList = getListByUser(user);
		List<UserRole> activeUserRoleList = new ArrayList<>();
		for (UserRole userRole : userRoleList) {
			if (intUserRoleKeyList.contains(userRole.getInternalRoleCd().getKey())) {
				userRole.setActive(true);
				activeUserRoleList.add(userRole);
			} else {
				userRole.setActive(false);
				userRole.setMain(false);
			}
		}

		List<String> userRoleKeyList = userRoleList.stream()
				.map(userRole -> userRole.getInternalRoleCd().getKey())
				.collect(Collectors.toList());

		for (String userRoleKey : intUserRoleKeyList) {
			if (!userRoleKeyList.contains(userRoleKey)) {
				activeUserRoleList.add(createUserRole(user, new LOV(userRoleKey)));
			}
		}

		if (activeUserRoleList.stream().noneMatch(UserRole::getMain)) {
			final String mainUserRole = getMainUserRole(intUserRoleKeyList);
			activeUserRoleList.stream()
					.filter(ur -> ur.getInternalRoleCd().getKey().equals(mainUserRole))
					.findFirst()
					.ifPresent(userRole -> userRole.setMain(true));
		}

		return activeUserRoleList;
	}

	private UserRole createUserRole(User user, final LOV internalRoleCd) {
		jpaDao.lockAndRefresh(user, LockOptions.WAIT_FOREVER);
		UserRole userRole = jpaDao.getSingleResultOrNull(UserRole.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(UserRole_.user), user),
				cb.equal(root.get(UserRole_.internalRoleCd), internalRoleCd)
		));
		if (userRole == null) {
			userRole = new UserRole();
			userRole.setUser(user);
			userRole.setInternalRoleCd(internalRoleCd);
			userRole.setActive(true);
			userRole.setMain(false);
			jpaDao.save(userRole);
		}
		return userRole;
	}

	/**
	 * Update user groups
	 *
	 * @param user user
	 * @param activeUserRoleList list of active user roles
	 */
	private void updateProjectGroupUserList(User user, List<UserRole> activeUserRoleList) {
		List<LoginRole> loginRoleList = listByDeptByInternalRole(user);

		List<LoginRole> activeLoginRoleList = loginRoleList.stream()
				.filter(loginRole -> activeUserRoleList.stream().anyMatch(userRole -> userRole.getActive() &&
						userRole.getInternalRoleCd().equals(loginRole.getInternalRoleCd())))
				.collect(Collectors.toList());

		List<ProjectGroup> activeProjectGroupList = activeLoginRoleList.stream()
				.map(LoginRole::getProjectGroup)
				.distinct()
				.collect(Collectors.toList());

		List<ProjectGroupUser> projectGroupUserList = jpaDao.getList(
				ProjectGroupUser.class,
				(root, cq, cb) -> cb.equal(root.get(ProjectGroupUser_.user), user)
		);

		activeProjectGroupList.forEach(projectGroup -> {
			if (projectGroupUserList.stream().noneMatch(pgu ->
					pgu.getProjectGroup().getId().equals(projectGroup.getId()))) {
				ProjectGroupUser projectGroupUser = new ProjectGroupUser();
				projectGroupUser.setProjectGroup(projectGroup);
				projectGroupUser.setUser(user);
				jpaDao.save(projectGroupUser);
			}
		});

		projectGroupUserList.forEach(pgu -> {
			if (activeLoginRoleList.stream().noneMatch(loginRole ->
					loginRole.getProjectGroup().getId().equals(pgu.getProjectGroup().getId()))) {
				jpaDao.delete(pgu);
			}
		});
	}

	private List<LoginRole> listByDeptByInternalRole(User user) {
		return jpaDao.getList(LoginRole.class, (root, query, cb) -> {
			final Subquery<LOV> subquery = query.subquery(LOV.class);
			final Root<UserRole> subqueryRoot = subquery.from(UserRole.class);
			subquery.select(subqueryRoot.get(UserRole_.internalRoleCd));
			subquery.where(cb.equal(subqueryRoot.get(UserRole_.user), user));
			return cb.and(
					cb.or(
							cb.isNull(root.get(LoginRole_.department)),
							cb.equal(root.get(LoginRole_.department), user.getDepartment())
					),
					root.get(LoginRole_.internalRoleCd).in(subquery)
			);
		});
	}

	/**
	 * Get a list of all user roles
	 *
	 * @param user user
	 * @return List
	 */
	private List<UserRole> getListByUser(User user) {
		return jpaDao.getList(UserRole.class, (root, query, cb) -> cb.equal(root.get(UserRole_.user), user));
	}

}
