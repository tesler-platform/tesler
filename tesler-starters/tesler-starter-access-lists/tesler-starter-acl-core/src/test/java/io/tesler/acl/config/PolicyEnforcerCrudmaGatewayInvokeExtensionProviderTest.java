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

package io.tesler.acl.config;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.tesler.acl.service.PolicyEnforcer;
import io.tesler.acl.model.ObligationSet;
import io.tesler.acl.service.PolicyEnforcerCrudmaGatewayInvokeExtensionProvider;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.CrudmaActionHolder.SimpleCrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PolicyEnforcerCrudmaGatewayInvokeExtensionProviderTest {

	@Mock
	PolicyEnforcer policyEnforcer;

	@InjectMocks
	PolicyEnforcerCrudmaGatewayInvokeExtensionProvider policyEnforcerCrudmaGatewayInvokeExtensionProvider;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testExtendInvoker() {
		when(policyEnforcer.transform(any(), any(), any())).thenReturn(true);
		when(policyEnforcer.check(any())).thenReturn(new ObligationSet());

		Invoker<Object, RuntimeException> result = policyEnforcerCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(new SimpleCrudmaAction(CrudmaActionType.INVOKE), () -> true, true);
		Assertions.assertEquals(true, result.invoke());
	}

}
