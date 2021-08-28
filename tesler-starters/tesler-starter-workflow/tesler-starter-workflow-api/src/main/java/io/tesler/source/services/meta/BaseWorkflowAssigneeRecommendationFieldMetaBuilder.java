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

import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.condAssigneeCd;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.department;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.description;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.projectGroup;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.source.dto.WorkflowAssigneeRecommendationDto;


public abstract class BaseWorkflowAssigneeRecommendationFieldMetaBuilder<D extends WorkflowAssigneeRecommendationDto> extends
		FieldMetaBuilder<D> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<D> fields, InnerBcDescription bcDescription, Long rowId,
			Long parRowId) {
		fields.setEnabled(condAssigneeCd, projectGroup, department, description);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<D> fields, InnerBcDescription bcDescription, Long parRowId) {
		fields.setDictionaryTypeWithAllValues(condAssigneeCd, WorkflowDictionaryType.WF_COND_ASSIGNEE);
	}

}
