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

import static io.tesler.source.dto.WorkflowDto_.activeVersion;
import static io.tesler.source.dto.WorkflowDto_.activeVersionId;
import static io.tesler.source.dto.WorkflowDto_.deptShortName;
import static io.tesler.source.dto.WorkflowDto_.description;
import static io.tesler.source.dto.WorkflowDto_.name;
import static io.tesler.source.dto.WorkflowDto_.taskTypeCd;
import static java.util.Optional.ofNullable;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.Department;
import io.tesler.model.workflow.entity.Workflow;
import io.tesler.model.workflow.entity.WorkflowVersion;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class WorkflowDtoConstructor extends DtoConstructor<Workflow, WorkflowDto> {

	public WorkflowDtoConstructor() {
		super(Workflow.class, WorkflowDto.class);
	}

	@Override
	public Map<DtoField<? super WorkflowDto, ?>, ValueSupplier<? super Workflow, ? super WorkflowDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowDto, ?>, ValueSupplier<? super Workflow, ? super WorkflowDto, ?>>builder()
				.put(name, (mapping, entity) -> entity.getName())
				.put(description, (mapping, entity) -> entity.getDescription())
				.put(taskTypeCd, (mapping, entity) -> DictionaryType.TASK_TYPE.lookupValue(entity.getTaskTypeCd()))
				.put(deptShortName, (mapping, entity) -> ofNullable(entity.getDepartment())
						.map(Department::getShortName)
						.orElse(null)
				)
				.put(activeVersionId, (mapping, entity) -> ofNullable(entity.getActiveVersion())
						.map(BaseEntity::getId)
						.orElse(null)
				)
				.put(activeVersion, (mapping, entity) -> ofNullable(entity.getActiveVersion())
						.map(WorkflowVersion::getVersion)
						.map(Objects::toString)
						.orElse(null)
				)
				.build();
	}

}
