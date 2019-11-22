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

package io.tesler.core.util.session;

import io.tesler.model.core.entity.User;


public interface UserExternalService<T extends SessionUser> {

	T getSessionUser();

	/**
	 * Создать/обновить информацию о пользователе
	 *
	 * @param sessionUser Информация о пользователе
	 * @param role Роль
	 * @return User
	 */
	User upsert(T sessionUser, String role);


}
