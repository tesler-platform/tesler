/*-
 * #%L
 * IO Tesler - Dictionary Crudma
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

package io.tesler.source.services.data.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.dictionary.entity.AudDictionary;
import io.tesler.model.dictionary.entity.AudDictionary_;
import io.tesler.model.dictionary.entity.DictionaryItem;
import io.tesler.source.dto.AudDictionaryDto;
import io.tesler.source.services.data.AudDictionaryService;
import io.tesler.source.services.meta.AudDictionaryFieldMetaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AudDictionaryServiceImpl extends AbstractResponseService<AudDictionaryDto, AudDictionary> implements
		AudDictionaryService {

	@Autowired
	private JpaDao jpaDao;

	public AudDictionaryServiceImpl() {
		super(AudDictionaryDto.class, AudDictionary.class, null, AudDictionaryFieldMetaBuilder.class);
	}

	@Override
	protected Specification<AudDictionary> getParentSpecification(BusinessComponent bc) {
		DictionaryItem parent = jpaDao.findById(DictionaryItem.class, bc.getParentIdAsLong());
		if (parent != null) {
			return (root, cq, cb) -> cb.equal(root.get(AudDictionary_.dictType), parent.getType());
		} else {
			return (root, cq, cb) -> cb.and();
		}
	}

}
