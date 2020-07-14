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

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryLnkRuleDto extends DataResponseDTO {

	@SearchParameter
	private String field;

	@SearchParameter
	private String name;

	@SearchParameter
	private String type;

	@SearchParameter(provider = BooleanValueProvider.class)
	private Boolean allValues;

	@SearchParameter(provider = BooleanValueProvider.class)
	private Boolean filterableField;

	@SearchParameter(provider = BooleanValueProvider.class)
	private Boolean defaultRuleFlg;

	public DictionaryLnkRuleDto(DictionaryLnkRule entity) {
		this.id = entity.getId().toString();
		this.name = entity.getName();
		this.field = entity.getField();
		this.type = entity.getType();
		this.allValues = entity.isAllValues();
		this.filterableField = entity.isFilterableField();
		this.defaultRuleFlg = entity.isDefaultRuleFlg();
	}

}
