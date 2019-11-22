/*-
 * #%L
 * IO Tesler - Dictionary Links Implementation
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

import static io.tesler.api.data.dictionary.DictionaryCache.dictionary;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizableResponseServiceDto extends DataResponseDTO {

	@SearchParameter
	private String serviceName;

	private String docName;

	private String docUrl;

	private String docUrlType;

	public CustomizableResponseServiceDto(CustomizableResponseService entity) {
		this.id = entity.getId().toString();
		this.serviceName = entity.getServiceName();
		DictionaryCache dictionary = dictionary();
		if (dictionary.containsKey(serviceName, DictionaryType.BUSINESS_SERVICE_NAME)) {
			this.docName = DictionaryType.BUSINESS_SERVICE_NAME.lookupValue(new LOV(this.serviceName));
		}
		if (dictionary.containsKey(serviceName, DictionaryType.BUSINESS_SERVICE_URL)) {
			this.docUrl = DictionaryType.BUSINESS_SERVICE_URL.lookupValue(new LOV(this.serviceName));
		}
		this.docUrlType = DrillDownType.EXTERNAL_NEW.getValue();
	}

}
