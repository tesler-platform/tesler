/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.dto;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.vanilla.entity.VanillaTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VanillaRelatedRecordsAssociateDTO extends DataResponseDTO {

	Boolean _associate;

	String name;

	String type;

	public VanillaRelatedRecordsAssociateDTO(VanillaTask task) {
		this.id = task.getId().toString();
		this.name = task.getName();
		this.type = "Задача";
	}

}
