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

import io.tesler.core.exception.BusinessException;
import io.tesler.core.exception.BusinessIntermediateException;
import io.tesler.core.exception.UnconfirmedException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ErrorResponseDTO {

	private final boolean success = false;

	private Object data;

	private String errorMessage;

	private BusinessError error;

	public ErrorResponseDTO(String e) {
		this.errorMessage = e;
	}

	public ErrorResponseDTO(BusinessException e) {
		this.error = new BusinessError(e.getPopup(), e.getEntity(), null, e.getPostActions());
	}

	public ErrorResponseDTO(UnconfirmedException e) {
		this.error = new BusinessError(null, null, e.getPreInvokeEvents(), null);
	}

	public ErrorResponseDTO(BusinessIntermediateException e) {
		this.data = e.getObject();
		this.error = new BusinessError(null, e.getEntity(), null, null);
	}

}
