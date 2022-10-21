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
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.source.dto.WorkflowTaskMigrationDto;
import io.tesler.source.dto.WorkflowTaskMigrationDto_;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowTaskMigrationFieldMetaBuilder extends InnerFieldMetaBuilder<WorkflowTaskMigrationDto> {

	private final WorkflowableTaskDao<?> workflowableTaskDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<WorkflowTaskMigrationDto> fields,
			InnerBcDescription bcDescription, Long rowId, Long parRowId) {
		if (workflowableTaskDao.getTask(rowId).getAutomaticTransitionName() != null) {
			fields.setRequired(WorkflowTaskMigrationDto_.newAutomaticTransitionName);
		}
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<WorkflowTaskMigrationDto> fields, InnerBcDescription bcDescription,
			Long parRowId) {
		fields.setEnabled(WorkflowTaskMigrationDto_.newStepName, WorkflowTaskMigrationDto_.newAutomaticTransitionName);
		fields.setRequired(WorkflowTaskMigrationDto_.newStepName);
		fields.enableFilter(
				WorkflowTaskMigrationDto_.currentStepName,
				WorkflowTaskMigrationDto_.currentAutomaticTransitionName
		);
	}

}
