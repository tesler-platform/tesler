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

import io.tesler.api.data.dictionary.CoreDictionaries.DictionaryTermType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.service.spec.ResponseServiceExtractor;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.source.engine.LinkedDictionaryConditionChecker;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("BooleanFieldLinkedDictionaryConditionChecker")
public class BooleanFieldLinkedDictionaryConditionChecker implements LinkedDictionaryConditionChecker<Boolean> {

	private final ResponseServiceExtractor responseServiceExtractor;

	@Override
	public LOV getType() {
		return DictionaryTermType.BOOLEAN_FIELD;
	}

	@Override
	public boolean check(Boolean object, DictionaryLnkRuleCond ruleCond) {
		return object != null && Objects.equals(object, ruleCond.getFieldBooleanValue());
	}

	@Override
	public boolean accept(DictionaryLnkRuleCond ruleCond, BusinessComponent bc) {
		return ruleCond.getFieldName() != null && ruleCond.getFieldBooleanValue() != null;
	}

	@Override
	@Cacheable(
			cacheNames = CacheConfig.REQUEST_CACHE,
			keyGenerator = "conditionKeyGenerator"
	)
	public Boolean prepare(DictionaryLnkRuleCond ruleCond, BusinessComponent bc) {
		return (Boolean) responseServiceExtractor.getFieldValue(bc, ruleCond.getFieldName());
	}

}
