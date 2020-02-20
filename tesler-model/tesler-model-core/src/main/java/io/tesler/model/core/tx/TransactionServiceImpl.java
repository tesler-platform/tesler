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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service(TransactionService.SERVICE_NAME)
public class TransactionServiceImpl implements TransactionService {

	private final List<EntityManager> entityManagers;

	public TransactionServiceImpl(List<EntityManager> entityManagers) {
		this.entityManagers = entityManagers;
	}

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
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T, E extends Throwable> T invokeInNewRollbackOnlyTx(Invoker<T, E> invoker) throws E {
		setRollbackOnly();
		return invoker.invoke();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public <T, E extends Throwable> T invokeInNewROTx(Invoker<T, E> invoker) throws E {
		setRollbackOnly();
		return invoker.invoke();
	}

	@Override
	public boolean isRollbackOnly() {
		return TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
	}

	@Override
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
			entityManagers.forEach(
					entityManager -> {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						// напрямую нельзы вызывать EntityManager.getTransaction, см.
						// SharedEntityManagerCreator.SharedEntityManagerInvocationHandler.invoke
						Session session = entityManager.unwrap(Session.class);
						session.getTransaction().setRollbackOnly();
					}
			);
		}
	}

	@Override
	public boolean isActive() {
		return TransactionSynchronizationManager.isActualTransactionActive();
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

	@Override
	@Transactional
	public <T> T woAutoFlush(Invoker<T, RuntimeException> invoker) {
		Map<Session, FlushMode> sessionsWithFlushModes = entityManagers.stream().map(
				entityManager -> entityManager.unwrap(Session.class)
		).collect(Collectors.toMap(Function.identity(), Session::getHibernateFlushMode));
		try {
			sessionsWithFlushModes.forEach(
					(session, flushMode) -> session.setHibernateFlushMode(FlushMode.MANUAL)
			);
			return invoker.invoke();
		} finally {
			sessionsWithFlushModes.forEach(Session::setHibernateFlushMode);
		}
	}

	@Override
	public void flush() {
		if (isActive()) {
			entityManagers.forEach(EntityManager::flush);
		}
	}

}
