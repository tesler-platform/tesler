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

import static io.tesler.source.dto.WorkflowConditionDto_.condCd;
import static io.tesler.source.dto.WorkflowConditionDto_.dmn;
import static io.tesler.source.dto.WorkflowConditionDto_.seq;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.api.data.dictionary.CoreDictionaries.WorkflowConditionType;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dict.WorkflowDictionaries;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowCondition_;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup_;
import io.tesler.model.workflow.entity.WorkflowStepField;
import io.tesler.model.workflow.entity.WorkflowStepField_;
import io.tesler.model.workflow.entity.WorkflowTaskChildBcAvailability;
import io.tesler.model.workflow.entity.WorkflowTaskChildBcAvailability_;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup_;
import io.tesler.source.dto.WorkflowConditionDto;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;


public abstract class BaseWorkflowConditionServiceImpl<D extends WorkflowConditionDto, E extends WorkflowCondition> extends
		VersionAwareResponseService<D, E> {

	public BaseWorkflowConditionServiceImpl(
			final Class<D> typeOfDTO,
			final Class<E> typeOfEntity,
			final SingularAttribute<? super E, ? extends BaseEntity> parentSpec,
			final Class<? extends FieldMetaBuilder<D, InnerBcDescription>> metaBuilder) {
		super(typeOfDTO, typeOfEntity, parentSpec, metaBuilder);
	}

	@Override
	protected Specification<E> getParentSpecification(BusinessComponent<InnerBcDescription> bc) {
		final Long parentId = bc.getParentIdAsLong();
		if (WorkflowServiceAssociation.wfStepCondRecommendedAssignee.isBc(bc)) {
			return (root, cq, cb) -> cb.equal(
					root.get(WorkflowCondition_.stepConditionGroup).get(WorkflowStepConditionGroup_.id), parentId
			);
		} else if (WorkflowServiceAssociation.wfStepFieldCond.isBc(bc)) {
			return (root, cq, cb) -> cb.equal(
					root.get(WorkflowCondition_.stepField).get(WorkflowStepField_.id), parentId
			);
		} else if (WorkflowServiceAssociation.wfChildBcAvailabilityCond.isBc(bc)) {
			return (root, cq, cb) -> cb.equal(
					root.get(WorkflowCondition_.wfChildBcAvailability).get(WorkflowTaskChildBcAvailability_.id), parentId
			);
		} else if (WorkflowServiceAssociation.wfTranCondValid.isBc(bc) || WorkflowServiceAssociation.wfTransitionCond
				.isBc(bc) || WorkflowServiceAssociation.wfPostFuncGroupCond.isBc(bc)) {
			return (root, cq, cb) -> cb.equal(
					root.get(WorkflowCondition_.transitionConditionGroup).get(WorkflowTransitionConditionGroup_.id), parentId
			);
		} else {
			return (root, cq, cb) -> cb.and();
		}
	}

	@Override
	protected final CreateResult<D> doCreateEntity(final E entity, final BusinessComponent<InnerBcDescription> bc) {
		if (WorkflowServiceAssociation.wfStepCondRecommendedAssignee.isBc(bc)) {
			entity.setStepConditionGroup(baseDAO.findById(WorkflowStepConditionGroup.class, bc.getParentIdAsLong()));
			entity.setCondLinkType(WorkflowConditionType.STEP_CONDITION);
		} else if (WorkflowServiceAssociation.wfStepFieldCond.isBc(bc)) {
			entity.setStepField(baseDAO.findById(WorkflowStepField.class, bc.getParentIdAsLong()));
			entity.setCondLinkType(WorkflowConditionType.STEP_FIELD_CONDITION);
		} else if (WorkflowServiceAssociation.wfChildBcAvailabilityCond.isBc(bc)) {
			entity.setWfChildBcAvailability(baseDAO.findById(WorkflowTaskChildBcAvailability.class, bc.getParentIdAsLong()));
			entity.setCondLinkType(WorkflowConditionType.CHILD_BC_CONDITION);
		} else if (WorkflowServiceAssociation.wfTranCondValid.isBc(bc) || WorkflowServiceAssociation.wfTransitionCond
				.isBc(bc) || WorkflowServiceAssociation.wfPostFuncGroupCond.isBc(bc)) {
			entity.setTransitionConditionGroup(
					baseDAO.findById(WorkflowTransitionConditionGroup.class, bc.getParentIdAsLong())
			);
			entity.setCondLinkType(WorkflowConditionType.TRANSITION_CONDITION);
		}
		entity.setCondCd(WorkflowDictionaries.WfCondition.ALWAYS_HIDDEN);
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
		if (dto.isFieldChanged(seq)) {
			entity.setSeq(dto.getSeq());
		}
		if (dto.isFieldChanged(condCd)) {
			entity.setCondCd(WorkflowDictionaryType.WF_COND.lookupName(dto.getCondCd()));
		}
		if (dto.isFieldChanged(dmn)) {
			entity.setDmn(dto.getDmn());
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
