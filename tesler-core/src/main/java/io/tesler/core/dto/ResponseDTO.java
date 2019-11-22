/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.dto;

import io.tesler.api.data.ResultPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

	private final boolean success = true;

	private Object data;

	private Boolean hasNext;

	public ResponseDTO() {
	}

	public ResponseDTO(Object data) {
		this.data = data;
	}

	public ResponseDTO(Object data, boolean hasNext) {
		this.data = data;
		this.hasNext = hasNext;
	}

	public ResponseDTO(ResultPage resultPage) {
		this.data = resultPage.getResult();
		this.hasNext = resultPage.isHasNext();
	}

}
