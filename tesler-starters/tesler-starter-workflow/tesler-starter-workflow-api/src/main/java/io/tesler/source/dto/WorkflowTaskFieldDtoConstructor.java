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

import static io.tesler.source.dto.WorkflowTaskFieldDto_.key;
import static io.tesler.source.dto.WorkflowTaskFieldDto_.title;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.TaskField;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTaskFieldDtoConstructor extends DtoConstructor<TaskField, WorkflowTaskFieldDto> {

	public WorkflowTaskFieldDtoConstructor() {
		super(TaskField.class, WorkflowTaskFieldDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowTaskFieldDto, ?>, ValueSupplier<? super TaskField, ? super WorkflowTaskFieldDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowTaskFieldDto, ?>, ValueSupplier<? super TaskField, ? super WorkflowTaskFieldDto, ?>>builder()
				.put(key, (mapping, entity) -> entity.getKey())
				.put(title, (mapping, entity) -> entity.getTitle())
				.build();
	}

}
