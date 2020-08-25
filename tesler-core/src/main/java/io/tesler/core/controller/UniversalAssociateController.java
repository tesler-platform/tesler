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

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static io.tesler.api.util.i18n.InfoMessageSource.infoMessage;

import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.CrudmaActionHolder;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.CrudmaGateway;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.exception.ClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "associate/**")
public class UniversalAssociateController {

	@Autowired
	@Qualifier("teslerObjectMapper")
	private ObjectMapper objectMapper;

	@Autowired
	private CrudmaGateway crudmaGateway;

	@Autowired
	private ResponseBuilder resp;

	@Autowired
	private BCFactory bcFactory;

	@Autowired
	private CrudmaActionHolder crudmaActionHolder;

	@RequestMapping(method = POST)
	public ResponseDTO associate(HttpServletRequest request,
			QueryParameters queryParameters,
			@RequestBody List<Object> data) {
		BusinessComponent bc = bcFactory.getBusinessComponent(request, queryParameters);
		if (data == null) {
			throw new ClientException("request must contain body");
		}
		final String action = queryParameters.getParameter("_action");
		CrudmaAction crudmaAction = crudmaActionHolder.of(CrudmaActionType.ASSOCIATE)
				.setBc(bc).setOriginalActionType(action).setDescription(
						infoMessage(
								"info.associate_request",
								bc.getDescription().getName(),
								bc.getId(),
								bc.getParentId()
						)
				).getAction();
		return resp.build(crudmaGateway.associate(crudmaAction, convertData(data)));
	}

	private List<AssociateDTO> convertData(List<Object> data) {
		List<AssociateDTO> result = new ArrayList<>();
		data.forEach(d -> result.add(objectMapper.convertValue(d, AssociateDTO.class)));
		return result;
	}

}
