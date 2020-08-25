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

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.CrudmaActionHolder;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.CrudmaGateway;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.exception.ClientException;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UniversalDataController {

	@Autowired
	private CrudmaGateway crudmaGateway;

	@Autowired
	private ResponseBuilder resp;

	@Autowired
	private BCFactory bcFactory;

	@Autowired
	private CrudmaActionHolder crudmaActionHolder;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = {"data/**"})
	public ResponseDTO find(
			HttpServletRequest request,
			QueryParameters queryParameters) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		if (bc.getId() != null) {
			CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.GET)
					.setBc(bc).getAction();
			crudmaAction.setDescription(
					String.format(
							"Получение записи %s, id: %s, parentId: %s",
							bc.getDescription().getName(),
							bc.getId(),
							bc.getParentId()
					)
			);
			DataResponseDTO data = crudmaGateway.get(crudmaAction);
			return resp.build(data == null ? Collections.emptyList() : Collections.singletonList(data));
		} else {
			CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.FIND)
					.setBc(bc).getAction();
			crudmaAction.setDescription(
					infoMessage("info.get_list_request", bc.getDescription().getName(), bc.getParentId())
			);
			ResultPage<? extends DataResponseDTO> data = crudmaGateway.getAll(crudmaAction);
			return resp.build(data.getResult(), data.isHasNext());
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = {"data/**"})
	public ResponseDTO update(HttpServletRequest request,
			QueryParameters queryParameters,
			@RequestBody Map<String, Object> requestBody) {
		if (requestBody == null || requestBody.get("data") == null || !(requestBody.get("data") instanceof Map)) {
			throw new ClientException("Request with wrong request body. Expected: {\"data\":{}}");
		} else {
			requestBody = (Map) requestBody.get("data");
		}
		final String action = queryParameters.getParameter("_action");
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.UPDATE)
				.setBc(bc).setOriginalActionType(action).setDescription(
						String.format(
								"Изменение записи %s, id: %s, parentId: %s",
								bc.getDescription().getName(),
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.update(crudmaAction, requestBody));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = {"data/**"})
	public ResponseDTO delete(HttpServletRequest request,
			QueryParameters queryParameters
	) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		final String action = queryParameters.getParameter("_action");
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.DELETE)
				.setBc(bc).setOriginalActionType(action).setDescription(
						String.format(
								"Удаление записи %s, id: %s, parentId: %s",
								bc.getDescription().getName(),
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.delete(crudmaAction));
	}

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = {"count/**"})
	public ResponseDTO count(
			HttpServletRequest request,
			QueryParameters queryParameters
	) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.COUNT)
				.setBc(bc).setDescription(
						String.format(
								"Получение количества записей %s, parentId: %s",
								bc.getDescription().getName(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.count(crudmaAction));
	}

}
