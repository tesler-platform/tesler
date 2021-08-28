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

import static io.tesler.source.dto.WorkflowStepDto_.linkedStatusKey;
import static io.tesler.source.dto.WorkflowStepDto_.linkedStatusValue;
import static io.tesler.source.dto.WorkflowStepDto_.name;
import static io.tesler.source.dto.WorkflowStepDto_.overdueTransitionId;
import static io.tesler.source.dto.WorkflowStepDto_.overdueTransitionName;
import static io.tesler.source.dto.WorkflowStepDto_.workflowName;
import static io.tesler.source.dto.WorkflowStepDto_.workflowVersion;
import static java.util.Optional.ofNullable;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStepDtoConstructor extends DtoConstructor<WorkflowStep, WorkflowStepDto> {

	public WorkflowStepDtoConstructor() {
		super(WorkflowStep.class, WorkflowStepDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowStepDto, ?>, ValueSupplier<? super WorkflowStep, ? super WorkflowStepDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowStepDto, ?>, ValueSupplier<? super WorkflowStep, ? super WorkflowStepDto, ?>>builder()
				.put(name, (mapping, entity) -> entity.getName())
				.put(linkedStatusKey, (mapping, entity) -> ofNullable(entity.getLinkedStatusCd())
						.map(LOV::getKey)
						.orElse(null)
				)
				.put(linkedStatusValue, (mapping, entity) -> DictionaryType.TASK_STATUS.lookupValue(entity.getLinkedStatusCd()))
				.put(workflowVersion, (mapping, entity) -> entity.getWorkflowVersion().getVersion())
				.put(workflowName, (mapping, entity) -> entity.getWorkflowVersion().getWorkflow().getName())
				.put(overdueTransitionId, (mapping, entity) -> ofNullable(entity.getOverdueTransition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(overdueTransitionName, (mapping, entity) -> ofNullable(entity.getOverdueTransition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.build();
	}

}
