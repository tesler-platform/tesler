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

import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.requestPostFunctionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.requestPostFunctionType;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode1TransitionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode1TransitionName;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode2TransitionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode2TransitionName;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode3TransitionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode3TransitionName;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode4TransitionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode4TransitionName;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode5TransitionId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseCode5TransitionName;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseWaitStepId;
import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.responseWaitStepName;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowPostFunctionTriggerDtoConstructor extends
		DtoConstructor<WorkflowPostFunctionTrigger, WorkflowPostFunctionTriggerDto> {

	public WorkflowPostFunctionTriggerDtoConstructor() {
		super(WorkflowPostFunctionTrigger.class, WorkflowPostFunctionTriggerDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowPostFunctionTriggerDto, ?>, ValueSupplier<? super WorkflowPostFunctionTrigger, ? super WorkflowPostFunctionTriggerDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowPostFunctionTriggerDto, ?>, ValueSupplier<? super WorkflowPostFunctionTrigger, ? super WorkflowPostFunctionTriggerDto, ?>>builder()
				.put(requestPostFunctionId, (mapping, entity) -> entity.getRequestPostFunction().getId())
				.put(requestPostFunctionType, (mapping, entity) -> WorkflowDictionaryType.WF_TRN_ACT.lookupValue(
						entity.getRequestPostFunction().getActionCd()
				))
				.put(responseWaitStepId, (mapping, entity) -> ofNullable(entity.getResponseWaitStep())
						.map(WorkflowStep::getId)
						.orElse(null)
				)
				.put(responseWaitStepName, (mapping, entity) -> ofNullable(entity.getResponseWaitStep())
						.map(WorkflowStep::getName)
						.orElse(null)
				)
				.put(responseCode1TransitionId, (mapping, entity) -> ofNullable(entity.getResponseCode1Transition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(responseCode1TransitionName, (mapping, entity) -> ofNullable(entity.getResponseCode1Transition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.put(responseCode2TransitionId, (mapping, entity) -> ofNullable(entity.getResponseCode2Transition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(responseCode2TransitionName, (mapping, entity) -> ofNullable(entity.getResponseCode2Transition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.put(responseCode3TransitionId, (mapping, entity) -> ofNullable(entity.getResponseCode3Transition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(responseCode3TransitionName, (mapping, entity) -> ofNullable(entity.getResponseCode3Transition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.put(responseCode4TransitionId, (mapping, entity) -> ofNullable(entity.getResponseCode4Transition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(responseCode4TransitionName, (mapping, entity) -> ofNullable(entity.getResponseCode4Transition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.put(responseCode5TransitionId, (mapping, entity) -> ofNullable(entity.getResponseCode5Transition())
						.map(WorkflowTransition::getId)
						.orElse(null)
				)
				.put(responseCode5TransitionName, (mapping, entity) -> ofNullable(entity.getResponseCode5Transition())
						.map(WorkflowTransition::getName)
						.orElse(null)
				)
				.build();
	}

}
