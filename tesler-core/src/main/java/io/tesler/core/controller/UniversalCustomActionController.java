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

import io.tesler.core.controller.finish.CustomActionFinishAction;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "custom-action/**")
public class UniversalCustomActionController {

	@Autowired
	private CrudmaGateway crudmaGateway;

	@Autowired
	private ResponseBuilder responseBuilder;

	@Autowired
	private BCFactory bcFactory;

	@Autowired
	private CrudmaActionHolder crudmaActionHolder;

	@Autowired(required = false)
	private CustomActionFinishAction customActionFinishAction;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseDTO invoke(HttpServletRequest request,
			QueryParameters queryParameters,
			@RequestBody Map<String, Map<String, Object>> requestBody) {
		if (requestBody == null || requestBody.get("data") == null) {
			throw new ClientException("Request with wrong request body. Expected: {\"data\":{}}");
		}
		final BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		final String action = queryParameters.getParameter("_action");
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.INVOKE)
				.setBc(bc).setName(action).setDescription(
						String.format(
								"Выполнение действия %s.%s, id: %s, parentId: %s",
								bc.getDescription().getName(),
								action,
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return processFinishAction(responseBuilder.build(crudmaGateway.invokeAction(crudmaAction, requestBody.get("data"))));
	}

	protected ResponseDTO processFinishAction(ResponseDTO result) {
		if (customActionFinishAction != null) {
			customActionFinishAction.invoke(result);
		}
		return result;
	}

}
