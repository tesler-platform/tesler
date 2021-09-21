/*-
 * #%L
 * IO Tesler - Workflow API
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

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.source.dto.AdminBcDto;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.source.dto.AdminBcDto_;
import org.springframework.stereotype.Service;


@Service
public class TaskChildBcFieldMetaBuilder extends FieldMetaBuilder<AdminBcDto> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<AdminBcDto> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<AdminBcDto> fields, InnerBcDescription bcDescription, Long parRowId) {
		fields.enableFilter(AdminBcDto_.name, AdminBcDto_.affectedWidgets);
	}

}
