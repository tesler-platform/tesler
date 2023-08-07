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
import io.tesler.model.workflow.entity.WorkflowTaskChildBcAvailability;
import io.tesler.model.workflow.entity.WorkflowTaskChildBcAvailability_;
import io.tesler.source.dto.WorkflowTaskChildBcAvailabilityDto;
import io.tesler.source.dto.WorkflowTaskChildBcAvailabilityDto_;
import io.tesler.source.services.data.WorkflowTaskChildBcAvailabilityService;
import io.tesler.source.services.meta.WorkflowTaskChildBcAvailabilityFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTaskChildBcAvailabilityServiceImpl extends
		VersionAwareResponseService<WorkflowTaskChildBcAvailabilityDto, WorkflowTaskChildBcAvailability> implements
		WorkflowTaskChildBcAvailabilityService {

	public WorkflowTaskChildBcAvailabilityServiceImpl() {
		super(
				WorkflowTaskChildBcAvailabilityDto.class,
				WorkflowTaskChildBcAvailability.class,
				WorkflowTaskChildBcAvailability_.workflowStep,
				WorkflowTaskChildBcAvailabilityFieldMetaBuilder.class
		);
	}

	@Override
	protected CreateResult<WorkflowTaskChildBcAvailabilityDto> doCreateEntity(
			final WorkflowTaskChildBcAvailability entity, final BusinessComponent<InnerBcDescription> bc) {
		entity.setWorkflowStep(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<WorkflowTaskChildBcAvailabilityDto> doUpdateEntity(WorkflowTaskChildBcAvailability entity,
			WorkflowTaskChildBcAvailabilityDto dto, BusinessComponent<InnerBcDescription> bc) {
		if (dto.isFieldChanged(WorkflowTaskChildBcAvailabilityDto_.bcName)) {
			entity.setBcName(dto.getBcName());
		}
		if (dto.isFieldChanged(WorkflowTaskChildBcAvailabilityDto_.affectedWidgets)) {
			entity.setAffectedWidgets(dto.getAffectedWidgets());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowTaskChildBcAvailabilityDto, InnerBcDescription> getActions() {
		return Actions.<WorkflowTaskChildBcAvailabilityDto, InnerBcDescription>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
