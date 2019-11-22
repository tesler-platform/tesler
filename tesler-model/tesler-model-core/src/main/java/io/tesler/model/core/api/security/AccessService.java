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

package io.tesler.model.core.api.security;

import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.security.AccessList;
import io.tesler.model.core.entity.security.AccessRecord;
import io.tesler.model.core.entity.security.SecurableEntity;
import io.tesler.model.core.entity.security.types.AccessListType;
import io.tesler.model.core.entity.security.types.Permission;
import org.springframework.data.jpa.domain.Specification;

/**
 * Сервис управления правами доступа
 */
public interface AccessService {

	String SERVICE_NAME = "accessService";

	/**
	 * Возвращает спецификацию безопасности для получения списка сущностей
	 * для текущего пользователя и запрашиваемого уровня доступа
	 *
	 * @param permission запрашиваемый уровень доступа
	 * @return спецификация безопасности
	 */
	<T extends SecurableEntity> Specification<T> getSecuritySpecification(Permission permission);

	/**
	 * Возвращает спецификацию безопасности для получения списка сущностей
	 * для указанного пользователя и запрашиваемого уровня доступа
	 *
	 * @param user пользователь
	 * @param permission запрашиваемый уровень доступа
	 * @return спецификация безопасности
	 */
	<T extends SecurableEntity> Specification<T> getSecuritySpecification(User user, Permission permission);

	/**
	 * Возвращает права текущего пользователья
	 *
	 * @param entity сущность
	 * @return права
	 */
	Permission getPermission(SecurableEntity entity);

	/**
	 * Возвращает права указанного пользователя
	 *
	 * @param entity сущность
	 * @param user пользователь
	 * @return права
	 */
	Permission getPermission(SecurableEntity entity, User user);

	/**
	 * Возвращает права указанного пользователя
	 *
	 * @param accessList список доуступа
	 * @param user пользователь
	 * @return права
	 */
	Permission getPermission(AccessList accessList, User user);

	/**
	 * Возвращает права текущего пользователя
	 *
	 * @param accessList список доступа
	 * @return права
	 */
	Permission getPermission(AccessList accessList);

	/**
	 * Возвращает запись о праве доступа пользователя/группы
	 *
	 * @param accessList список доступа
	 * @param supplier пользователь/группа
	 * @return запись о праве доступа
	 */
	AccessRecord getAccessRecord(AccessList accessList, IAccessorSupplier supplier);

	/**
	 * Выдает права указанному пользователю/группе, предварительно скопировав
	 * список доступа если он не является приватным
	 *
	 * @param entity сущность
	 * @param supplier пользователь/группа
	 * @param permission права
	 */
	void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission);

	/**
	 * Выдает права указанному пользователю/группе, предварительно скопировав
	 * список доступа если он не является приватным
	 *
	 * @param entity сущность
	 * @param supplier пользователь/группа
	 * @param permission права
	 * @param mandatory признак обязательного совпадения
	 */
	void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission, Boolean mandatory);

	/**
	 * Удаляет запись о праве доступа указанного пользователя/группы, предварительно скопировав
	 * список доступа если он не является приватным
	 *
	 * @param entity сущность
	 * @param supplier пользователь/группа
	 */
	void removeAccessor(SecurableEntity entity, IAccessorSupplier supplier);

	/**
	 * Удаляет запись о праве доступа указанного пользователя/группы
	 *
	 * @param entity список доступа
	 * @param supplier пользователь/группа
	 */
	void removeAccessor(AccessList entity, IAccessorSupplier supplier);

	/**
	 * Выдает права указанному пользователю/группе
	 *
	 * @param entity список доуступа
	 * @param supplier пользователь/группа
	 * @param permission права
	 */
	void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission);

	/**
	 * Выдает права указанному пользователю/группе
	 *
	 * @param entity список доуступа
	 * @param supplier пользователь/группа
	 * @param permission права
	 * @param mandatory признак обязательного совпадения
	 */
	void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission, Boolean mandatory);

	/**
	 * Назначает список доступа сущности
	 *
	 * @param entity сущность
	 * @param accessList список доступа
	 */
	void assignAccessList(SecurableEntity entity, AccessList accessList);

	/**
	 * Копирует существующий список доступа присваивая нужный тип
	 *
	 * @param accessList оригинальный список доступа
	 * @param targetType целевой тип списка доступа
	 * @return новый список доступа
	 */
	AccessList copy(AccessList accessList, AccessListType targetType);

}
