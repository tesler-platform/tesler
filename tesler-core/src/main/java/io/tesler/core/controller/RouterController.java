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

import io.tesler.core.controller.http.AJAXRedirectStrategy;
import io.tesler.core.service.RouterService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class RouterController {

	private final RouterService routerService;

	private final AJAXRedirectStrategy redirectStrategy;

	@RequestMapping(method = RequestMethod.GET, value = "/router/{type}/{id}")
	protected void route(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String type,
			@PathVariable Long id,
			@RequestParam(name = "_bc", required = false) String bcName) throws IOException {
		redirectStrategy.sendRedirect(
				request,
				response,
				routerService.getLocation(type, id, bcName)
		);
	}

}
