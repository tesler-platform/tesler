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

package io.tesler.core.dto.rowmeta;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Builder
@AllArgsConstructor
public class PreAction {

	private final PreActionType preActionType;

	private final String message;

	private final Map<String, String> customParameters;

	private static PreAction preAction(PreActionType preAction, String message, Map<String, String> customParameters) {
		return PreAction.builder()
				.preActionType(preAction)
				.message(message)
				.customParameters(customParameters)
				.build();
	}

	public static PreAction confirm(String message) {
		return PreAction.preAction(PreActionType.CONFIRMATION, message, null);
	}

	public static PreAction confirm() {
		return PreAction.confirm(null);
	}

	public static PreAction info(String message) {
		return PreAction.preAction(PreActionType.INFORMATION, message, null);
	}

	public static PreAction info() {
		return PreAction.info(null);
	}

	public static PreAction error(String message) {
		return PreAction.preAction(PreActionType.ERROR, message, null);
	}

	public static PreAction error() {
		return PreAction.error(null);
	}

	public static PreAction custom(String message, Map<String, String> customParameters) {
		return PreAction.preAction(PreActionType.CUSTOM, message, customParameters);
	}

	public String getType() {
		return preActionType.getType();
	}

	public String getMessage() {
		return message;
	}

	@JsonAnyGetter
	public Map<String, String> getCustomParameters() {
		return customParameters;
	}

	public String getMessage(String action) {
		return isBlank(message) && nonNull(preActionType) ? preActionType.getMessage(trimToEmpty(action)) : message;
	}

}
