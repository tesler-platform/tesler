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

package io.tesler.source.dto;

import static io.tesler.source.dto.WorkflowTransitionDto_.backgroundExecution;
import static io.tesler.source.dto.WorkflowTransitionDto_.checkRequiredFields;
import static io.tesler.source.dto.WorkflowTransitionDto_.name;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowDestStepId;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowDestStepName;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowGroupDescription;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowGroupNameButtonYet;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowName;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowTransitionGroupId;
import static io.tesler.source.dto.WorkflowTransitionDto_.workflowVersion;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTransitionDtoConstructor extends DtoConstructor<WorkflowTransition, WorkflowTransitionDto> {

	public WorkflowTransitionDtoConstructor() {
		super(WorkflowTransition.class, WorkflowTransitionDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowTransitionDto, ?>, ValueSupplier<? super WorkflowTransition, ? super WorkflowTransitionDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowTransitionDto, ?>, ValueSupplier<? super WorkflowTransition, ? super WorkflowTransitionDto, ?>>builder()
				.put(name, (mapping, entity) -> entity.getName())
				.put(workflowVersion, (mapping, entity) -> entity.getSourceStep().getWorkflowVersion().getVersion())
				.put(workflowName, (mapping, entity) -> entity.getSourceStep().getWorkflowVersion().getWorkflow().getName())
				.put(workflowDestStepId, (mapping, entity) -> ofNullable(entity.getDestinationStep())
						.map(WorkflowStep::getId)
						.orElse(null)
				)
				.put(workflowDestStepName, (mapping, entity) -> ofNullable(entity.getDestinationStep())
						.map(WorkflowStep::getName)
						.orElse(null)
				)
				.put(workflowTransitionGroupId, (mapping, entity) -> ofNullable(entity.getWorkflowTransitionGroup())
						.map(BaseEntity::getId)
						.orElse(null)
				)
				.put(workflowGroupDescription, (mapping, entity) -> ofNullable(entity.getWorkflowTransitionGroup())
						.map(WorkflowTransitionGroup::getDescription)
						.orElse(null)
				)
				.put(workflowGroupNameButtonYet, (mapping, entity) -> ofNullable(entity.getWorkflowTransitionGroup())
						.map(WorkflowTransitionGroup::getNameButtonYet)
						.orElse(null)
				)
				.put(checkRequiredFields, (mapping, entity) -> entity.getCheckRequiredFields())
				.put(backgroundExecution, (mapping, entity) -> entity.getBackgroundExecution())
				.build();
	}

}
