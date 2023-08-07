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
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.source.dto.WorkflowConditionDto;
import io.tesler.source.services.data.WorkflowConditionService;
import io.tesler.source.services.meta.WorkflowConditionFieldMetaBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkflowConditionServiceImpl extends
		BaseWorkflowConditionServiceImpl<WorkflowConditionDto, WorkflowCondition> implements WorkflowConditionService {

	public WorkflowConditionServiceImpl() {
		super(WorkflowConditionDto.class, WorkflowCondition.class, null, WorkflowConditionFieldMetaBuilder.class);
	}

	@Override
	protected WorkflowCondition create(BusinessComponent<InnerBcDescription> bc) {
		return new WorkflowCondition();
	}

}
