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

package io.tesler.model.core.api;

import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.security.Group;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Subquery;

/**
 * Group membership management service
 */
public interface GroupService {

	/**
	 * Returns a set of all groups identifiers
	 * that include the user directly
	 *
	 * @param user user
	 * @return set of identifiers
	 */
	Set<Long> getUserDirectGroups(User user);

	/**
	 * Returns a set of all groups identifiers
	 * that include the user directly
	 * or indirectly (through other groups)
	 *
	 * @param user user
	 * @return set of identifiers
	 */
	Set<Long> getUserAllGroups(User user);

	/**
	 * Add the user to the group
	 *
	 * @param parent parent group
	 * @param user user
	 */
	void addUserToGroup(Group parent, User user);

	/**
	 * Removes the user from the group
	 *
	 * @param parent parent group
	 * @param user user
	 */
	void removeUserFromGroup(Group parent, User user);

	/**
	 * Add the group to the group
	 *
	 * @param parent parent group
	 * @param group child group
	 */
	void addGroupToGroup(Group parent, Group group);

	/**
	 * Remove the group from the group
	 *
	 * @param parent parent group
	 * @param group child group
	 */
	void removeGroupFromGroup(Group parent, Group group);

	/**
	 * Is the user a member of the group directly
	 *
	 * @param parent parent group
	 * @param user user
	 * @return true/false
	 */
	boolean isUserInGroup(Group parent, User user);

	/**
	 * Is the group a member of the group directly
	 *
	 * @param parent parent group
	 * @param group child group
	 * @return true/false
	 */
	boolean isGroupInGroup(Group parent, Group group);

	/**
	 * Returns a subquery returning the identifiers of all user groups
	 *
	 * @param user пользователь
	 * @param cq CriteriaBuilder
	 * @param cb CriteriaQuery
	 * @return подзапрос
	 */
	Subquery<Long> getAllGroupsSubquery(User user, CriteriaQuery<?> cq, CriteriaBuilder cb);

	/**
	 * Returns a subquery that returns identifiers of the user's direct groups
	 *
	 * @param user user
	 * @param cq CriteriaBuilder
	 * @param cb CriteriaQuery
	 * @return subquery
	 */
	Subquery<Long> getDirectGroupsSubquery(User user, CriteriaQuery<?> cq, CriteriaBuilder cb);

	/**
	 * Returns a set of identifiers of users who are direct members of the group
	 *
	 * @param group group
	 * @return set of identifiers
	 */
	Set<Long> getDirectGroupMembers(Group group);

	/**
	 * Returns a set of user identifiers for all members of the group
	 *
	 * @param group group
	 * @return set of identifiers
	 */
	Set<Long> getAllGroupMembers(Group group);

}
