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

import static io.tesler.source.dto.WorkflowTaskMigrationDto_.currentAutomaticTransitionId;
import static io.tesler.source.dto.WorkflowTaskMigrationDto_.currentAutomaticTransitionName;
import static io.tesler.source.dto.WorkflowTaskMigrationDto_.currentStepId;
import static io.tesler.source.dto.WorkflowTaskMigrationDto_.currentStepName;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.core.util.SpringBeanUtils;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTaskMigrationDtoConstructor extends DtoConstructor<WorkflowableTask, WorkflowTaskMigrationDto> {

	public WorkflowTaskMigrationDtoConstructor() {
		super(WorkflowableTask.class, WorkflowTaskMigrationDto.class);
	}

	@Override
	public Map<DtoField<? super WorkflowTaskMigrationDto, ?>, ValueSupplier<? super WorkflowableTask, ? super WorkflowTaskMigrationDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowTaskMigrationDto, ?>, ValueSupplier<? super WorkflowableTask, ? super WorkflowTaskMigrationDto, ?>>builder()
				.put(currentStepId, (mapping, entity) -> ofNullable(entity.getWorkflowTask())
						.map(WorkflowTask::getWorkflowStep)
						.map(WorkflowStep::getId)
						.orElse(null)
				)
				.put(currentStepName, (mapping, entity) -> ofNullable(entity.getWorkflowTask())
						.map(WorkflowTask::getWorkflowStep)
						.map(WorkflowStep::getName)
						.orElse(null)
				)
				.put(currentAutomaticTransitionId, (mapping, entity) -> entity.getAutomaticTransitionName())
				.put(currentAutomaticTransitionName, (mapping, entity) -> ofNullable(entity.getAutomaticTransitionName())
						.map(uuid -> SpringBeanUtils.getBean(WorkflowDao.class).getActiveWorkflowTransitionByName(uuid))
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.build();
	}

}
