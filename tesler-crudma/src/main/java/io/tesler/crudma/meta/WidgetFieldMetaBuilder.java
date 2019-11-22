/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.crudma.meta;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.data.view.WidgetDTO;
import io.tesler.core.dto.data.view.WidgetDTO_;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import org.springframework.stereotype.Service;


@Service
public class WidgetFieldMetaBuilder extends FieldMetaBuilder<WidgetDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<WidgetDTO> fields, InnerBcDescription bcDescription,
			Long id, Long parentId) {
		fields.setEnabled(
				WidgetDTO_.name, WidgetDTO_.bcName, WidgetDTO_.showCondition, WidgetDTO_.fields,
				WidgetDTO_.axisFields, WidgetDTO_.chart, WidgetDTO_.options, WidgetDTO_.graph, WidgetDTO_.pivotFields,
				WidgetDTO_.title, WidgetDTO_.type
		);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<WidgetDTO> fields, InnerBcDescription bcDescription, Long parentId) {
		fields.enableFilter(WidgetDTO_.name, WidgetDTO_.bcName, WidgetDTO_.title);
	}

}
