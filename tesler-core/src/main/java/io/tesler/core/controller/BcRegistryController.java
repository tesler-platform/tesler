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
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.service.UIService;
import io.tesler.core.ui.BcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TESLER_API_PATH_SPEL + "/bc-registry")
public class BcRegistryController {

	@Autowired
	private BcRegistry bcRegistry;

	@Autowired
	private ResponseBuilder responseBuilder;

	@Autowired
	private BcUtils bcUtils;

	@Autowired
	private UIService uiService;

	@RequestMapping(method = GET, value = "invalidate-cache")
	public ResponseDTO invalidateCache() {
		bcRegistry.refresh();
		bcUtils.invalidateFieldCache();
		uiService.invalidateCache();
		return responseBuilder.build();
	}

}
