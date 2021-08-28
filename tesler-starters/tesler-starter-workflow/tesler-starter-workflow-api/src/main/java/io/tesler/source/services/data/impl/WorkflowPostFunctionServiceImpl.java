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
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunction_;
import io.tesler.source.dto.WorkflowPostFunctionDto;
import io.tesler.source.services.data.WorkflowPostFunctionService;
import io.tesler.source.services.meta.WorkflowPostFunctionFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowPostFunctionServiceImpl extends
		BaseWorkflowPostFunctionServiceImpl<WorkflowPostFunctionDto, WorkflowPostFunction> implements
		WorkflowPostFunctionService {

	public WorkflowPostFunctionServiceImpl() {
		super(
				WorkflowPostFunctionDto.class,
				WorkflowPostFunction.class,
				WorkflowPostFunction_.conditionGroup,
				WorkflowPostFunctionFieldMetaBuilder.class
		);
	}

	@Override
	protected WorkflowPostFunction create(BusinessComponent bc) {
		return new WorkflowPostFunction();
	}

}
