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

import static io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType.CONDITION;
import static io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType.POST_FUNCTION;
import static io.tesler.core.dict.WorkflowDictionaries.ConditionGroupType.VALIDATION;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.PluginAware;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup_;
import io.tesler.source.dto.WorkflowTransitionConditionGroupDto;
import io.tesler.source.dto.WorkflowTransitionConditionGroupDto_;
import io.tesler.source.services.data.WorkflowTransitionConditionGroupService;
import io.tesler.source.services.meta.WorkflowTransitionConditionGroupFieldMetaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@PluginAware
public class WorkflowTransitionConditionGroupServiceImpl extends
		VersionAwareResponseService<WorkflowTransitionConditionGroupDto, WorkflowTransitionConditionGroup> implements
		WorkflowTransitionConditionGroupService {

	@Autowired
	private WorkflowDao workflowDao;

	protected WorkflowTransitionConditionGroupServiceImpl() {
		super(
				WorkflowTransitionConditionGroupDto.class,
				WorkflowTransitionConditionGroup.class,
				WorkflowTransitionConditionGroup_.transition,
				WorkflowTransitionConditionGroupFieldMetaBuilder.class
		);
	}

	@Override
	protected Specification<WorkflowTransitionConditionGroup> getParentSpecification(BusinessComponent bc) {
		return (root, cq, cb) -> cb.and(
				cb.equal(root.get(parentSpec).get(BaseEntity_.id), bc.getParentIdAsLong()),
				cb.equal(root.get(WorkflowTransitionConditionGroup_.condGroupCd), getCondGroupCd(bc))
		);
	}

	private LOV getCondGroupCd(BusinessComponent bc) {
		if (WorkflowServiceAssociation.wfTransitionCondGroup.isBc(bc)) {
			return CONDITION;
		}
		if (WorkflowServiceAssociation.wfPostFuncGroup.isBc(bc)) {
			return POST_FUNCTION;
		}
		if (WorkflowServiceAssociation.wfTranCondGroupValid.isBc(bc)) {
			return VALIDATION;
		}
		return null;
	}

	@Override
	protected CreateResult<WorkflowTransitionConditionGroupDto> doCreateEntity(
			final WorkflowTransitionConditionGroup entity, final BusinessComponent bc) {
		entity.setCondGroupCd(getCondGroupCd(bc));
		entity.setTransition(baseDAO.findById(WorkflowTransition.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		workflowDao.createDefaultPostFunctions(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<WorkflowTransitionConditionGroupDto> doUpdateEntity(WorkflowTransitionConditionGroup entity,
			WorkflowTransitionConditionGroupDto dto, BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowTransitionConditionGroupDto_.seq)) {
			entity.setSeq(dto.getSeq());
		}
		if (dto.isFieldChanged(WorkflowTransitionConditionGroupDto_.name)) {
			entity.setName(dto.getName());
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<WorkflowTransitionConditionGroupDto> deleteEntity(BusinessComponent bc) {
		workflowDao.deleteTransitionConditionGroup(isExist(bc.getIdAsLong()));
		return new ActionResultDTO<>();
	}

	@Override
	public Actions<WorkflowTransitionConditionGroupDto> getActions() {
		return Actions.<WorkflowTransitionConditionGroupDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
