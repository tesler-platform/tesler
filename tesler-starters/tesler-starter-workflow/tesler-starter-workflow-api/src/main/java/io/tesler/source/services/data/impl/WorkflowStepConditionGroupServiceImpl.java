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
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup_;
import io.tesler.source.dto.WorkflowStepConditionGroupDto;
import io.tesler.source.dto.WorkflowStepConditionGroupDto_;
import io.tesler.source.services.data.WorkflowStepConditionGroupService;
import io.tesler.source.services.meta.WorkflowStepConditionGroupFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStepConditionGroupServiceImpl extends
		VersionAwareResponseService<WorkflowStepConditionGroupDto, WorkflowStepConditionGroup> implements
		WorkflowStepConditionGroupService {

	public WorkflowStepConditionGroupServiceImpl() {
		super(
				WorkflowStepConditionGroupDto.class,
				WorkflowStepConditionGroup.class,
				WorkflowStepConditionGroup_.step,
				WorkflowStepConditionGroupFieldMetaBuilder.class
		);
	}

	@Override
	protected CreateResult<WorkflowStepConditionGroupDto> doCreateEntity(final WorkflowStepConditionGroup entity,
			final BusinessComponent<InnerBcDescription> bc) {
		entity.setStep(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<WorkflowStepConditionGroupDto> doUpdateEntity(WorkflowStepConditionGroup entity,
			WorkflowStepConditionGroupDto dto, BusinessComponent<InnerBcDescription> bc) {
		if (dto.isFieldChanged(WorkflowStepConditionGroupDto_.seq)) {
			entity.setSeq(dto.getSeq());
		}
		if (dto.isFieldChanged(WorkflowStepConditionGroupDto_.name)) {
			entity.setName(dto.getName());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowStepConditionGroupDto, InnerBcDescription> getActions() {
		return Actions.<WorkflowStepConditionGroupDto, InnerBcDescription>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
