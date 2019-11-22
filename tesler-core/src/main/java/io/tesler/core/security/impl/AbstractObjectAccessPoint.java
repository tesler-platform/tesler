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

package io.tesler.core.security.impl;

import io.tesler.api.security.IPolicyPoint;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;


public abstract class AbstractObjectAccessPoint implements IPolicyPoint<CrudmaAction> {

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

	@Override
	public boolean isContextSupported(Object context) {
		if (!IPolicyPoint.super.isContextSupported(context)) {
			return false;
		}
		BusinessComponent bc = ((CrudmaAction) context).getBc();
		BcDescription description = bc.getDescription();
		return (description instanceof InnerBcDescription) && bc.getId() != null;
	}

}
