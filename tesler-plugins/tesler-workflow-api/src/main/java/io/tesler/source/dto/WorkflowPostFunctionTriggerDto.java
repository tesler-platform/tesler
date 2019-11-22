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

import io.tesler.api.data.dto.DataResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowPostFunctionTriggerDto extends DataResponseDTO {

	private Long requestPostFunctionId;

	private String requestPostFunctionType;

	private Long responseWaitStepId;

	private String responseWaitStepName;

	private Long responseCode1TransitionId;

	private String responseCode1TransitionName;

	private Long responseCode2TransitionId;

	private String responseCode2TransitionName;

	private Long responseCode3TransitionId;

	private String responseCode3TransitionName;

	private Long responseCode4TransitionId;

	private String responseCode4TransitionName;

	private Long responseCode5TransitionId;

	private String responseCode5TransitionName;

}
