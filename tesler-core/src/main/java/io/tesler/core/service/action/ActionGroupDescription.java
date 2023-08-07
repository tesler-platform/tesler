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

package io.tesler.core.service.action;

import static io.tesler.core.service.action.TeslerActionIconSpecifier.WITHOUT_ICON;

import io.tesler.api.data.dto.DataResponseDTO;
import java.util.List;

import io.tesler.core.crudma.bc.impl.BcDescription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public final class ActionGroupDescription<T extends DataResponseDTO, D extends BcDescription> {

	private final String key;

	private final String text;

	private final int maxGroupVisualButtonsCount;

	private final List<ActionDescription<T, D>> actions;

	@Setter
	private ActionIconSpecifier iconCode = WITHOUT_ICON;

	@Setter
	private boolean showOnlyIcon = false;

}
