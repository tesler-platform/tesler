/*-
 * #%L
 * IO Tesler - Source
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
import io.tesler.core.dto.data.view.ScreenDTO;
import io.tesler.core.dto.data.view.ScreenDTO_;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import org.springframework.stereotype.Service;


@Service
public class ScreenFieldMetaBuilder extends FieldMetaBuilder<ScreenDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<ScreenDTO> fields, InnerBcDescription bcDescription, Long id,
			Long parentId) {
		fields.setEnabled(ScreenDTO_.name, ScreenDTO_.title, ScreenDTO_.primary, ScreenDTO_.primaries);
		fields.setRequired(ScreenDTO_.name);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<ScreenDTO> fields, InnerBcDescription bcDescription, Long parentId) {
		fields.enableFilter(ScreenDTO_.name, ScreenDTO_.title);
	}

}
