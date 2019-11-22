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

package io.tesler.core.security.impl.pep;

import io.tesler.api.data.IDataContainer;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.security.attributes.IAttributeType;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.security.impl.AttributeTypes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;


@Component
public class CrudmaDataFiledVisibilityEnforcement extends
		AbstractPolicyEnforcement<CrudmaAction, IDataContainer> {

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

	@Override
	public Class<IDataContainer> getResultType() {
		return IDataContainer.class;
	}

	@Override
	protected IAttributeType getAttributeType() {
		return AttributeTypes.FORM_FIELD;
	}

	@Override
	protected IDataContainer transform(IDataContainer result, CrudmaAction crudmaAction, List<String> values) {
		result.transformData(data -> {
			if (data instanceof DataResponseDTO) {
				hideFields((DataResponseDTO) data, values);
			}
			return data;
		});
		return result;
	}

	private DataResponseDTO hideFields(DataResponseDTO result, List<String> allowed) {
		Set<String> serializableFields = result.getSerializableFields();
		if (serializableFields != null) {
			serializableFields.retainAll(allowed);
		} else {
			result.setSerializableFields(new HashSet<>(allowed));
		}
		return result;
	}

}
