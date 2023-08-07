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

import static io.tesler.source.dto.WorkflowDto_.activeVersion;
import static io.tesler.source.dto.WorkflowDto_.deptShortName;
import static io.tesler.source.dto.WorkflowDto_.description;
import static io.tesler.source.dto.WorkflowDto_.name;
import static io.tesler.source.dto.WorkflowDto_.taskTypeCd;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.source.dto.WorkflowDto;
import org.springframework.stereotype.Service;

@Service
public class WorkflowFieldMetaBuilder extends InnerFieldMetaBuilder<WorkflowDto> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<WorkflowDto> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<WorkflowDto> fields, InnerBcDescription bcDescription, Long parRowId) {
		if (WorkflowServiceAssociation.migrationWf.isNotBc(bcDescription)) {
			fields.setEnabled(name, description, taskTypeCd, activeVersion, deptShortName);
			fields.enableFilter(name, description, taskTypeCd, deptShortName);
			fields.setDictionaryTypeWithAllValues(taskTypeCd, DictionaryType.TASK_TYPE);
			fields.setAllFilterValuesByLovType(taskTypeCd, DictionaryType.TASK_TYPE);
		}
	}

}
