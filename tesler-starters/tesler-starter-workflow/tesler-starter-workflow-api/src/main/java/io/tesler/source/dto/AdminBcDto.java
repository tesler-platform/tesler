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

package io.tesler.source.dto;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.util.filter.SearchParameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminBcDto extends DataResponseDTO {

	@SearchParameter
	private String name;

	@SearchParameter
	private String parentName;

	private String affectedWidgets;

	public AdminBcDto(BcDescription bcDescription) {
		this.name = this.id = bcDescription.getName();
		this.parentName = bcDescription.getParentName();
	}

}
