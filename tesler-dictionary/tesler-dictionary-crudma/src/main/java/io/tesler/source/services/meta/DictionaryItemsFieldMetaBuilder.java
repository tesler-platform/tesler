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

package io.tesler.source.services.meta;

import static io.tesler.source.dto.DictionaryItemDTO_.active;
import static io.tesler.source.dto.DictionaryItemDTO_.additionFlg;
import static io.tesler.source.dto.DictionaryItemDTO_.description;
import static io.tesler.source.dto.DictionaryItemDTO_.displayOrder;
import static io.tesler.source.dto.DictionaryItemDTO_.key;
import static io.tesler.source.dto.DictionaryItemDTO_.type;
import static io.tesler.source.dto.DictionaryItemDTO_.value;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.dictionary.entity.DictionaryItem;
import io.tesler.source.dto.DictionaryItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictionaryItemsFieldMetaBuilder extends InnerFieldMetaBuilder<DictionaryItemDTO> {

	private final JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<DictionaryItemDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		fields.setEnabled(
				key,
				value,
				active,
				displayOrder,
				description,
				additionFlg
		);
		fields.setDisabled(type);
		// проверка на наличие значение в поле key
		if (rowId != null) {
			DictionaryItem dictionaryItem = jpaDao.findById(DictionaryItem.class, rowId);
			if (dictionaryItem != null && dictionaryItem.getKey() != null) {
				fields.setDisabled(key);
			}
		}
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<DictionaryItemDTO> fields, InnerBcDescription bcDescription,
			Long parRowId) {
		fields.enableFilter(
				active,
				type,
				value,
				key,
				additionFlg
		);
	}

}
