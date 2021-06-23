/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2020 Tesler Contributors
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
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.system.SystemSettings;
import io.tesler.core.dto.LoggedUser;
import io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties;
import io.tesler.core.service.ScreenResponsibilityService;
import io.tesler.core.service.UIService;
import io.tesler.core.service.impl.UserRoleService;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DirtiesContext
@SpringJUnitConfig({
	LoginServiceImpl.class
})
public class LoginServiceImplTest {

	@MockBean
	private SessionService sessionService;

	@MockBean
	private UserRoleService userRoleService;

	@MockBean
	private SystemSettings systemSettings;

	@MockBean
	private UIService uiService;

	@MockBean
	private ScreenResponsibilityService screenResponsibilityService;

	@MockBean
	private MetaConfigurationProperties metaConfigurationProperties;

	@InjectMocks
	@Autowired
	private LoginServiceImpl loginService;

	@Test
	public void getLoggedUser() {
		User user = new User();
		LOV userRole = new LOV("ADMIN");
		when(sessionService.getSessionId()).thenReturn("id");
		when(sessionService.getSessionUser()).thenReturn(user);
		when(sessionService.getSessionUserRole()).thenReturn(userRole);
		when(userRoleService.getUserRoles(user)).thenReturn(Arrays.asList(new SimpleDictionary("admin", "ADMIN")));
		when(screenResponsibilityService.getScreens(user, userRole)).thenReturn(new ArrayList<>());
		LoggedUser result = loginService.getLoggedUser("ADMIN");
		assertThat(result).isNotNull();
		// TODO: Remove when getScreens is removed from LoginServiceImpl
		verify(screenResponsibilityService, times(1)).getScreens(user, userRole);
	}
}
