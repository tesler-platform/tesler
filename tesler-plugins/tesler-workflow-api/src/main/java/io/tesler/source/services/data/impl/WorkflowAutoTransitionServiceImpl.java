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
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.service.action.Actions;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger_;
import io.tesler.model.workflow.entity.WorkflowTransition_;
import io.tesler.source.dto.WorkflowPostFunctionTriggerDto;
import io.tesler.source.services.data.WorkflowAutoTransitionService;
import io.tesler.source.services.meta.WorkflowAutoTransitionFieldMetaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class WorkflowAutoTransitionServiceImpl extends
		AbstractResponseService<WorkflowPostFunctionTriggerDto, WorkflowPostFunctionTrigger> implements
		WorkflowAutoTransitionService {

	public WorkflowAutoTransitionServiceImpl() {
		super(
				WorkflowPostFunctionTriggerDto.class,
				WorkflowPostFunctionTrigger.class,
				null,
				WorkflowAutoTransitionFieldMetaBuilder.class
		);
	}

	@Override
	protected Specification<WorkflowPostFunctionTrigger> getParentSpecification(BusinessComponent bc) {
		return (root, cq, cb) ->
				cb.or(
						cb.equal(
								root.get(WorkflowPostFunctionTrigger_.responseCode1Transition).get(WorkflowTransition_.id),
								bc.getParentIdAsLong()
						),
						cb.equal(
								root.get(WorkflowPostFunctionTrigger_.responseCode2Transition).get(WorkflowTransition_.id),
								bc.getParentIdAsLong()
						),
						cb.equal(
								root.get(WorkflowPostFunctionTrigger_.responseCode3Transition).get(WorkflowTransition_.id),
								bc.getParentIdAsLong()
						),
						cb.equal(
								root.get(WorkflowPostFunctionTrigger_.responseCode4Transition).get(WorkflowTransition_.id),
								bc.getParentIdAsLong()
						),
						cb.equal(
								root.get(WorkflowPostFunctionTrigger_.responseCode5Transition).get(WorkflowTransition_.id),
								bc.getParentIdAsLong()
						)
				);
	}

	@Override
	public Actions<WorkflowPostFunctionTriggerDto> getActions() {
		return Actions.<WorkflowPostFunctionTriggerDto>builder()
				.delete().add()
				.build();
	}

}
