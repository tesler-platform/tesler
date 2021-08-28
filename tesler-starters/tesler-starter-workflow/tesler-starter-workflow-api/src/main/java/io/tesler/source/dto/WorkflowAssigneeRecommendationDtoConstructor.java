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

import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.condAssigneeCd;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.department;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.departmentId;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.description;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.projectGroup;
import static io.tesler.source.dto.WorkflowAssigneeRecommendationDto_.projectGroupId;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.core.entity.Department;
import io.tesler.model.core.entity.ProjectGroup;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowAssigneeRecommendationDtoConstructor extends
		DtoConstructor<WorkflowAssigneeRecommendation, WorkflowAssigneeRecommendationDto> {

	public WorkflowAssigneeRecommendationDtoConstructor() {
		super(WorkflowAssigneeRecommendation.class, WorkflowAssigneeRecommendationDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowAssigneeRecommendationDto, ?>, ValueSupplier<? super WorkflowAssigneeRecommendation, ? super WorkflowAssigneeRecommendationDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowAssigneeRecommendationDto, ?>, ValueSupplier<? super WorkflowAssigneeRecommendation, ? super WorkflowAssigneeRecommendationDto, ?>>builder()
				.put(condAssigneeCd, (mapping, entity) -> WorkflowDictionaryType.WF_COND_ASSIGNEE.lookupValue(
						entity.getCondAssigneeCd()
				))
				.put(projectGroupId, (mapping, entity) -> ofNullable(entity.getProjectGroup())
						.map(ProjectGroup::getId)
						.orElse(null)
				)
				.put(projectGroup, (mapping, entity) -> ofNullable(entity.getProjectGroup())
						.map(ProjectGroup::getName)
						.orElse(null)
				)
				.put(departmentId, (mapping, entity) -> ofNullable(entity.getDepartment())
						.map(Department::getId)
						.orElse(null)
				)
				.put(department, (mapping, entity) -> ofNullable(entity.getDepartment())
						.map(Department::getShortName)
						.orElse(null)
				)
				.put(description, (mapping, entity) -> entity.getDescription())
				.build();
	}

}
