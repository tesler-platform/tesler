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

import io.tesler.acl.service.IPolicyEnforcementPoint;
import io.tesler.acl.service.AccessService;
import io.tesler.acl.entity.SecurableEntity;
import io.tesler.acl.entity.types.Permission;
import io.tesler.acl.model.IObligationSet;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class SpecificationEnforcement implements IPolicyEnforcementPoint<CrudmaAction, Specification> {

	private final AccessService accessService;

	@Override
	public Class<Specification> getResultType() {
		return Specification.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Specification transform(Specification result, CrudmaAction context, IObligationSet obligationSet) {
		return (root, query, cb) -> {
			Class<?> cls = root.getModel().getBindableJavaType();
			if (SecurableEntity.class.isAssignableFrom(cls)) {
				return Specification.where(result).and(
						accessService.getSecuritySpecification(Permission.READ)
				).toPredicate(root, query, cb);
			}
			return result.toPredicate(root, query, cb);
		};
	}

	@Override
	public Class<CrudmaAction> getContextType() {
		return CrudmaAction.class;
	}

}
