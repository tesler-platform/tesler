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

package io.tesler.model.core.service;

import static org.mockito.Mockito.when;

import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TeslerBaseEntityListenerDelegateTest {

	@Mock
	CurrentUserAware<User> currentUserAware;

	@InjectMocks
	TeslerBaseEntityListenerDelegate teslerBaseEntityListenerDelegate;

	@Mock
	BaseEntity baseEntity;

	@Mock
	User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testBaseEntityOnLoad() {
		teslerBaseEntityListenerDelegate.baseEntityOnLoad(baseEntity);
	}

	@Test
	void testBaseEntityOnCreate() {
		when(currentUserAware.getCurrentUser()).thenReturn(user);

		teslerBaseEntityListenerDelegate.baseEntityOnCreate(baseEntity);
	}

	@Test
	void testBaseEntityOnUpdate() {
		when(currentUserAware.getCurrentUser()).thenReturn(user);

		teslerBaseEntityListenerDelegate.baseEntityOnUpdate(baseEntity);
	}

}
