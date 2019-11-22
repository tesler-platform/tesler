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

import static io.tesler.source.dto.WorkflowTransitionGroupDto_.description;
import static io.tesler.source.dto.WorkflowTransitionGroupDto_.maxShowButtonsInGroup;
import static io.tesler.source.dto.WorkflowTransitionGroupDto_.nameButtonYet;
import static io.tesler.source.dto.WorkflowTransitionGroupDto_.workflowStepId;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTransitionGroupDtoConstructor extends
		DtoConstructor<WorkflowTransitionGroup, WorkflowTransitionGroupDto> {

	public WorkflowTransitionGroupDtoConstructor() {
		super(WorkflowTransitionGroup.class, WorkflowTransitionGroupDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowTransitionGroupDto, ?>, ValueSupplier<? super WorkflowTransitionGroup, ? super WorkflowTransitionGroupDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowTransitionGroupDto, ?>, ValueSupplier<? super WorkflowTransitionGroup, ? super WorkflowTransitionGroupDto, ?>>builder()
				.put(workflowStepId, (mapping, entity) -> entity.getWorkflowStep().getId())
				.put(maxShowButtonsInGroup, (mapping, entity) -> entity.getMaxShowButtonsInGroup())
				.put(nameButtonYet, (mapping, entity) -> entity.getNameButtonYet())
				.put(description, (mapping, entity) -> entity.getDescription())
				.build();
	}

}
