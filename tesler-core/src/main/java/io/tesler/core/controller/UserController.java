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

import static io.tesler.core.config.properties.APIProperties.TESLER_API_PATH_SPEL;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.util.ResponseBuilder;
import io.tesler.core.util.session.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TESLER_API_PATH_SPEL)
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET, value = "/users")
	public ResponseDTO searchUser(
			@RequestParam(value = "search") String search,
			PageSpecification page) {
		return ResponseBuilder.build(userService.getByMention(search, page));
	}

	@Getter
	@Setter
	public static class UserDto extends DataResponseDTO {

		private String fio;

		private String phone;

		private String email;

		private String firstName;

		private String lastName;

		private String patronymic;

		private String login;

	}

}
