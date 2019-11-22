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

import static java.util.Optional.ofNullable;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.api.service.PluginAware;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dict.WorkflowDictionaries;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowPostFunction_;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStep_;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup_;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import io.tesler.model.workflow.entity.WorkflowTransition_;
import io.tesler.model.workflow.entity.WorkflowVersion_;
import io.tesler.model.workflow.entity.WorkflowableTask;
import io.tesler.source.dto.WorkflowTransitionDto;
import io.tesler.source.dto.WorkflowTransitionDto_;
import io.tesler.source.services.data.WorkflowTransitionService;
import io.tesler.source.services.meta.WorkflowTransitionFieldMetaBuilder;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@PluginAware
public class WorkflowTransitionServiceImpl extends
		VersionAwareResponseService<WorkflowTransitionDto, WorkflowTransition> implements WorkflowTransitionService {

	@Autowired
	private WorkflowableTaskDao<?> workflowableTaskDao;

	@Autowired
	private WorkflowDao workflowDao;

	public WorkflowTransitionServiceImpl() {
		super(
				WorkflowTransitionDto.class,
				WorkflowTransition.class,
				WorkflowTransition_.sourceStep,
				WorkflowTransitionFieldMetaBuilder.class
		);
	}

	@Override
	protected Specification<WorkflowTransition> getParentSpecification(BusinessComponent bc) {
		if (WorkflowServiceAssociation.wfTemplateMigrationNewAutomaticTransition.isBc(bc)) {
			final WorkflowableTask task = workflowableTaskDao.getTask(bc.getParentIdAsLong());
			return (root, query, cb) -> cb.and(
					cb.equal(
							root.get(WorkflowTransition_.sourceStep)
									.get(WorkflowStep_.workflowVersion)
									.get(WorkflowVersion_.workflow),
							task.getWorkflowTask().getWorkflowStep().getWorkflowVersion().getWorkflow()
					),
					cb.equal(
							root.get(WorkflowTransition_.sourceStep)
									.get(WorkflowStep_.workflowVersion)
									.get(WorkflowVersion_.draft),
							Boolean.FALSE
					),
					cb.not(
							cb.equal(
									root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion),
									task.getWorkflowTask().getWorkflowStep().getWorkflowVersion()
							)
					)
			);
		} else if (WorkflowServiceAssociation.wfTemplateMigrationCurrentAutomaticTransition.isBc(bc)) {
			final WorkflowableTask task = workflowableTaskDao.getTask(bc.getParentIdAsLong());
			return (root, query, cb) -> cb.equal(
					root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion),
					task.getWorkflowTask().getWorkflowStep().getWorkflowVersion()
			);
		}
		return (root, cq, cb) -> {
			final Long parentId = getSourceStepId(bc);
			return parentId == null
					? cb.and()
					: cb.equal(root.get(WorkflowTransition_.sourceStep).get(BaseEntity_.id), parentId);
		};
	}

	private Long getSourceStepId(BusinessComponent bc) {
		if (WorkflowServiceAssociation.wfPostFuncTriggerTransition.isBc(bc)) {
			return ofNullable(baseDAO.findById(WorkflowPostFunctionTrigger.class, bc.getParentIdAsLong()))
					.map(WorkflowPostFunctionTrigger::getResponseWaitStep)
					.map(BaseEntity::getId)
					.orElse(null);
		}
		return bc.getParentIdAsLong();
	}

	@Override
	protected ActionResultDTO<WorkflowTransitionDto> doUpdateEntity(WorkflowTransition entity, WorkflowTransitionDto dto,
			BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowTransitionDto_.name)) {
			entity.setName(dto.getName());
		}
		if (dto.isFieldChanged(WorkflowTransitionDto_.workflowDestStepId)) {
			entity.setDestinationStep(dto.getWorkflowDestStepId() == null ? null
					: baseDAO.findById(WorkflowStep.class, dto.getWorkflowDestStepId()));
		}
		if (dto.isFieldChanged(WorkflowTransitionDto_.workflowTransitionGroupId)) {
			entity.setWorkflowTransitionGroup(dto.getWorkflowTransitionGroupId() == null ? null
					: baseDAO.findById(WorkflowTransitionGroup.class, dto.getWorkflowTransitionGroupId()));
		}
		if (dto.isFieldChanged(WorkflowTransitionDto_.workflowDestStepId)) {
			entity.setUuid(UUID.randomUUID().toString());
		}
		if (dto.isFieldChanged(WorkflowTransitionDto_.checkRequiredFields)) {
			entity.setCheckRequiredFields(dto.getCheckRequiredFields());
		}
		if (dto.isFieldChanged(WorkflowTransitionDto_.backgroundExecution)) {
			entity.setBackgroundExecution(dto.getBackgroundExecution());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<WorkflowTransitionDto> deleteEntity(BusinessComponent bc) {
		WorkflowTransitionConditionGroup workflowGroup = baseDAO.getSingleResultOrNull(
				WorkflowTransitionConditionGroup.class,
				(root, cq, cb) -> cb.equal(root.get(WorkflowTransitionConditionGroup_.transition)
						.get(WorkflowTransition_.id), bc.getIdAsLong())
		);
		if (workflowGroup != null) {
			WorkflowPostFunction workflowPostFunction = baseDAO.getSingleResultOrNull(
					WorkflowPostFunction.class,
					(root, cq, cb) -> cb.equal(
							root.get(WorkflowPostFunction_.conditionGroup),
							workflowGroup
					)
			);
			if (workflowPostFunction != null) {
				baseDAO.delete(WorkflowPostFunction.class, workflowPostFunction.getId());
			}
			baseDAO.delete(WorkflowTransitionConditionGroup.class, workflowGroup.getId());
		}
		baseDAO.delete(WorkflowTransition.class, bc.getIdAsLong());
		return new ActionResultDTO<>();
	}

	@Override
	protected CreateResult<WorkflowTransitionDto> doCreateEntity(final WorkflowTransition entity,
			final BusinessComponent bc) {
		entity.setUuid(UUID.randomUUID().toString());
		entity.setSourceStep(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()));
		entity.setDestinationStep(baseDAO.findById(WorkflowStep.class, bc.getParentIdAsLong()));
		entity.setCheckRequiredFields(Boolean.TRUE);
		baseDAO.save(entity);
		WorkflowTransitionConditionGroup defaultPostFunctionGroup = createDefaultConditionGroup(entity);
		workflowDao.createDefaultPostFunctions(defaultPostFunctionGroup);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	private WorkflowTransitionConditionGroup createDefaultConditionGroup(WorkflowTransition transition) {
		WorkflowTransitionConditionGroup postFunctionGroup = new WorkflowTransitionConditionGroup();
		postFunctionGroup.setTransition(transition);
		postFunctionGroup.setSeq(1L);
		postFunctionGroup.setName("Группа по умолчанию");
		postFunctionGroup.setCondGroupCd(WorkflowDictionaries.ConditionGroupType.POST_FUNCTION);
		baseDAO.save(postFunctionGroup);
		return postFunctionGroup;
	}

	@Override
	public Actions<WorkflowTransitionDto> getActions() {
		return Actions.<WorkflowTransitionDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
