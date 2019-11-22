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

import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.api.data.dto.rowmeta.FieldsDTO;
import io.tesler.api.security.IPolicyEnforcementPoint;
import io.tesler.api.security.obligations.IObligationSet;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.MetaContainer;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.security.impl.AbstractObjectAccessPoint;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.model.core.api.security.AccessService;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.security.SecurableEntity;
import io.tesler.model.core.entity.security.types.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ObjectAccessEnforcement extends AbstractObjectAccessPoint implements
		IPolicyEnforcementPoint<CrudmaAction, MetaContainer> {

	private final AccessService accessService;

	private final ResponseFactory respFactory;

	@Override
	public Class<MetaContainer> getResultType() {
		return MetaContainer.class;
	}

	@Override
	public MetaContainer transform(MetaContainer result, CrudmaAction crudmaAction, IObligationSet obligationSet) {
		BusinessComponent bc = crudmaAction.getBc();
		ResponseService<?, ?> responseService = respFactory.getService(bc.getDescription());
		BaseEntity entity = responseService.hasPersister() ? responseService.getOneAsEntity(bc) : null;
		if (!(entity instanceof SecurableEntity)) {
			return result;
		}
		Permission permission = accessService.getPermission((SecurableEntity) entity);
		if (permission.compareTo(Permission.READ) > 0) {
			return result;
		}
		result.transformMeta(meta -> {
					if (meta instanceof MetaDTO) {
						disableFields((MetaDTO) meta);
					}
					return meta;
				}
		);
		return result;
	}

	private MetaDTO disableFields(MetaDTO result) {
		FieldsDTO fieldsDTO = result.getRow().getFields();
		for (FieldDTO field : fieldsDTO) {
			field.setDisabled(true);
		}
		return result;
	}

}
