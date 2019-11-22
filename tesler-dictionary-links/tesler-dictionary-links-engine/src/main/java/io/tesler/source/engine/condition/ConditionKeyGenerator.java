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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import java.lang.reflect.Method;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;


@Component("conditionKeyGenerator")
public class ConditionKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return ConditionKey.of((DictionaryLnkRuleCond) params[0]);
	}

	@Data
	@EqualsAndHashCode
	@RequiredArgsConstructor
	static class ConditionKey {

		private final LOV type;

		private final String fieldName;

		private static ConditionKey of(DictionaryLnkRuleCond ruleCondition) {
			return new ConditionKey(ruleCondition.getType(), ruleCondition.getFieldName());
		}

	}


}
