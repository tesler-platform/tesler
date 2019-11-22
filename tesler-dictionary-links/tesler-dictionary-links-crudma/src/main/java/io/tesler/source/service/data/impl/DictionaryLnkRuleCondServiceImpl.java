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

package io.tesler.source.service.data.impl;

import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond_;
import io.tesler.source.dto.DictionaryLnkRuleCondDto;
import io.tesler.source.service.data.DictionaryLnkRuleCondService;
import io.tesler.source.service.meta.DictionaryLnkRuleCondFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class DictionaryLnkRuleCondServiceImpl extends
		BaseDictionaryLnkRuleCondServiceImpl<DictionaryLnkRuleCondDto, DictionaryLnkRuleCond>
		implements DictionaryLnkRuleCondService {

	public DictionaryLnkRuleCondServiceImpl() {
		super(
				DictionaryLnkRuleCondDto.class,
				DictionaryLnkRuleCond.class,
				DictionaryLnkRuleCond_.dictionaryLnkRule,
				DictionaryLnkRuleCondFieldMetaBuilder.class
		);
	}


}
