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

import io.tesler.api.security.IPolicyEnforcementPoint;
import io.tesler.api.security.attributes.IAttributeType;
import io.tesler.api.security.obligations.IObligationSet;
import java.util.List;


public abstract class AbstractPolicyEnforcement<T, V> implements IPolicyEnforcementPoint<T, V> {

	protected abstract IAttributeType getAttributeType();

	protected abstract V transform(V result, T context, List<String> values);

	@Override
	public final V transform(V result, T context, IObligationSet obligationSet) {
		IAttributeType attributeType = getAttributeType();
		if (obligationSet == null || obligationSet.isEmpty()) {
			return result;
		}
		if (!obligationSet.isApplicable(attributeType)) {
			return result;
		}
		return transform(result, context, obligationSet.getValues(attributeType));
	}

}
