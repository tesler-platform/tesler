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

import static io.tesler.WorkflowServiceAssociation.migrationWfVersion;
import static io.tesler.source.dto.WorkflowVersionDto_.autoClosedStepName;
import static io.tesler.source.dto.WorkflowVersionDto_.description;
import static io.tesler.source.dto.WorkflowVersionDto_.firstStepName;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.source.dto.WorkflowVersionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowVersionFieldMetaBuilder extends InnerFieldMetaBuilder<WorkflowVersionDto> {

	private final JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<WorkflowVersionDto> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		if (migrationWfVersion.isNotBc(bcDescription) && jpaDao.findById(WorkflowVersion.class, rowId).isDraft()) {
			fields.setEnabled(description, firstStepName, autoClosedStepName);
		}
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<WorkflowVersionDto> fields, InnerBcDescription bcDescription,
			Long parRowId) {
	}

}
