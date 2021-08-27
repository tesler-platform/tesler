/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.acl.model;

import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.bc.BusinessComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(chain = true)
@ToString
public class AclCrudmaAction implements CrudmaAction, IObligationSupplier<CrudmaAction> {

	private final CrudmaAction crudmaAction;

	@Getter
	@Setter
	private IObligationSet obligationSet;

	@Override
	public CrudmaAction getContext() {
		return this;
	}

	@Override
	public CrudmaActionType getActionType() {
		return crudmaAction.getActionType();
	}

	@Override
	public String getDescription() {
		return crudmaAction.getDescription();
	}

	@Override
	public String getName() {
		return crudmaAction.getName();
	}

	@Override
	public BusinessComponent getBc() {
		return crudmaAction.getBc();
	}

	@Override
	public String getOriginalActionType() {
		return crudmaAction.getOriginalActionType();
	}

	@Override
	public CrudmaAction setDescription(String description) {
		return crudmaAction.setDescription(description);
	}

	@Override
	public CrudmaAction setName(String name) {
		return crudmaAction.setName(name);
	}

	@Override
	public CrudmaAction setBc(BusinessComponent bc) {
		return crudmaAction.setBc(bc);
	}

	@Override
	public CrudmaAction setOriginalActionType(String originalActionType) {
		return crudmaAction.setOriginalActionType(originalActionType);
	}

}
