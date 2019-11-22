/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.dto;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.model.ui.entity.BcProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BcPropertiesDTO extends DataResponseDTO {

	String bc;

	String filter;

	Long limit;

	String sort;

	Long reportPeriod;

	@Builder
	public BcPropertiesDTO(BcProperties entity) {
		this.id = entity.getId().toString();
		this.filter = entity.getFilter();
		this.limit = entity.getLimit();
		this.sort = entity.getSort();
		this.bc = entity.getBc();
		this.reportPeriod = entity.getReportPeriod();
	}

}
