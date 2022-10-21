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
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.Department;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup;
import io.tesler.source.dto.WorkflowAssigneeRecommendationDto;
import io.tesler.source.dto.WorkflowAssigneeRecommendationDto_;
import javax.persistence.metamodel.SingularAttribute;


public abstract class BaseWorkflowAssigneeRecommendationServiceImpl<D extends WorkflowAssigneeRecommendationDto, E extends WorkflowAssigneeRecommendation> extends
		VersionAwareResponseService<D, E> {

	public BaseWorkflowAssigneeRecommendationServiceImpl(
			final Class<D> typeOfDTO,
			final Class<E> typeOfEntity,
			final SingularAttribute<? super E, ? extends BaseEntity> parentSpec,
			final Class<? extends FieldMetaBuilder<D, InnerBcDescription>> metaBuilder) {
		super(typeOfDTO, typeOfEntity, parentSpec, metaBuilder);
	}

	@Override
	protected final CreateResult<D> doCreateEntity(final E entity, final BusinessComponent<InnerBcDescription> bc) {
		entity.setConditionGroup(baseDAO.findById(WorkflowStepConditionGroup.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected abstract E create(BusinessComponent<InnerBcDescription> bc);

	@Override
	protected final ActionResultDTO<D> doUpdateEntity(E entity, D dto, BusinessComponent<InnerBcDescription> bc) {
		update(entity, dto, bc);
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	protected void update(E entity, D dto, BusinessComponent<InnerBcDescription> bc) {
		if (dto.isFieldChanged(WorkflowAssigneeRecommendationDto_.condAssigneeCd)) {
			entity.setCondAssigneeCd(WorkflowDictionaryType.WF_COND_ASSIGNEE.lookupName(dto.getCondAssigneeCd()));
		}
		if (dto.isFieldChanged(WorkflowAssigneeRecommendationDto_.departmentId)) {
			entity.setDepartment(
					dto.getDepartmentId() == null ? null : baseDAO.findById(Department.class, dto.getDepartmentId()));
		}
		if (dto.isFieldChanged(WorkflowAssigneeRecommendationDto_.description)) {
			entity.setDescription(dto.getDescription());
		}
	}

	@Override
	public Actions<D, InnerBcDescription> getActions() {
		return Actions.<D, InnerBcDescription>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
