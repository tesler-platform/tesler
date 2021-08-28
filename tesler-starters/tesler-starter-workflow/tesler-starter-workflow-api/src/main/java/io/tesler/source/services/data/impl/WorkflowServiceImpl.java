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
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.PluginAware;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.core.util.session.SessionService;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.entity.Department;
import io.tesler.model.core.entity.Project;
import io.tesler.model.workflow.entity.Workflow;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.Workflow_;
import io.tesler.source.dto.WorkflowDto;
import io.tesler.source.dto.WorkflowDto_;
import io.tesler.source.services.data.WorkflowService;
import io.tesler.source.services.meta.WorkflowFieldMetaBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@PluginAware
public class WorkflowServiceImpl extends VersionAwareResponseService<WorkflowDto, Workflow> implements WorkflowService {

	@Autowired
	private SessionService sessionService;

	@Autowired
	private WorkflowDao workflowDao;

	public WorkflowServiceImpl() {
		super(WorkflowDto.class, Workflow.class, Workflow_.project, WorkflowFieldMetaBuilder.class);
	}

	@Override
	protected Specification<Workflow> getParentSpecification(BusinessComponent bc) {
		if (WorkflowServiceAssociation.pfChildWorkflow.isBc(bc)) {
			return (root, cq, cb) -> cb.and();
		}
		return super.getParentSpecification(bc);
	}

	@Override
	protected ActionResultDTO<WorkflowDto> doUpdateEntity(Workflow entity, WorkflowDto dto, BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowDto_.deptId)) {
			entity.setDepartment(baseDAO.findById(Department.class, dto.getDeptId()));
		}
		if (dto.isFieldChanged(WorkflowDto_.name)) {
			entity.setName(dto.getName());
		}
		if (dto.isFieldChanged(WorkflowDto_.description)) {
			entity.setDescription(dto.getDescription());
		}
		if (dto.isFieldChanged(WorkflowDto_.taskTypeCd)) {
			entity.setTaskTypeCd(DictionaryType.TASK_TYPE.lookupName(dto.getTaskTypeCd()));
		}
		if (dto.isFieldChanged(WorkflowDto_.activeVersion)) {
			entity.setActiveVersion(
					dto.getActiveVersionId() == null ? null : baseDAO.findById(WorkflowVersion.class, dto.getActiveVersionId())
			);
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<WorkflowDto> deleteEntity(BusinessComponent bc) {
		baseDAO.delete(Workflow.class, bc.getIdAsLong());
		return new ActionResultDTO<>();
	}

	@Override
	protected CreateResult<WorkflowDto> doCreateEntity(final Workflow entity, final BusinessComponent bc) {
		final Project project = baseDAO.findById(Project.class, bc.getParentIdAsLong());
		entity.setProject(project);
		List<LOV> taskTypes = workflowDao.getTaskTypesNotInWf(project);
		if (taskTypes.isEmpty()) {
			throw new BusinessException().addPopup(errorMessage("error.no_task_type_available"));
		}
		entity.setTaskTypeCd(taskTypes.get(0));
		entity.setDepartment(sessionService.getSessionUserDepartment());
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowDto> getActions() {
		return Actions.<WorkflowDto>builder()
				.create().available(this::hasTaskTypesNotInWf).add()
				.save().available(this::isEditable).add()
				.delete().available(this::isEditable).add()
				.build();
	}

	private boolean hasTaskTypesNotInWf(final BusinessComponent bc) {
		return isEditable(bc) && !workflowDao.getTaskTypesNotInWf(
				baseDAO.findById(Project.class, bc.getParentIdAsLong())
		).isEmpty();
	}

	private boolean isEditable(final BusinessComponent bc) {
		return WorkflowServiceAssociation.migrationWf.isNotBc(bc);
	}

}
