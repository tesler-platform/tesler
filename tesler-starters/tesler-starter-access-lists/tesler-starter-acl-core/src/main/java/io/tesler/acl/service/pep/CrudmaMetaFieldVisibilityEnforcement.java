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

package io.tesler.acl.service.pep;

import io.tesler.acl.model.IAttributeType;
import io.tesler.acl.model.AttributeTypes;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.api.data.dto.rowmeta.FieldsDTO;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.MetaContainer;
import io.tesler.core.dto.rowmeta.MetaDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;


@Component
public class CrudmaMetaFieldVisibilityEnforcement extends
		AbstractPolicyEnforcement<CrudmaAction, MetaContainer> {

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

	@Override
	public Class<MetaContainer> getResultType() {
		return MetaContainer.class;
	}

	@Override
	protected IAttributeType getAttributeType() {
		return AttributeTypes.FORM_FIELD;
	}

	@Override
	public MetaContainer transform(MetaContainer result, CrudmaAction CrudmaAction, List<String> allowedValues) {
		result.transformMeta(meta -> {
			if (meta instanceof MetaDTO) {
				hideFields((MetaDTO) meta, allowedValues);
			}
			return meta;
		});
		return result;
	}

	private MetaDTO hideFields(MetaDTO result, List<String> allowed) {
		FieldsDTO fieldsDTO = result.getRow().getFields();
		Set<String> fields = new HashSet<>(allowed);
		for (FieldDTO field : fieldsDTO) {
			if (!fields.contains(field.getKey())) {
				field.setDisabled(true);
				field.setCurrentValue(null);
			}
		}
		return result;
	}

}
