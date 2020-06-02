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

import static io.tesler.api.util.i18n.LocalizationFormatter.uiMessage;
import static io.tesler.core.service.action.ActionAvailableChecker.ALWAYS_TRUE;
import static io.tesler.core.service.action.ActionAvailableChecker.NOT_NULL_ID;
import static io.tesler.core.service.action.ActionAvailableChecker.NOT_NULL_PARENT_ID;

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.service.action.ActionAvailableChecker;
import io.tesler.core.service.action.TeslerActionIconSpecifier;

import java.util.Objects;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ActionType {

	CREATE("create", () -> uiMessage("action.create"), TeslerActionIconSpecifier.PLUS, NOT_NULL_PARENT_ID),
	CANCEL_CREATE("cancel-create", () -> uiMessage("action.cancel-create"), TeslerActionIconSpecifier.CLOSE, ALWAYS_TRUE),
	SAVE("save", () -> uiMessage("action.save"), TeslerActionIconSpecifier.SAVE, NOT_NULL_ID),
	COPY("copy", () -> uiMessage("action.copy"), TeslerActionIconSpecifier.COPY, NOT_NULL_ID),
	ASSOCIATE("associate", () -> uiMessage("action.add"), TeslerActionIconSpecifier.PLUS, NOT_NULL_PARENT_ID),
	DELETE("delete", () -> uiMessage("action.delete"), TeslerActionIconSpecifier.DELETE, NOT_NULL_ID);

	private final String type;

	private final Supplier<String> text;

	private final TeslerActionIconSpecifier icon;

	private final ActionAvailableChecker baseAvailableChecker;

	public boolean isTypeOf(ActionDTO action) {
		return action != null && Objects.equals(getType(), action.getType());
	}

}
