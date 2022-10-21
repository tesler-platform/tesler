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

import static io.tesler.source.dto.WorkflowConditionDto_.condCd;
import static io.tesler.source.dto.WorkflowConditionDto_.dmn;
import static io.tesler.source.dto.WorkflowConditionDto_.seq;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.source.dto.WorkflowConditionDto;


public abstract class BaseWorkflowConditionFieldMetaBuilder<D extends WorkflowConditionDto> extends
		InnerFieldMetaBuilder<D> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<D> fields, InnerBcDescription bcDescription, Long rowId,
			Long parRowId) {
		fields.setEnabled(seq, condCd, dmn);
		fields.setDictionaryTypeWithAllValues(condCd, WorkflowDictionaryType.WF_COND);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<D> fields, InnerBcDescription bcDescription, Long parRowId) {
	}

}
