/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.tx;

import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service(TransactionService.SERVICE_NAME)
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	@Qualifier("primaryDatabase")
	private Database primaryDatabase;

	@Autowired
	private SpecificDatabaseTransactionalService specificDatabaseTransactionalService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public <T, E extends Throwable> T invokeInTx(Invoker<T, E> invoker) throws E {
		return invoker.invoke();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T, E extends Throwable> T invokeInNewTx(Invoker<T, E> invoker) throws E {
		return invoker.invoke();
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <T, E extends Throwable> T invokeNoTx(Invoker<T, E> invoker) throws E {
		return invoker.invoke();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public <T, E extends Throwable> T invokeInNewROTx(Invoker<T, E> invoker) throws E {
		if (primaryDatabase == Database.POSTGRESQL) {
			return specificDatabaseTransactionalService.rollbackOnlyTransaction(invoker);
		} else {
			return specificDatabaseTransactionalService.readOnlyTransaction(invoker);
		}
	}

	@Override
	public void setRollbackOnly() {
		specificDatabaseTransactionalService.setRollbackOnly();
	}

	@Override
	public boolean isRollbackOnly() {
		return TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
	}

	@Override
	public boolean isActive() {
		return specificDatabaseTransactionalService.isActive();
	}

	@Override
	public <T, E extends RuntimeException> void invokeAfterCompletion(Invoker<T, E> invoker) throws E {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCompletion(int status) {
					if (TransactionSynchronization.STATUS_COMMITTED == status) {
						invoker.invoke();
					}
				}
			});
		} else {
			invoker.invoke();
		}
	}

}
