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

package io.tesler.core.controller;

import static io.tesler.api.util.i18n.InfoMessageSource.infoMessage;

import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.CrudmaActionHolder;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.CrudmaGateway;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.exception.ClientException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UniversalRowMetaController {

	private final CrudmaGateway crudmaGateway;

	private final ResponseBuilder resp;

	private final BCFactory bcFactory;

	private final CrudmaActionHolder crudmaActionHolder;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "row-meta-new/**")
	public ResponseDTO rowMetaNew(
			HttpServletRequest request,
			QueryParameters queryParameters) {
		final String action = queryParameters.getParameter("_action");
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.CREATE)
				.setBc(bc).setOriginalActionType(action).setDescription(
						infoMessage(
								"info.record_create_request",
								bc.getDescription().getName(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.create(crudmaAction));
	}

	@RequestMapping(method = RequestMethod.GET, value = "row-meta-empty/**")
	public ResponseDTO rowMetaEmpty(HttpServletRequest request, QueryParameters queryParameters) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.META)
				.setBc(bc).setDescription(
						infoMessage(
								"info.row_meta_empty_request",
								bc.getDescription().getName(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.getMetaEmpty(crudmaAction));
	}

	@RequestMapping(method = RequestMethod.GET, value = "row-meta/**")
	public ResponseDTO rowMeta(HttpServletRequest request, QueryParameters queryParameters) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.META)
				.setBc(bc).setDescription(
						infoMessage(
								"info.row_meta_request",
								bc.getDescription().getName(),
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.getMeta(crudmaAction));
	}

	@RequestMapping(method = RequestMethod.POST, value = "row-meta/**")
	public ResponseDTO onFieldUpdateMeta(
			HttpServletRequest request,
			QueryParameters queryParameters,
			@RequestBody Map<String, Object> requestBody) {
		if (requestBody == null || requestBody.get("data") == null || !(requestBody.get("data") instanceof Map)) {
			throw new ClientException("Request with wrong request body. Expected: {\"data\":{}}");
		} else {
			requestBody = (Map) requestBody.get("data");
		}
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.PREVIEW)
				.setBc(bc).setDescription(
						infoMessage(
								"info.record_preview_request",
								bc.getDescription().getName(),
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.preview(crudmaAction, requestBody));
	}

}
