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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.session.TeslerAuthenticationService;
import io.tesler.api.service.session.TeslerUserDetails;
import io.tesler.core.service.impl.UserRoleService;
import io.tesler.core.util.session.UserService;
import io.tesler.model.core.entity.User;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeslerAuthenticationServiceImpl implements TeslerAuthenticationService {

	private final UserService userService;

	private final UserRoleService userRoleService;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return loadUserByUsername(username, null);
	}

	@Override
	public UserDetails loadUserByUsername(final String username, final LOV userRole) throws UsernameNotFoundException {
		final User user = userService.getUserByLogin(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return createUserDetails(
				user,
				userRole == null
						? userRoleService.getMainUserRoleKey(user)
						: userRole
		);
	}

	private TeslerUserDetails createUserDetails(final User user, final LOV userRole) {
		return TeslerUserDetails.builder()
				.id(user.getId())
				.username(user.getLogin())
				.password(user.getPassword())
				.userRole(userRole)
				.timezone(user.getTimezone())
				.localeCd(user.getLocale())
				.authorities(Collections.emptySet())
				.build();
	}

}
