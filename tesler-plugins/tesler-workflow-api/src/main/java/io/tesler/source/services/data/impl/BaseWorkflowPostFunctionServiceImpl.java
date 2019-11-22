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

import io.tesler.api.service.PluginAware;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunction_;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup_;
import io.tesler.source.dto.WorkflowPostFunctionDto;
import io.tesler.source.dto.WorkflowPostFunctionDto_;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;

@PluginAware
public abstract class BaseWorkflowPostFunctionServiceImpl<D extends WorkflowPostFunctionDto, E extends WorkflowPostFunction> extends
		VersionAwareResponseService<D, E> {

	@Autowired
	private WorkflowDao workflowDao;

	public BaseWorkflowPostFunctionServiceImpl(
			final Class<D> typeOfDTO,
			final Class<E> typeOfEntity,
			final SingularAttribute<? super E, ? extends BaseEntity> parentSpec,
			final Class<? extends FieldMetaBuilder<D>> metaBuilder) {
		super(typeOfDTO, typeOfEntity, parentSpec, metaBuilder);
	}

	@Override
	protected final ActionResultDTO<D> doUpdateEntity(E entity, D dto, BusinessComponent bc) {
		update(entity, dto, bc);
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	protected void update(E entity, D dto, BusinessComponent bc) {
		if (dto.isFieldChanged(WorkflowPostFunctionDto_.seq)) {
			entity.setSeq(dto.getSeq());
		}
		if (dto.isFieldChanged(WorkflowPostFunctionDto_.actionCd)) {
			entity.setActionCd(WorkflowDictionaryType.WF_TRN_ACT.lookupName(dto.getActionCd()));
		}
		if (dto.isFieldChanged(WorkflowPostFunctionDto_.stepTerm)) {
			entity.setStepTerm(dto.getStepTerm());
		}
	}

	@Override
	protected final CreateResult<D> doCreateEntity(final E entity, final BusinessComponent bc) {
		entity.setConditionGroup(
				baseDAO.findById(WorkflowTransitionConditionGroup.class, bc.getParentIdAsLong())
		);
		Optional<WorkflowPostFunction> workflowPostFunction = baseDAO.getList(
				WorkflowPostFunction.class,
				Specifications.where((root, cq, cb) -> cb.equal(
						root.get(WorkflowPostFunction_.conditionGroup).get(WorkflowTransitionConditionGroup_.id),
						bc.getParentIdAsLong()
				))
		).stream().filter(o -> !Objects.isNull(o.getSeq())).max(Comparator.comparing(WorkflowPostFunction::getSeq));
		entity
				.setSeq(workflowPostFunction.map(workflowPostFunction1 -> workflowPostFunction1.getSeq() + 1).orElse(1L));
		entity.setActionCd(WorkflowDictionaryType.WF_TRN_ACT.lookupName("SetStepTerm"));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected abstract E create(BusinessComponent bc);

	@Override
	public ActionResultDTO<D> deleteEntity(final BusinessComponent bc) {
		workflowDao.deletePostFunction(isExist(bc.getIdAsLong()));
		return new ActionResultDTO<>();
	}

	@Override
	public Actions<D> getActions() {
		return Actions.<D>builder()
				.create().available(this::isCreateAvailable).add()
				.save().add()
				.delete().add()
				.build();
	}

	protected boolean isCreateAvailable(BusinessComponent bc) {
		return true;
	}

}
