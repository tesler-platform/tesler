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
 * Сервис управления членством в группах
 */
public interface GroupService {

	/**
	 * Возвращает набор идентификаторов групп,
	 * в которые пользователь входит непосредственно
	 *
	 * @param user пользователь
	 * @return набор идентификаторов
	 */
	Set<Long> getUserDirectGroups(User user);

	/**
	 * Возвращает набор идентификаторов всех групп,
	 * в которые пользователь входит непосредственно
	 * или опосредованно (через другие группы)
	 *
	 * @param user пользователь
	 * @return набор идентификаторов
	 */
	Set<Long> getUserAllGroups(User user);

	/**
	 * Добавляет пользователя в группу
	 *
	 * @param parent родительская группа
	 * @param user пользователь
	 */
	void addUserToGroup(Group parent, User user);

	/**
	 * Удаляет пользователя из группы
	 *
	 * @param parent родительская группа
	 * @param user пользователь
	 */
	void removeUserFromGroup(Group parent, User user);

	/**
	 * Добавляет группу в группу
	 *
	 * @param parent родительская группа
	 * @param group дочерняя группа
	 */
	void addGroupToGroup(Group parent, Group group);

	/**
	 * Удаляет группу из группы
	 *
	 * @param parent родительская группа
	 * @param group дочерняя группа
	 */
	void removeGroupFromGroup(Group parent, Group group);

	/**
	 * Входит ли пользователь в группу непосредственно
	 *
	 * @param parent родительская группа
	 * @param user пользователь
	 * @return true/false
	 */
	boolean isUserInGroup(Group parent, User user);

	/**
	 * Входит ли группа в группу непосредственно
	 *
	 * @param parent родительская группа
	 * @param group дочерняя группа
	 * @return true/false
	 */
	boolean isGroupInGroup(Group parent, Group group);

	/**
	 * Возвращает подзапрос, возвращающий идентификаторы всех групп пользователя
	 *
	 * @param user пользователь
	 * @param cq CriteriaBuilder
	 * @param cb CriteriaQuery
	 * @return подзапрос
	 */
	Subquery<Long> getAllGroupsSubquery(User user, CriteriaQuery<?> cq, CriteriaBuilder cb);

	/**
	 * Возвращает подзапрос, возвращающий идентификаторы непосредственных групп пользователя
	 *
	 * @param user пользователь
	 * @param cq CriteriaBuilder
	 * @param cb CriteriaQuery
	 * @return подзапрос
	 */
	Subquery<Long> getDirectGroupsSubquery(User user, CriteriaQuery<?> cq, CriteriaBuilder cb);

	/**
	 * Возвращает набор идентификаторов пользователей - непосредственных членов группы
	 *
	 * @param group группа
	 * @return набор идентификаторов
	 */
	Set<Long> getDirectGroupMembers(Group group);

	/**
	 * Возвращает набор идентификаторов пользователей - всех членов группы
	 *
	 * @param group группа
	 * @return набор идентификаторов
	 */
	Set<Long> getAllGroupMembers(Group group);

}
