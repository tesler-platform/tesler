/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.data.dto.rowmeta;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;


public class ActionDTOListSerializer extends JsonSerializer<List<ActionDTO>> {

	@Override
	public void serialize(List<ActionDTO> toSerialize, JsonGenerator jgen, SerializerProvider provider)
			throws IOException {
		jgen.writeStartArray();
		for (ActionDTO dto : toSerialize) {
			if (isAvailable(dto) || isNonEmptyGroup(dto)) {
				jgen.writeObject(dto);
			}
		}
		jgen.writeEndArray();
	}

	private boolean isAvailable(ActionDTO dto) {
		return dto != null && dto.isAvailable();
	}

	private boolean isNonEmptyGroup(ActionDTO dto) {
		if (dto == null) {
			return false;
		}
		List<ActionDTO> actions = dto.getActions();
		if (actions == null) {
			return false;
		}
		return actions.stream().anyMatch(child ->
				isAvailable(child) || isNonEmptyGroup(child)
		);
	}

}
