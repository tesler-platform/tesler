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
import static io.tesler.api.data.dictionary.DictionaryType.DICTIONARY_TERM_TYPE;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.LovValueProvider;
import io.tesler.model.core.entity.Department;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryLnkRuleCondDto extends DataResponseDTO {

	@SearchParameter(provider = LovValueProvider.class)
	@Lov(DICTIONARY_TERM_TYPE)
	private String type;

	private String typeCd;

	private String fieldName;

	@SearchParameter(name = "fieldName")
	private String fieldNameText;

	private String fieldTextValue;

	private Boolean fieldBooleanValue;

	private String fieldDictValue;

	private String department;

	private String departmentId;

	private String bcName;

	private String fieldType;

	private boolean defaultRuleFlg;

	private boolean ruleInversionFlg;

	public DictionaryLnkRuleCondDto(DictionaryLnkRuleCond entity) {
		this.id = entity.getId().toString();
		this.fieldName = entity.getFieldName();
		this.fieldNameText = entity.getFieldName();
		this.bcName = entity.getBcName();
		this.departmentId = Optional.of(entity).map(DictionaryLnkRuleCond::getDepartment)
				.map(Department::getId).map(Object::toString).orElse(null);
		this.department = Optional.of(entity).map(DictionaryLnkRuleCond::getDepartment)
				.map(Department::getShortName).orElse(null);
		this.fieldTextValue = entity.getFieldTextValue();
		this.fieldBooleanValue = entity.getFieldBooleanValue();
		this.type = DICTIONARY_TERM_TYPE.lookupValue(entity.getType());
		this.typeCd = Optional.of(entity).map(DictionaryLnkRuleCond::getType).map(LOV::getKey).orElse(null);
		this.fieldType = entity.getFieldType();
		if (entity.getFieldType() != null) {
			this.fieldDictValue = dictionary().lookupValue(entity.getFieldDictValue(), entity.getFieldType());
		}
		if (entity.getDictionaryLnkRule() != null) {
			defaultRuleFlg = entity.getDictionaryLnkRule().isDefaultRuleFlg();
		}
		this.ruleInversionFlg = entity.isRuleInversionFlg();
	}

}
