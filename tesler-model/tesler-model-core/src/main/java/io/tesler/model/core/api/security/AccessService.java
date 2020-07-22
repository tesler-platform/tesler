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
 * Access rights management service
 */
public interface AccessService {

	String SERVICE_NAME = "accessService";

	/**
	 * Returns the security specification for getting a list of entities
	 * for the current user and the requested access level
	 *
	 * @param permission requested access level
	 * @return security specification
	 */
	<T extends SecurableEntity> Specification<T> getSecuritySpecification(Permission permission);

	/**
	 * Returns the security specification for getting a list of entities
	 * for the specified user and the requested access level
	 *
	 * @param user user
	 * @param permission requested access level
	 * @return security specification
	 */
	<T extends SecurableEntity> Specification<T> getSecuritySpecification(User user, Permission permission);

	/**
	 * Returns the rights of the current user
	 *
	 * @param entity entity
	 * @return rights
	 */
	Permission getPermission(SecurableEntity entity);

	/**
	 * Returns the rights of the specified user
	 *
	 * @param entity entity
	 * @param user user
	 * @return rights
	 */
	Permission getPermission(SecurableEntity entity, User user);

	/**
	 * Returns the rights of the specified user
	 *
	 * @param accessList access list
	 * @param user user
	 * @return rights
	 */
	Permission getPermission(AccessList accessList, User user);

	/**
	 * Returns the rights of the current user
	 *
	 * @param accessList access list
	 * @return rights
	 */
	Permission getPermission(AccessList accessList);

	/**
	 * Returns a record of the user / group's access rights
	 *
	 * @param accessList access list
	 * @param supplier user / group
	 * @return record of the access right
	 */
	AccessRecord getAccessRecord(AccessList accessList, IAccessorSupplier supplier);

	/**
	 * Grants rights to the specified user / group, after copying
	 * access list if not private
	 *
	 * @param entity entity
	 * @param supplier user / group
	 * @param permission rights
	 */
	void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission);

	/**
	 * Grants rights to the specified user / group, after copying
	 * access list if not private
	 *
	 * @param entity entity
	 * @param supplier user / group
	 * @param permission rights
	 * @param mandatory mandatory match attribute
	 */
	void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission, Boolean mandatory);

	/**
	 * Deletes the entry on the access right of the specified user / group, after copying
	 * access list if not private
	 *
	 * @param entity entity
	 * @param supplier user / group
	 */
	void removeAccessor(SecurableEntity entity, IAccessorSupplier supplier);

	/**
	 * Deletes the entry on the access right of the specified user / group
	 *
	 * @param entity access list
	 * @param supplier user / group
	 */
	void removeAccessor(AccessList entity, IAccessorSupplier supplier);

	/**
	 * Grants rights to the specified user / group
	 *
	 * @param entity access list
	 * @param supplier user / group
	 * @param permission rights
	 */
	void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission);

	/**
	 * Grants rights to the specified user / group
	 *
	 * @param entity access list
	 * @param supplier user / group
	 * @param permission rights
	 * @param mandatory mandatory match attribute
	 */
	void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission, Boolean mandatory);

	/**
	 * Assigns an access list to an entity
	 *
	 * @param entity entity
	 * @param accessList access list
	 */
	void assignAccessList(SecurableEntity entity, AccessList accessList);

	/**
	 * Copies the existing access list and assigns the appropriate type
	 *
	 * @param accessList оригинальный список доступа
	 * @param targetType целевой тип списка доступа
	 * @return новый список доступа
	 */
	AccessList copy(AccessList accessList, AccessListType targetType);

}
