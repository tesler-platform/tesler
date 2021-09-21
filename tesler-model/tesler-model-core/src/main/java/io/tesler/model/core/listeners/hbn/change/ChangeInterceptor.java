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

package io.tesler.model.core.listeners.hbn.change;

import io.tesler.api.data.dao.databaselistener.IChangeListener;
import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dao.databaselistener.InterimChangeListener;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.model.core.api.EntitySerializationEvent;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.listeners.hbn.EntityId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.internal.SessionImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ChangeInterceptor implements ApplicationListener<EntitySerializationEvent> {

	private final Map<Transaction, ChangeEventHolder> events = new ConcurrentHashMap<>();

	private final Optional<List<IChangeListener>> changeListeners;

	private final List<EntityManager> entityManagers;

	private final TransactionService txService;

	public ChangeInterceptor(Optional<List<IChangeListener>> changeListeners,
			List<EntityManager> entityManagers, TransactionService txService) {
		this.changeListeners = changeListeners;
		this.entityManagers = entityManagers;
		this.txService = txService;
	}

	private EntityManager getSupportedEntityManager(String entityClazz) {
		List<EntityManager> supportedEntityManagers = entityManagers.stream().filter(
				entityManager -> entityManager.getMetamodel().getEntities().stream().anyMatch(
						entityType -> com.google.common.base.Objects.equal(entityType.getBindableJavaType().getName(), entityClazz)
				)
		).collect(Collectors.toList());
		if (supportedEntityManagers.size() == 1) {
			return supportedEntityManagers.get(0);
		} else {
			throw new IllegalArgumentException("Can't find unique EntityManager for entity: " + entityClazz);
		}
	}

	@Override
	public void onApplicationEvent(EntitySerializationEvent serializationEvent) {
		if (!txService.isActive()) {
			return;
		}

		BaseEntity entity = serializationEvent.getEntity();
		SessionImpl session = getSupportedEntityManager(Hibernate.getClass(entity).getName()).unwrap(SessionImpl.class);
		session.flush();
		if (needRefresh(session, entity)) {
			session.refresh(entity);
		}
		Transaction transaction = session.getTransaction();
		ChangeEventHolder eventHolder = events.get(transaction);
		if (eventHolder == null) {
			return;
		}
		String name = session.getEntityName(entity);
		AbstractEvent event = eventHolder.getEvent(new EntityId(entity.getId(), name));
		if (event != null) {
			processEvent(event, InterimChangeListener.class);
		}
	}

	private boolean needRefresh(Session session, BaseEntity entity) {
		if (!session.contains(entity) || entity.isNew()) {
			return false;
		}
		// оптимизация - не обновлять, если уже обновились
		// при помощи другого механизма
		// несовпадение vstamp и loadVstamp означает что после того
		// как был выполнен flush, сущность из БД заново не доставалась
		return !Objects.equals(entity.getVstamp(), entity.getLoadVstamp());
	}

	private boolean isSupported(PostDeleteEvent event) {
		return changeListeners.map(
				changeListeners -> changeListeners.stream().anyMatch(
						g -> g.isSupported(event.getEntity())
				)
		).orElse(false);
	}

	private boolean isSupported(PostInsertEvent event) {
		return changeListeners.map(
				changeListeners -> changeListeners.stream().anyMatch(
						g -> g.isSupported(event.getEntity())
				)
		).orElse(false);
	}

	private boolean isSupported(PostUpdateEvent event) {
		return changeListeners.map(
				changeListeners -> changeListeners.stream().anyMatch(
						g -> g.isSupported(event.getEntity())
				)
		).orElse(false);
	}

	public void addEvent(PostDeleteEvent event) {
		if (isSupported(event)) {
			EventSource session = event.getSession();
			events.computeIfAbsent(session.getTransaction(), registerTransaction(session)).addEvent(event);
		}
	}

	public void addEvent(PostInsertEvent event) {
		if (isSupported(event)) {
			EventSource session = event.getSession();
			events.computeIfAbsent(session.getTransaction(), registerTransaction(session)).addEvent(event);
		}
	}

	public void addEvent(PostUpdateEvent event) {
		if (isSupported(event)) {
			EventSource session = event.getSession();
			events.computeIfAbsent(session.getTransaction(), registerTransaction(session)).addEvent(event);
		}
	}

	private Function<Transaction, ChangeEventHolder> registerTransaction(EventSource session) {
		return transaction -> {
			session.getActionQueue().registerProcess((success, implementor) -> events.remove(transaction));
			session.getActionQueue().registerProcess(implementor -> processAllEvents(events.get(transaction)));
			return new ChangeEventHolder();
		};
	}

	private void processAllEvents(ChangeEventHolder eventHolder) {
		if (!txService.isActive()) {
			return;
		}
		// этот код вызывается перед коммитом транзакции
		// когда flush уже выполнился, поэтому все объекты
		// которые есть в сессии уже чистые, а запросы
		// вызываемые из лиснеров могут приводить
		// к автоматическому сбросу, что не нужно
		txService.woAutoFlush(Invoker.of(() -> {
			eventHolder.getEvents().forEach(
					event -> processEvent(event, IChangeListener.class)
			);
			eventHolder.clear();
			txService.flush();
		}));
	}

	private void processEvent(AbstractEvent event, Class<?> listenerClass) {
		processEvent(ChangeVectorFactory.getVector(event), listenerClass);
	}

	public void processEvent(IChangeVector vector, Class<?> listenerClass) {
		processEvent(vector, null, listenerClass);
	}

	public void processEvent(IChangeVector vector, LOV eventName) {
		processEvent(vector, eventName, IChangeListener.class);
	}

	@SneakyThrows
	private void processEvent(IChangeVector vector, LOV eventName, Class<?> listenerClass) {
		try {
			doProcessEvent(vector, eventName, listenerClass);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	private void doProcessEvent(IChangeVector vector, LOV eventName, Class<?> listenerClass) {
		changeListeners.ifPresent(changeListeners -> {
			for (IChangeListener<?> changeListener : changeListeners) {
				if (!listenerClass.isInstance(changeListener)) {
					continue;
				}
				if (!changeListener.canProcess(vector, eventName)) {
					continue;
				}
				changeListener.process(vector, eventName);
			}
		});
	}

}
