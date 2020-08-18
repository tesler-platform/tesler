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

package io.tesler.core.crudma.state.impl;

import io.tesler.core.crudma.bc.BcHierarchy;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.state.BcState;
import io.tesler.core.crudma.state.BcStateAware;
import io.tesler.core.test.util.TestResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringJUnitConfig({BcStateAwareImpl.class})
class BcStateAwareImplTest {

	@Autowired
	BcStateAware bcStateAware;

	private static final BcDescription BC_DESCRIPTION =
			new InnerBcDescription("name", "parentName", null, true);

	private static final BcHierarchy BC_HIERARCHY =
			new BcHierarchy("screenName", "bcName", "id", null);

	private static final BcState STATE = new BcState(new TestResponseDto(), false, null);

	@BeforeEach
	void setUp() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("ClientId", "123");
		request.getSession(true);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	private static final BusinessComponent BUSINESS_COMPONENT = new BusinessComponent(
			"id",
			"parentId",
			BC_DESCRIPTION,
			BC_HIERARCHY,
			null
	);

	@Test
	void testClear() {
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		bcStateAware.clear();
		Assertions.assertNull(bcStateAware.getState(BUSINESS_COMPONENT));
	}

	@Test
	void testSetAndGet() {
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		Assertions.assertEquals(STATE, bcStateAware.getState(BUSINESS_COMPONENT));
	}

	@Test
	void testIsPersisted() {
		Assertions.assertTrue(bcStateAware.isPersisted(BUSINESS_COMPONENT));
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		Assertions.assertFalse(bcStateAware.isPersisted(BUSINESS_COMPONENT));
	}

	@Test
	void testNullChecks() {
		RequestContextHolder.setRequestAttributes(null);
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		Assertions.assertNull(bcStateAware.getState(BUSINESS_COMPONENT));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.getSession(false);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		Assertions.assertNull(bcStateAware.getState(BUSINESS_COMPONENT));
		request.getSession(true);
		bcStateAware.set(
				BUSINESS_COMPONENT,
				STATE
		);
		Assertions.assertNotNull(bcStateAware.getState(BUSINESS_COMPONENT));
	}


}
