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

package io.tesler.acl.service;

import io.tesler.acl.model.AclCrudmaAction;
import io.tesler.acl.model.IObligationSet;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.ext.CrudmaGatewayInvokeExtensionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class PolicyEnforcerCrudmaGatewayInvokeExtensionProvider implements CrudmaGatewayInvokeExtensionProvider {

	private final PolicyEnforcer policyEnforcer;

	@Override
	public <T> Invoker<T, RuntimeException> extendInvoker(CrudmaAction CrudmaAction, Invoker<T, RuntimeException> invoker,
			boolean readOnly) {
		return () -> {
			// check that the action can be performed and
			// set of obligations to be observed
			AclCrudmaAction aclCrudmaAction = new AclCrudmaAction(CrudmaAction);
			IObligationSet obligationSet = policyEnforcer.check(aclCrudmaAction);
			// make a set of obligations available from anywhere
			aclCrudmaAction.setObligationSet(obligationSet);
			T invokeResult = invoker.invoke();
			// modify the result of the action
			return policyEnforcer.transform(invokeResult, aclCrudmaAction, obligationSet);
		};
	}

}
