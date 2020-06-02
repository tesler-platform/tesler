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

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.PreAction;

import java.util.Collections;
import java.util.List;


public abstract class ResponseServiceAction<T extends DataResponseDTO> {

	public abstract String getButtonName();

	public BcIdentifier getBCName() {
		return null;
	}

	public abstract boolean isAvailable(BusinessComponent bc);

	public abstract ActionResultDTO<T> invoke(BusinessComponent bc, T data);

	public PreAction preActionSpecifier(BusinessComponent bc) {
		return null;
	}

	public List<PreActionEvent> preActionEventSpecifier(BusinessComponent bc) {
		return Collections.emptyList();
	}

	public List<String> dataValidator(BusinessComponent bc, DataResponseDTO data, T entityDto) {
		return Collections.emptyList();
	}

	public ActionIconSpecifier getIcon() {
		return TeslerActionIconSpecifier.WITHOUT_ICON;
	}

	public ActionScope getScope() {
		return ActionScope.RECORD;
	}

	public boolean isAutoSaveBefore() {
		return true;
	}

	public boolean isIconWithText() {
		return false;
	}


}
