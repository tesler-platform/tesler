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

package io.tesler.source.services.data.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.model.workflow.entity.TaskField;
import io.tesler.source.dto.WorkflowTaskFieldDto;
import io.tesler.source.dto.WorkflowTaskFieldDto_;
import io.tesler.source.services.data.WorkflowTaskFieldsService;
import io.tesler.source.services.meta.WorkflowTaskFieldsFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTaskFieldsServiceImpl extends
		VersionAwareResponseService<WorkflowTaskFieldDto, TaskField> implements WorkflowTaskFieldsService {

	public WorkflowTaskFieldsServiceImpl() {
		super(WorkflowTaskFieldDto.class, TaskField.class, null, WorkflowTaskFieldsFieldMetaBuilder.class);
	}

	@Override
	protected CreateResult<WorkflowTaskFieldDto> doCreateEntity(final TaskField entity, final BusinessComponent bc) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ActionResultDTO<WorkflowTaskFieldDto> doUpdateEntity(TaskField entity, WorkflowTaskFieldDto dto,
			BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowTaskFieldDto_.title)) {
			entity.setTitle(dto.getTitle());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowTaskFieldDto> getActions() {
		return Actions.<WorkflowTaskFieldDto>builder()
				.save().add()
				.build();
	}

}
