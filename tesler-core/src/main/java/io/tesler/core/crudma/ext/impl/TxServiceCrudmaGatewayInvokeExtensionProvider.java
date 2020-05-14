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

package io.tesler.core.crudma.ext.impl;

import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.ext.CrudmaGatewayInvokeExtensionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(200)
@Slf4j
public class TxServiceCrudmaGatewayInvokeExtensionProvider implements CrudmaGatewayInvokeExtensionProvider {

	private final TransactionService txService;

	@Override
	public <T> Invoker<T, RuntimeException> extendInvoker(CrudmaAction crudmaAction, Invoker<T, RuntimeException> invoker,
			boolean readOnly) {
		return () -> {
			if (readOnly) {
				log.debug("Open read-only transaction for crudmaAction: " + crudmaAction);
				return txService.invokeInNewRollbackOnlyTx(invoker);
			}
			log.debug("Open read-write transaction for crudmaAction: " + crudmaAction);
			return txService.invokeInNewTx(invoker);
		};
	}

}
