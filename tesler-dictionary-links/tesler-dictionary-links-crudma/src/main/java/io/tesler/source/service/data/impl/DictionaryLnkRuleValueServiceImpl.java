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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.service.action.Actions;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue;
import io.tesler.source.dto.DictionaryLnkRuleValueDto;
import io.tesler.source.service.data.DictionaryLnkRuleValueService;
import io.tesler.source.service.meta.DictionaryLnkRuleValueFieldMetaBuilder;
import io.tesler.source.service.specification.DictionaryLnkRuleValueLinkSpecifications;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DictionaryLnkRuleValueServiceImpl extends
		AbstractResponseService<DictionaryLnkRuleValueDto, DictionaryLnkRuleValue> implements
		DictionaryLnkRuleValueService {

	public DictionaryLnkRuleValueServiceImpl() {
		super(
				DictionaryLnkRuleValueDto.class,
				DictionaryLnkRuleValue.class,
				null,
				DictionaryLnkRuleValueFieldMetaBuilder.class
		);
		this.linkSpecificationHolder = DictionaryLnkRuleValueLinkSpecifications.class;
	}

	@Override
	public Actions<DictionaryLnkRuleValueDto, InnerBcDescription> getActions() {
		return Actions.<DictionaryLnkRuleValueDto, InnerBcDescription>builder()
				.associate().add()
				.delete().add()
				.build();
	}

	@Override
	protected AssociateResultDTO doAssociate(List<AssociateDTO> data, BusinessComponent<InnerBcDescription> bc) {
		DictionaryLnkRule parent = baseDAO.findById(DictionaryLnkRule.class, bc.getParentIdAsLong());
		for (AssociateDTO dto : data) {
			if (dto.getAssociated()) {
				DictionaryLnkRuleValue entity = new DictionaryLnkRuleValue();
				entity.setDictionaryLnkRule(parent);
				entity.setChildKey(new LOV(dto.getId()));
				baseDAO.save(entity);
			}
		}
		return new AssociateResultDTO(Collections.emptyList())
				.setAction(PostAction.refreshBc(bc));
	}

}
