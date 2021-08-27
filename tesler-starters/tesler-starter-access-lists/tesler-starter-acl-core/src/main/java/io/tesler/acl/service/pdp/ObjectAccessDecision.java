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

package io.tesler.acl.service.pdp;

import io.tesler.acl.model.CheckDecision;
import io.tesler.acl.model.IAttributeSet;
import io.tesler.acl.model.ICheckResult;
import io.tesler.acl.service.AbstractObjectAccessPoint;
import io.tesler.acl.service.IPolicyDecisionPoint;
import io.tesler.acl.model.CheckResult;
import io.tesler.acl.service.AccessService;
import io.tesler.acl.entity.SecurableEntity;
import io.tesler.acl.entity.types.Permission;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.model.core.entity.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ObjectAccessDecision extends AbstractObjectAccessPoint implements IPolicyDecisionPoint<CrudmaAction> {

	private final AccessService accessService;

	private final ResponseFactory respFactory;

	@Override
	public ICheckResult check(IAttributeSet attributeSet, CrudmaAction CrudmaAction) {
		BusinessComponent bc = CrudmaAction.getBc();
		ResponseService<?, ?> responseService = respFactory.getService(bc.getDescription());
		BaseEntity entity = responseService.hasPersister() ? responseService.getOneAsEntity(bc) : null;
		if (!(entity instanceof SecurableEntity)) {
			return new CheckResult(CheckDecision.Permit);
		}
		Permission permission = accessService.getPermission((SecurableEntity) entity);
		return new CheckResult(
				permission.compareTo(getRequiredPermission(CrudmaAction)) >= 0
						? CheckDecision.Permit
						: CheckDecision.Deny
		);
	}

	private Permission getRequiredPermission(CrudmaAction action) {
		switch (action.getActionType()) {
			case DELETE:
				return Permission.DELETE;
			case INVOKE:
			case UPDATE:
			case PREVIEW:
				return Permission.WRITE;
			default:
				return Permission.READ;
		}
	}


}
