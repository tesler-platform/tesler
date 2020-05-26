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

package io.tesler.core.controller.finish;

import io.tesler.core.dto.ResponseDTO;

/**
 * Base finish action interface, which defines actions under resulting ResponseDTO
 * entity when common process is finished, but before returning response to the client.
 * If You need to convert result to JSON, inject {@code teslerObjectMapper} ObjectMapper
 * to interface implementation.
 */
public interface FinishAction {

	void invoke(ResponseDTO processResult);

}
