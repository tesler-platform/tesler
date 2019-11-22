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

package io.tesler.source.service.specification;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.service.spec.LinkSpecificationHolder;
import io.tesler.core.service.spec.SpecificationHeader;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue_;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule_;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
public class DictionaryLnkRuleValueLinkSpecifications extends LinkSpecificationHolder<DictionaryLnkRuleValue> {

	public DictionaryLnkRuleValueLinkSpecifications() {
		specificationHeader = SpecificationName.class;
		map = ImmutableMap.<SpecificationHeader<DictionaryLnkRuleValue>, ParentSpecification<DictionaryLnkRuleValue>>builder()
				.put(SpecificationName.LINK_SS_1, (bcDescription, parentId) -> (root, cq, cb) ->
						cb.equal(
								root.get(DictionaryLnkRuleValue_.dictionaryLnkRule).get(DictionaryLnkRule_.id),
								NumberUtils.createLong(parentId)
						)
				).build();
	}

	public enum SpecificationName implements SpecificationHeader<DictionaryLnkRuleValue> {

		LINK_SS_1;

		@Override
		public LOV toLOV() {
			return new LOV(this.name());
		}

	}

}
