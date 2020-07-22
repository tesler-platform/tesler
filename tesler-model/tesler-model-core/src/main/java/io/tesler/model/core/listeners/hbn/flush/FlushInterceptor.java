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

package io.tesler.model.core.listeners.hbn.flush;

import io.tesler.api.service.tx.ITransactionStatus;
import io.tesler.model.core.api.RefreshOnFlush;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Performs automatic updates {@link Session#refresh(java.lang.Object)}
 * objects marked as {@link RefreshOnFlush} after calling {@link org.hibernate.Session#flush()}
 */
@Component
public class FlushInterceptor {

	private final Map<Transaction, Set<RefreshOnFlush>> entities = new ConcurrentHashMap<>();

	@Autowired
	private ITransactionStatus txStatus;

	public void onFlush(FlushEntityEvent event) {
		Object entity = event.getEntity();
		if (!(entity instanceof RefreshOnFlush)) {
			return;
		}
		// игнорируем то, что сливается в базу при
		// коммите транзакции - мы этими сущностями
		// все равно не сможем воспользоваться
		if (txStatus.isCommitting()) {
			return;
		}
		EventSource session = event.getSession();
		entities.computeIfAbsent(
				session.getTransaction(),
				registerTransaction(session)
		).add((RefreshOnFlush) entity);
	}

	public void onFlush(FlushEvent event) {
		// здесь настоящий flush, который вызывается
		// или при коммите транзакции или руками
		performRefresh(event.getSession(), true);
	}

	public void onFlush(AutoFlushEvent event) {
		performRefresh(event.getSession(), event.isFlushRequired());
	}

	private void performRefresh(EventSource session, boolean flushRequired) {
		// при autoflush хибернейт для каждой сущности вызывает FLUSH_ENTITY,
		// однако flush может и не выполнить, например если решит что
		// грязные объекты не влияют на результат выполнения запроса, однако
		// при настоящем flush нам-таки нужно сохранить результат
		if (!flushRequired) {
			return;
		}
		Set<RefreshOnFlush> pending = getSnapshot(session);
		// если flush осуществляется на коммите, то нам ничего
		// обновлять не нужно, поэтому просто удаляем все
		if (pending == null || txStatus.isCommitting()) {
			return;
		}
		pending.forEach(e -> {
			if (session.contains(e) && e.needRefresh()) {
				session.refresh(e);
			}
		});
	}

	private Function<Transaction, Set<RefreshOnFlush>> registerTransaction(EventSource session) {
		return transaction -> {
			// если flush осуществляется на коммите, то нам ничего
			// обновлять не нужно, поэтому просто удаляем все
			session.getActionQueue().registerProcess((success, implementor) -> entities.remove(transaction));
			session.getActionQueue().registerProcess(implementor -> entities.remove(transaction));
			return createIdentitySet();
		};
	}

	private Set<RefreshOnFlush> getSnapshot(Session session) {
		// заменяем то что накомилось на пустой сет, потому что иначе
		// получится, что мы зарегистриуем себя в ActionQueue несколько
		// раз, хотя транзакция будет одна и та же
		return entities.replace(session.getTransaction(), createIdentitySet());
	}

	private <T> Set<T> createIdentitySet() {
		// для сущностей equals и hashCode работают неправильно
		// поэтому для сравнения используем identity
		return Collections.newSetFromMap(new IdentityHashMap<>());
	}


}
