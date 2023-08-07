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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static java.util.Optional.ofNullable;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup_;
import io.tesler.source.dto.WorkflowTransitionGroupDto;
import io.tesler.source.dto.WorkflowTransitionGroupDto_;
import io.tesler.source.services.data.WorkflowTransitionGroupService;
import io.tesler.source.services.meta.WorkflowTransitionGroupMetaBuilder;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTransitionGroupServiceImpl extends
		VersionAwareResponseService<WorkflowTransitionGroupDto, WorkflowTransitionGroup> implements
		WorkflowTransitionGroupService {

	protected WorkflowTransitionGroupServiceImpl() {
		super(
				WorkflowTransitionGroupDto.class,
				WorkflowTransitionGroup.class,
				WorkflowTransitionGroup_.workflowStep,
				WorkflowTransitionGroupMetaBuilder.class
		);
	}

	@Override
	protected Specification<WorkflowTransitionGroup> getParentSpecification(BusinessComponent<InnerBcDescription> bc) {
		return (root, cq, cb) -> {
			final Long parentId = getParentId(bc);
			return parentId == null
					? cb.and()
					: cb.equal(root.get(WorkflowTransitionGroup_.workflowStep).get(BaseEntity_.id), parentId);
		};
	}

	private Long getParentId(BusinessComponent<InnerBcDescription> bc) {
		if (WorkflowServiceAssociation.wfTransitionGroupPopup.isBc(bc)) {
			return ofNullable(baseDAO.findById(WorkflowTransition.class, bc.getParentIdAsLong()))
					.map(WorkflowTransition::getSourceStep)
					.map(BaseEntity::getId)
					.orElse(null);
		}
		return bc.getParentIdAsLong();
	}

	@Override
	protected CreateResult<WorkflowTransitionGroupDto> doCreateEntity(final WorkflowTransitionGroup entity,
			final BusinessComponent<InnerBcDescription> bc) {
		WorkflowStep workflowStep = Optional.ofNullable(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()))
				.orElseThrow(() -> new BusinessException().addPopup(errorMessage("error.no_parent_workflow_step")));

		entity.setWorkflowStep(workflowStep);
		baseDAO.save(entity);

		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<WorkflowTransitionGroupDto> doUpdateEntity(WorkflowTransitionGroup entity,
			WorkflowTransitionGroupDto dto, BusinessComponent<InnerBcDescription> bc) {
		if (dto.isFieldChanged(WorkflowTransitionGroupDto_.maxShowButtonsInGroup)) {
			entity.setMaxShowButtonsInGroup(dto.getMaxShowButtonsInGroup());
		}
		if (dto.isFieldChanged(WorkflowTransitionGroupDto_.nameButtonYet)) {
			entity.setNameButtonYet(dto.getNameButtonYet());
		}
		if (dto.isFieldChanged(WorkflowTransitionGroupDto_.description)) {
			entity.setDescription(dto.getDescription());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowTransitionGroupDto, InnerBcDescription> getActions() {
		return Actions.<WorkflowTransitionGroupDto, InnerBcDescription>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
