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

package io.tesler.acl.service.pip;

import io.tesler.acl.model.IAttributeSet;
import io.tesler.acl.service.IPolicyInformationPoint;
import io.tesler.acl.model.Attribute;
import io.tesler.acl.model.AttributeSet;
import io.tesler.acl.model.AttributeTypes;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import org.springframework.stereotype.Service;


@Service
public class CrudmaActionInformation implements IPolicyInformationPoint<CrudmaAction> {

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

	@Override
	public IAttributeSet getAttributes(CrudmaAction context) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.addAttribute(new Attribute(
				context.getActionType().name(),
				AttributeTypes.ACTION_TYPE
		));
		attributeSet.addAttribute(new Attribute(
				context.getName(),
				AttributeTypes.BC_ACTION
		));
		attributeSet.addAttribute(new Attribute(
				context.getBc().getName(),
				AttributeTypes.BUSINESS_COMPONENT
		));
		attributeSet.addAttribute(new Attribute(
				context.getBc().getId(),
				AttributeTypes.BUSINESS_OBJECT
		));
		return attributeSet;
	}

}
