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

import io.tesler.WorkflowServiceAssociation;
import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dao.BaseDAO;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.engine.workflow.services.WorkflowEngine;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTask_;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowableTask;
import io.tesler.model.workflow.entity.WorkflowableTask_;
import io.tesler.source.dto.WorkflowTaskMigrationDto;
import io.tesler.source.dto.WorkflowTaskMigrationDto_;
import io.tesler.source.services.data.WorkflowTaskMigrationService;
import io.tesler.source.services.meta.WorkflowTaskMigrationFieldMetaBuilder;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTaskMigrationServiceImpl extends
		AbstractResponseService<WorkflowTaskMigrationDto, WorkflowableTask> implements WorkflowTaskMigrationService {

	@Autowired
	private WorkflowSettings<?> workflowSettings;

	@Autowired
	private WorkflowableTaskDao<?> workflowableTaskDao;

	@Autowired
	private WorkflowEngine workflowEngine;

	@Autowired
	private WorkflowDao workflowDao;

	public WorkflowTaskMigrationServiceImpl() {
		super(WorkflowTaskMigrationDto.class, WorkflowableTask.class, null, WorkflowTaskMigrationFieldMetaBuilder.class);
	}

	@Override
	protected String getFetchGraphName(BusinessComponent bc) {
		return null;
	}

	@Override
	public WorkflowableTask getOneAsEntity(final BusinessComponent bc) {
		return workflowableTaskDao.getTask(bc.getIdAsLong());
	}

	@Override
	public ResultPage<WorkflowTaskMigrationDto> getList(final BaseDAO dao, final BusinessComponent bc) {
		return getList(dao, bc, (Class<WorkflowableTask>) workflowSettings.getEntityClass(), typeOfDTO);
	}

	@Override
	public long count(final BaseDAO dao, final BusinessComponent bc) {
		return count(dao, bc, (Class<WorkflowableTask>) workflowSettings.getEntityClass(), typeOfDTO);
	}

	@Override
	protected Specification<WorkflowableTask> getParentSpecification(final BusinessComponent bc) {
		final WorkflowVersion version = baseDAO.findById(WorkflowVersion.class, bc.getParentIdAsLong());
		return (root, query, cb) -> cb.and(
				cb.equal(
						root.get(WorkflowableTask_.workflowTask)
								.get(WorkflowTask_.workflowName),
						version.getWorkflow().getName()
				),
				cb.equal(
						root.get(WorkflowableTask_.workflowTask).get(WorkflowTask_.version),
						version.getVersion()
				),
				cb.equal(
						root.get(WorkflowableTask_.templateFlg),
						WorkflowServiceAssociation.wfTemplateMigration.isBc(bc) ? Boolean.TRUE : Boolean.FALSE
				)
		);
	}

	@Override
	public ActionResultDTO<WorkflowTaskMigrationDto> updateEntity(final BusinessComponent bc,
			final DataResponseDTO data) {
		final WorkflowableTask entity = workflowableTaskDao.getTask(bc.getIdAsLong());
		final WorkflowTaskMigrationDto dto = (WorkflowTaskMigrationDto) data;
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(WorkflowTaskMigrationDto_.newStepId)) {
				workflowEngine.setCustomStep(entity, baseDAO.findById(WorkflowStep.class, dto.getNewStepId()));
			}
			if (data.isFieldChanged(WorkflowTaskMigrationDto_.newAutomaticTransitionId)) {
				final WorkflowTransition automaticTransition = workflowDao.getActiveWorkflowTransitionByName(
						dto.getNewAutomaticTransitionId()
				);
				if (!Objects.equals(automaticTransition.getSourceStep(), workflowDao.getWorkflowStep(entity.getWorkflowTask()))) {
					throw new BusinessException().addPopup(errorMessage("error.automatic_transition_mismatch"));
				}
				entity.setAutomaticTransitionName(automaticTransition.getName());
			}
		}
		final WorkflowTaskMigrationDto updatedDto = entityToDto(bc, entity);
		return new ActionResultDTO<>(updatedDto).setAction(PostAction.refreshBc(bc));
	}

	@Override
	public Actions<WorkflowTaskMigrationDto> getActions() {
		return Actions.<WorkflowTaskMigrationDto>builder()
				.save().add()
				.build();
	}

}
