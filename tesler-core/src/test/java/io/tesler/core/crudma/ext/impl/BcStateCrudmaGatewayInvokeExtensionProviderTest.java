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
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.tesler.api.data.dto.DataResponseDTO_;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.api.data.dto.rowmeta.FieldsDTO;
import io.tesler.api.util.Invoker;
import io.tesler.core.controller.BCFactory;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.InterimResult;
import io.tesler.core.crudma.bc.BcHierarchy;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.state.BcState;
import io.tesler.core.crudma.state.BcStateAware;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionType;
import io.tesler.core.dto.rowmeta.ActionsDTO;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.dto.rowmeta.RowMetaDTO;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.core.test.util.TestResponseDto;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BcStateCrudmaGatewayInvokeExtensionProviderTest {

	@Mock
	BcRegistry bcRegistry;

	@Mock
	BCFactory bcFactory;

	@Mock
	ResponseFactory respFactory;

	@Mock
	BcStateAware bcStateAware;

	private BcDescription bcDescription;

	private BusinessComponent bc;

	private InterimResult interimResult;

	@InjectMocks
	BcStateCrudmaGatewayInvokeExtensionProvider bcStateCrudmaGatewayInvokeExtensionProvider;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		bcDescription = new ExtremeBcDescription("name", "parent", null, true);
		bc = new BusinessComponent("id", "parentId", bcDescription);
		interimResult = new InterimResult(bc, null, null);
		ResponseService<?, ?> responseService = mock(ResponseService.class);
		when(responseService.onCancel(any())).thenReturn(new ActionResultDTO<>());
		when(respFactory.getService(any())).thenReturn(responseService);
		when(bcRegistry.getBcDescription(anyString())).thenReturn(bcDescription);
		when(bcFactory.getBusinessComponent(any(), any())).thenReturn(bc);
		when(bcStateAware.getState(any())).thenReturn(new BcState(null, true, null));
		when(bcStateAware.isPersisted(any())).thenReturn(true);
	}

	@Test
	void testExtendInvoker() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.INVOKE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> true, false);
		Assertions.assertEquals(true, result.invoke());
	}

	@Test
	void testExtendInvokerWithCancel() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.INVOKE);
		crudmaAction.setName(ActionType.CANCEL_CREATE.getType());
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> true, true);
		Assertions.assertEquals(ActionResultDTO.class, result.invoke().getClass());
		bcDescription = new InnerBcDescription("name", "parent", null, true);
		bc = new BusinessComponent("id", "parentId", bcDescription);
		crudmaAction.setBc(bc);
		result = bcStateCrudmaGatewayInvokeExtensionProvider.extendInvoker(crudmaAction, () -> true, true);
		Assertions.assertEquals(ActionResultDTO.class, result.invoke().getClass());
	}

	@Test
	void testExtendInvokerWithCreateAction() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.CREATE);
		crudmaAction.setBc(bc);
		TestResponseDto dto = new TestResponseDto();
		dto.setId("1");
		MetaDTO metaDTO = new MetaDTO(
				new RowMetaDTO(
						new ActionsDTO(),
						new FieldsDTO()
				)
		);
		metaDTO.setPostActions(Collections.singletonList(
				PostAction.drillDown(DrillDownType.INNER, "screen/somescreen/view/someview/somebc/1")
		));
		interimResult = new InterimResult(bc, dto, metaDTO);
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(InterimResult.class, result.invoke().getClass());
		metaDTO.setPostActions(Collections.emptyList());
		result = bcStateCrudmaGatewayInvokeExtensionProvider.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(InterimResult.class, result.invoke().getClass());
	}

	@Test
	void testExtendInvokerWithPreviewAction() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.PREVIEW);
		when(bcStateAware.isPersisted(any())).thenReturn(false);
		crudmaAction.setBc(bc);
		TestResponseDto dto = new TestResponseDto();
		dto.setId("1");
		MetaDTO metaDTO = new MetaDTO(
				new RowMetaDTO(
						new ActionsDTO(),
						new FieldsDTO()
				)
		);
		metaDTO.setPostActions(Collections.emptyList());
		interimResult = new InterimResult(bc, dto, metaDTO);
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> interimResult, true);
		Assertions.assertEquals(InterimResult.class, result.invoke().getClass());
	}

	@Test
	void testExtendInvokerWithMetaAction() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.META);
		when(bcStateAware.isPersisted(any())).thenReturn(false);
		crudmaAction.setBc(bc);
		FieldsDTO fields = new FieldsDTO();
		fields.add(FieldDTO.disabledField(DataResponseDTO_.vstamp.getName()));
		MetaDTO metaDTO = new MetaDTO(
				new RowMetaDTO(
						new ActionsDTO(),
						fields
				)
		);
		metaDTO.setPostActions(Collections.emptyList());
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> metaDTO, true);
		Assertions.assertEquals(MetaDTO.class, result.invoke().getClass());
	}

	@Test
	void testExtendInvokerWithGetAction() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.GET);
		when(bcStateAware.isPersisted(any())).thenReturn(false);
		crudmaAction.setBc(bc);
		TestResponseDto dto = new TestResponseDto();
		dto.setId("1");
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> dto, true);
		Assertions.assertEquals(TestResponseDto.class, result.invoke().getClass());
	}

	@Test
	void testExtendInvokerRestoreState() {
		CrudmaAction crudmaAction = new CrudmaAction(CrudmaActionType.INVOKE);
		crudmaAction.setBc(bc);
		Invoker<Object, RuntimeException> result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> "ExpectedResult", false);
		Assertions.assertEquals("ExpectedResult", result.invoke());
		bcDescription = new InnerBcDescription("name", "parent", null, true);
		BcHierarchy bcHierarchy = new BcHierarchy(
				"screen", "id", "name", new BcHierarchy(
				"screen", "parent", "parentId", null
		)
		);
		bc = new BusinessComponent("id", "parentId", bcDescription, bcHierarchy);
		crudmaAction.setBc(bc);
		result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> "ExpectedResult", false);
		Assertions.assertEquals("ExpectedResult", result.invoke());
		bcDescription = new InnerBcDescription("name", "parent", null, true);
		bc = new BusinessComponent("id", "parentId", bcDescription, bcHierarchy);
		crudmaAction.setBc(bc);
		result = bcStateCrudmaGatewayInvokeExtensionProvider
				.extendInvoker(crudmaAction, () -> "ExpectedResult", false);
		Assertions.assertEquals("ExpectedResult", result.invoke());
	}

}
