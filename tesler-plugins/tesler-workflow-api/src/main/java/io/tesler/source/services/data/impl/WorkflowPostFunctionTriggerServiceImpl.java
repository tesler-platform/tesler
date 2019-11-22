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
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger_;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.source.dto.WorkflowPostFunctionTriggerDto;
import io.tesler.source.dto.WorkflowPostFunctionTriggerDto_;
import io.tesler.source.services.data.WorkflowPostFunctionTriggerService;
import io.tesler.source.services.meta.WorkflowPostFunctionTriggerFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowPostFunctionTriggerServiceImpl extends
		VersionAwareResponseService<WorkflowPostFunctionTriggerDto, WorkflowPostFunctionTrigger> implements
		WorkflowPostFunctionTriggerService {

	public WorkflowPostFunctionTriggerServiceImpl() {
		super(
				WorkflowPostFunctionTriggerDto.class,
				WorkflowPostFunctionTrigger.class,
				WorkflowPostFunctionTrigger_.requestPostFunction,
				WorkflowPostFunctionTriggerFieldMetaBuilder.class
		);
	}

	@Override
	protected CreateResult<WorkflowPostFunctionTriggerDto> doCreateEntity(final WorkflowPostFunctionTrigger entity,
			final BusinessComponent bc) {
		WorkflowPostFunction parentPostFunction = baseDAO.findById(WorkflowPostFunction.class, bc.getParentIdAsLong());
		entity.setRequestPostFunction(parentPostFunction);
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<WorkflowPostFunctionTriggerDto> doUpdateEntity(WorkflowPostFunctionTrigger entity,
			WorkflowPostFunctionTriggerDto dto, BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseWaitStepId)) {
			entity.setResponseWaitStep(baseDAO.findById(WorkflowStep.class, dto.getResponseWaitStepId()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseCode1TransitionId)) {
			entity.setResponseCode1Transition(dto.getResponseCode1TransitionId() == null ? null
					: baseDAO.findById(WorkflowTransition.class, dto.getResponseCode1TransitionId()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseCode2TransitionId)) {
			entity.setResponseCode2Transition(dto.getResponseCode2TransitionId() == null ? null
					: baseDAO.findById(WorkflowTransition.class, dto.getResponseCode2TransitionId()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseCode3TransitionId)) {
			entity.setResponseCode3Transition(dto.getResponseCode3TransitionId() == null ? null
					: baseDAO.findById(WorkflowTransition.class, dto.getResponseCode3TransitionId()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseCode4TransitionId)) {
			entity.setResponseCode4Transition(dto.getResponseCode4TransitionId() == null ? null
					: baseDAO.findById(WorkflowTransition.class, dto.getResponseCode4TransitionId()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionTriggerDto_.responseCode5TransitionId)) {
			entity.setResponseCode5Transition(dto.getResponseCode5TransitionId() == null ? null
					: baseDAO.findById(WorkflowTransition.class, dto.getResponseCode5TransitionId()));
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowPostFunctionTriggerDto> getActions() {
		return Actions.<WorkflowPostFunctionTriggerDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
