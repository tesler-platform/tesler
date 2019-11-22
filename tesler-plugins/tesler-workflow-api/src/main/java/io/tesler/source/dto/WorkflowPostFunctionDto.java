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

import static io.tesler.core.util.filter.SearchParameterType.LONG;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dict.WorkflowLov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.SearchParameterType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowPostFunctionDto extends DataResponseDTO {

	@SearchParameter(type = LONG)
	private Long seq;

	@WorkflowLov(WorkflowDictionaryType.WF_TRN_ACT)
	@SearchParameter(type = SearchParameterType.LOV)
	private String actionCd;

	@SearchParameter(type = LONG)
	private Long stepTerm;

}
