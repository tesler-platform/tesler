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

import static io.tesler.source.dto.WorkflowVersionDto_.autoClosedStepId;
import static io.tesler.source.dto.WorkflowVersionDto_.autoClosedStepName;
import static io.tesler.source.dto.WorkflowVersionDto_.description;
import static io.tesler.source.dto.WorkflowVersionDto_.draft;
import static io.tesler.source.dto.WorkflowVersionDto_.firstStepId;
import static io.tesler.source.dto.WorkflowVersionDto_.firstStepName;
import static io.tesler.source.dto.WorkflowVersionDto_.stringVersion;
import static io.tesler.source.dto.WorkflowVersionDto_.version;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowVersion;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class WorkflowVersionDtoConstructor extends DtoConstructor<WorkflowVersion, WorkflowVersionDto> {

	public WorkflowVersionDtoConstructor() {
		super(WorkflowVersion.class, WorkflowVersionDto.class);
	}

	@Override
	public Map<DtoField<? super WorkflowVersionDto, ?>, ValueSupplier<? super WorkflowVersion, ? super WorkflowVersionDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowVersionDto, ?>, ValueSupplier<? super WorkflowVersion, ? super WorkflowVersionDto, ?>>builder()
				.put(version, (mapping, entity) -> entity.getVersion())
				.put(stringVersion, (mapping, entity) -> ofNullable(entity.getVersion())
						.map(Objects::toString)
						.orElse(null)
				)
				.put(description, (mapping, entity) -> entity.getDescription())
				.put(draft, (mapping, entity) -> entity.isDraft())
				.put(firstStepId, (mapping, entity) -> ofNullable(entity.getFirstStep())
						.map(WorkflowStep::getId)
						.orElse(null)
				)
				.put(firstStepName, (mapping, entity) -> ofNullable(entity.getFirstStep())
						.map(WorkflowStep::getName)
						.orElse(null)
				)
				.put(autoClosedStepId, (mapping, entity) -> ofNullable(entity.getAutoClosedStep())
						.map(WorkflowStep::getId)
						.orElse(null)
				)
				.put(autoClosedStepName, (mapping, entity) -> ofNullable(entity.getAutoClosedStep())
						.map(WorkflowStep::getName)
						.orElse(null)
				)
				.build();
	}


}
