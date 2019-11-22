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

package io.tesler.core.exception;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.model.core.entity.BaseEntity;


public class VersionMismatchException extends BusinessException {

	public VersionMismatchException() {
		super();
		addPopup(errorMessage("error.version_mismatch_simple"));
	}

	public VersionMismatchException(BaseEntity entity, DataResponseDTO data) {
		super();
		addPopup(buildPopup(entity, data));
	}

	private String buildPopup(BaseEntity entity, DataResponseDTO data) {
		return errorMessage(
				"error.version_mismatch_full",
				entity.getVstamp(),
				data.getVstamp()
		);
	}

}
