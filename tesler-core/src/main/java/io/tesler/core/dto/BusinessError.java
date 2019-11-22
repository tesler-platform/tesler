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

package io.tesler.core.dto;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.PostAction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class BusinessError {

	private final List<String> popup;

	private final Entity entity;

	@JsonProperty("preInvokeEvent")
	private final List<PreInvokeEvent> preInvokeEvents;

	private final List<PostAction> postActions;

	@Getter
	@ToString
	public static class Entity {

		private final String bcName;

		private final String id;

		private final Map<String, String> fields = new HashMap<>();

		public Entity(BusinessComponent businessComponent) {
			this.bcName = businessComponent.getName();
			this.id = businessComponent.getId();
		}

		public Entity(String bc, String id) {
			this.bcName = bc;
			this.id = id;
		}

		public Entity addField(String fieldName, String message) {
			fields.put(fieldName, message);
			return this;
		}

	}

}
