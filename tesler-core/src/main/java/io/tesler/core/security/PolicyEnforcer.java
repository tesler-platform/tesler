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

package io.tesler.core.security;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.security.CheckDecision;
import io.tesler.api.security.ICheckResult;
import io.tesler.api.security.IPolicyDecisionPoint;
import io.tesler.api.security.IPolicyEnforcementPoint;
import io.tesler.api.security.IPolicyInformationPoint;
import io.tesler.api.security.IPolicyRegistry;
import io.tesler.api.security.attributes.IAttributeSet;
import io.tesler.api.security.obligations.IObligationSet;
import io.tesler.api.security.obligations.IObligationSupplier;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.security.impl.CheckResult;
import io.tesler.core.security.impl.attributes.AttributeSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class PolicyEnforcer {

	private final IPolicyRegistry registry;

	public PolicyEnforcer(@Lazy IPolicyRegistry registry) {
		this.registry = registry;
	}

	public <V> V transform(V result, IObligationSupplier<?> supplier) {
		return transform(result, supplier.getContext(), supplier.getObligationSet());
	}

	public <V> V transform(V result, Object context, IObligationSet obligationSet) {
		V transformed = result;
		for (IPolicyEnforcementPoint pep : getPolicyEnforcementPoints(transformed, context)) {
			transformed = (V) pep.transform(transformed, context, obligationSet);
		}
		return transformed;
	}

	@SuppressWarnings("unchecked")
	public IObligationSet check(Object context) {
		IAttributeSet attributeSet = new AttributeSet();
		for (IPolicyInformationPoint pip : getPolicyInformationPoints(context)) {
			attributeSet = attributeSet.merge(pip.getAttributes(context));
		}
		ICheckResult checkResult = new CheckResult(CheckDecision.Permit);
		for (IPolicyDecisionPoint pdp : getPolicyDecisionPoints(context)) {
			checkResult = checkResult.merge(pdp.check(attributeSet, context));
			if (checkResult.getDecision() != CheckDecision.Permit) {
				throw new BusinessException().addPopup(errorMessage("error.action_not_allowed"));
			}
		}
		return checkResult.getObligationSet();
	}

	private List<IPolicyInformationPoint> getPolicyInformationPoints(Object context) {
		return registry.getPolicies(IPolicyInformationPoint.class::isInstance)
				.map(IPolicyInformationPoint.class::cast)
				.filter(p -> p.isContextSupported(context))
				.collect(Collectors.toList());
	}

	private List<IPolicyDecisionPoint> getPolicyDecisionPoints(Object context) {
		return registry.getPolicies(IPolicyDecisionPoint.class::isInstance)
				.map(IPolicyDecisionPoint.class::cast)
				.filter(p -> p.isContextSupported(context))
				.collect(Collectors.toList());
	}

	private List<IPolicyEnforcementPoint> getPolicyEnforcementPoints(Object result, Object context) {
		return registry.getPolicies(IPolicyEnforcementPoint.class::isInstance)
				.map(IPolicyEnforcementPoint.class::cast)
				.filter(p -> p.isResultSupported(result, context))
				.collect(Collectors.toList());
	}

}
