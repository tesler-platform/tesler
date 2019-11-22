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

package io.tesler.model.core.listeners.hbn;

import static org.hibernate.event.spi.EventType.AUTO_FLUSH;
import static org.hibernate.event.spi.EventType.FLUSH;
import static org.hibernate.event.spi.EventType.FLUSH_ENTITY;
import static org.hibernate.event.spi.EventType.POST_DELETE;
import static org.hibernate.event.spi.EventType.POST_INSERT;
import static org.hibernate.event.spi.EventType.POST_UPDATE;

import io.tesler.model.core.listeners.hbn.change.ChangeInterceptor;
import io.tesler.model.core.listeners.hbn.flush.FlushInterceptor;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DatabaseListener implements
		PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener,
		FlushEntityEventListener, FlushEventListener, AutoFlushEventListener {

	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private ChangeInterceptor changeInterceptor;

	@Autowired
	private FlushInterceptor flushInterceptor;

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		return false;
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		changeInterceptor.addEvent(event);
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		changeInterceptor.addEvent(event);
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		changeInterceptor.addEvent(event);
	}

	@Override
	public void onAutoFlush(AutoFlushEvent event) throws HibernateException {
		flushInterceptor.onFlush(event);
	}

	@Override
	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
		flushInterceptor.onFlush(event);
	}

	@Override
	public void onFlush(FlushEvent event) throws HibernateException {
		flushInterceptor.onFlush(event);
	}

	@PostConstruct
	protected void init() {
		prependListener(POST_INSERT, this);
		prependListener(POST_UPDATE, this);
		prependListener(POST_DELETE, this);
		appendListener(FLUSH_ENTITY, this);
		appendListener(FLUSH, this);
		appendListener(AUTO_FLUSH, this);
	}

	private EventListenerRegistry getRegistry() {
		SessionFactoryImplementor implementor = emf.unwrap(SessionFactoryImplementor.class);
		return implementor.getServiceRegistry().getService(EventListenerRegistry.class);
	}

	private <T> void prependListener(EventType<T> eventType, T listener) {
		getRegistry().getEventListenerGroup(eventType).prependListener(listener);
	}

	private <T> void appendListener(EventType<T> eventType, T listener) {
		getRegistry().getEventListenerGroup(eventType).appendListener(listener);
	}

}
