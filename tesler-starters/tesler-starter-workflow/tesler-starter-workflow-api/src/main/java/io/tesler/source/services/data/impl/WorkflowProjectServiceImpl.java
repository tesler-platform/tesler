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

import io.tesler.WorkflowServiceAssociation;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.model.core.entity.Project;
import io.tesler.source.dto.WorkflowProjectDto;
import io.tesler.source.dto.WorkflowProjectDto_;
import io.tesler.source.services.data.WorkflowProjectService;
import io.tesler.source.services.meta.WorkflowProjectFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowProjectServiceImpl extends VersionAwareResponseService<WorkflowProjectDto, Project> implements
		WorkflowProjectService {

	public WorkflowProjectServiceImpl() {
		super(WorkflowProjectDto.class, Project.class, null, WorkflowProjectFieldMetaBuilder.class);
	}

	@Override
	protected ActionResultDTO<WorkflowProjectDto> doUpdateEntity(Project entity, WorkflowProjectDto dto,
			BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowProjectDto_.name)) {
			entity.setName(dto.getName());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<WorkflowProjectDto> deleteEntity(BusinessComponent bc) {
		baseDAO.delete(Project.class, bc.getIdAsLong());
		return new ActionResultDTO<>();
	}

	@Override
	protected CreateResult<WorkflowProjectDto> doCreateEntity(final Project entity, final BusinessComponent bc) {
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<WorkflowProjectDto> getActions() {
		return Actions.<WorkflowProjectDto>builder()
				.create().available(this::isEditable).add()
				.save().available(this::isEditable).add()
				.delete().available(this::isEditable).add()
				.build();
	}

	private boolean isEditable(final BusinessComponent bc) {
		return WorkflowServiceAssociation.migrationWfProject.isNotBc(bc);
	}

}
