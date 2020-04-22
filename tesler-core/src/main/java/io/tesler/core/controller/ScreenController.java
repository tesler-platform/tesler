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

package io.tesler.core.controller;

import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.core.service.ScreenResponsibilityService;
import io.tesler.core.util.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScreenController {

	private final ScreenResponsibilityService screenResponsibilityService;

	private final SessionService sessionService;

	/**
	 * Should be called by authenticated user for a list of available screens
	 *
	 * @return Available screens and their meta information
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/screens")
	public List<ScreenResponsibility> getScreens() {
		return screenResponsibilityService.getScreens(sessionService.getSessionUser(), sessionService.getSessionUserRole());
	}
}
