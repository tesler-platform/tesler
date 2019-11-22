/*-
 * #%L
 * IO Tesler - Workflow API
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

package io.tesler.source.dto;

import io.tesler.api.data.dto.DataResponseDTO;
import lombok.Getter;

@Getter
public class DmnTaskFieldsDto extends DataResponseDTO {

	private final String title;

	private final String key;

	private final String type;

	private final String values;

	public DmnTaskFieldsDto(String id, String title, String key, String type, String values) {
		this.id = id;
		this.title = title;
		this.key = key;
		this.type = type;
		this.values = values;
	}

	public DmnTaskFieldsDto(String id, String title, String key, String type) {
		this(id, title, key, type, null);
	}

}
