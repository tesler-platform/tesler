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

import io.tesler.api.util.Invoker;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class SpecificDatabaseTransactionalService {

	@PersistenceContext
	private EntityManager em;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T, E extends Throwable> T rollbackOnlyTransaction(Invoker<T, E> invoker) throws E {
		setRollbackOnly();
		return invoker.invoke();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public <T, E extends Throwable> T readOnlyTransaction(Invoker<T, E> invoker) throws E {
		setRollbackOnly();
		return invoker.invoke();
	}

	public void setRollbackOnly() {
		if (isActive()) {
			// в спринге реализовано несколько странно:
			// AbstractPlatformTransactionManager.doSetRollbackOnly вызывается только
			// при возникновении исключения и там он помечает верхнеуровневую
			// транзакцию, а в случае нормального поведения помечается только
			// текущая транзакция, поэтому этот метод сделан приватным и
			// и вызывается только в случае Propagation.REQUIRES_NEW
			// помимо этого помечаем транзакцию хибернейта как rollback only
			// чтобы все "вложенные" транзакции тоже "откатывались"
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// напрямую нельзы вызывать EntityManager.getTransaction, см.
			// SharedEntityManagerCreator.SharedEntityManagerInvocationHandler.invoke
			Session session = em.unwrap(Session.class);
			session.getTransaction().setRollbackOnly();
		}
	}

	public boolean isActive() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

}
