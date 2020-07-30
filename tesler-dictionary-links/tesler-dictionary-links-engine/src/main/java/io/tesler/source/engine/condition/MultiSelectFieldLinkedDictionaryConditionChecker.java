/*-
 * #%L
 * IO Tesler - Dictionary Links Engine
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

package io.tesler.source.engine.condition;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.multivalue.MultivalueField;
import io.tesler.core.dto.multivalue.MultivalueFieldSingleValue;
import io.tesler.core.service.spec.ResponseServiceExtractor;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.source.engine.LinkedDictionaryConditionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("MultiSelectFieldLinkedDictionaryConditionChecker")
public class MultiSelectFieldLinkedDictionaryConditionChecker implements LinkedDictionaryConditionChecker<MultivalueField> {

	private final ResponseServiceExtractor responseServiceExtractor;


	@Override
	public LOV getType() {
		return CoreDictionaries.DictionaryTermType.MULTISELECT_FIELD;
	}

	@Override
	public boolean check(MultivalueField object, DictionaryLnkRuleCond ruleCond) {
		String conditionValue = ruleCond.getFieldMultiValue();
		return object != null &&
				object.getValues().stream()
						.map(MultivalueFieldSingleValue::getValue)
						.anyMatch(value -> value.equals(conditionValue)
						);
	}

	@Override
	public boolean accept(DictionaryLnkRuleCond ruleCond, BusinessComponent bc) {
		return ruleCond.getFieldName() != null && ruleCond.getFieldMultiValue() != null;
	}

	@Override
	@Cacheable(
			cacheNames = CacheConfig.REQUEST_CACHE,
			keyGenerator = "conditionKeyGenerator"
	)
	public MultivalueField prepare(DictionaryLnkRuleCond ruleCond, BusinessComponent bc) {
		return (MultivalueField) responseServiceExtractor.getFieldValue(bc, ruleCond.getFieldName());
	}
}
