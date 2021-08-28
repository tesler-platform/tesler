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
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStepField;
import io.tesler.model.workflow.entity.WorkflowStepField_;
import io.tesler.source.dto.WorkflowStepFieldDto;
import io.tesler.source.dto.WorkflowStepFieldDto_;
import io.tesler.source.services.data.WorkflowStepFieldService;
import io.tesler.source.services.meta.WorkflowStepFieldFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStepFieldServiceImpl extends
		VersionAwareResponseService<WorkflowStepFieldDto, WorkflowStepField> implements WorkflowStepFieldService {

	public WorkflowStepFieldServiceImpl() {
		super(
				WorkflowStepFieldDto.class,
				WorkflowStepField.class,
				WorkflowStepField_.step,
				WorkflowStepFieldFieldMetaBuilder.class
		);
	}

	@Override
	protected CreateResult<WorkflowStepFieldDto> doCreateEntity(final WorkflowStepField entity,
			final BusinessComponent bc) {
		entity.setStep(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<WorkflowStepFieldDto> doUpdateEntity(WorkflowStepField entity, WorkflowStepFieldDto dto,
			BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowStepFieldDto_.fieldId)) {
			entity.setTaskField(dto.getFieldId() == null ? null : baseDAO.findById(TaskField.class, dto.getFieldId()));
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowStepFieldDto> getActions() {
		return Actions.<WorkflowStepFieldDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
