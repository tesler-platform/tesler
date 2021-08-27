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

package io.tesler.core.crudma.ext.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.Crudma;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionHolder.SimpleCrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.CrudmaFactory;
import io.tesler.core.crudma.InterimResult;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.test.util.TestResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

class EventPublisherCrudmaGatewayInvokeExtensionProviderTest {

	@Mock
	ApplicationEventPublisher eventPublisher;

	@Mock
	CrudmaFactory crudmaFactory;

	@Mock
	Crudma crudma;

	@Mock
	Logger log;

	@InjectMocks
	EventPublisherCrudmaGatewayInvokeExtensionProvider eventPublisherCrudmaGatewayInvokeExtensionProvider;

	private BusinessComponent bc;

	private InterimResult interimResult;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		BcDescription bcDescription = new ExtremeBcDescription("name", "parent", crudma.getClass(), true);
		bc = new BusinessComponent("id", "parentId", bcDescription);
		interimResult = new InterimResult(bc, null, null);
		when(crudmaFactory.get(any())).thenReturn(crudma);
		when(crudma.get(any())).thenReturn(new TestResponseDto());
	}

	@Test
	void testExtendInvokerForInvoke() {
		CrudmaAction crudmaAction = new SimpleCrudmaAction(CrudmaActionType.INVOKE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = eventPublisherCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> true, true);
		Assertions.assertEquals(true, result.invoke());
	}

	@Test
	void testExtendInvokerForCreate() {
		CrudmaAction crudmaAction = new SimpleCrudmaAction(CrudmaActionType.CREATE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = eventPublisherCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(interimResult, result.invoke());
	}

	@Test
	void testExtendInvokerForPreview() {
		CrudmaAction crudmaAction = new SimpleCrudmaAction(CrudmaActionType.PREVIEW);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = eventPublisherCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(interimResult, result.invoke());
	}

	@Test
	void testExtendInvokerForDelete() {
		CrudmaAction crudmaAction = new SimpleCrudmaAction(CrudmaActionType.DELETE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = eventPublisherCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(interimResult, result.invoke());
	}

	@Test
	void testExtendInvokerWithException() {
		CrudmaAction crudmaAction = new SimpleCrudmaAction(CrudmaActionType.INVOKE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = eventPublisherCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> {
					throw new BusinessException();
				}, true);
		Assertions.assertThrows(BusinessException.class, result::invoke);
	}

}
