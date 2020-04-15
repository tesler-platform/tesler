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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.ViewService;
import io.tesler.model.ui.entity.WidgetLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewController {

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper mapper;

	private final ViewService views;

	private final ResponseBuilder resp;

	@RequestMapping(method = RequestMethod.GET, value = {"/screen/{name}/**", "/screen/{name}"})
	public ResponseDTO screen(@PathVariable String name) {
		return resp.build(views.getScreen(name));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/meta/view/{name}/layout")
	public ResponseDTO saveLayout(@PathVariable String name, @RequestBody Map<String, Object> requestBody) {
		if (requestBody == null || requestBody.get("data") == null || !(requestBody.get("data") instanceof Map)) {
			throw new ClientException("Request with wrong request body. Expected: {\"data\":{}}");
		}
		Map data = (Map) requestBody.get("data");
		List widgetsData = (List) data.get("widgets");
		List<WidgetLayout> widgets = convertWidgets(widgetsData);
		views.saveLayout(name, widgets);
		return resp.build();
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/meta/view/{name}/layout")
	public ResponseDTO clearLayout(@PathVariable String name) {
		views.clearLayout(name);
		return resp.build();
	}

	private List<WidgetLayout> convertWidgets(List<?> data) {
		return data.stream()
				.map(widget -> mapper.convertValue(widget, WidgetLayout.class))
				.collect(Collectors.toList());
	}

}
