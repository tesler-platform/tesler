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

import io.tesler.model.core.listeners.hbn.EntityId;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;


public class ChangeEventHolder {

	private final Map<EntityId, AbstractEvent> events = new LinkedHashMap<>();

	public Collection<AbstractEvent> getEvents() {
		return events.values();
	}

	public AbstractEvent getEvent(EntityId entityId) {
		return events.get(entityId);
	}

	public void clear() {
		events.clear();
	}

	protected void addEvent(PostInsertEvent event) {
		events.compute(getEntityId(event), (entityId, current) -> merge(current, event));
	}

	protected void addEvent(PostUpdateEvent event) {
		events.compute(getEntityId(event), (entityId, current) -> merge(current, event));
	}

	protected void addEvent(PostDeleteEvent event) {
		events.compute(getEntityId(event), (entityId, current) -> merge(current, event));
	}

	private EntityId getEntityId(PostInsertEvent event) {
		return new EntityId(event.getId(), event.getPersister().getEntityName());
	}

	private EntityId getEntityId(PostUpdateEvent event) {
		return new EntityId(event.getId(), event.getPersister().getEntityName());
	}

	private EntityId getEntityId(PostDeleteEvent event) {
		return new EntityId(event.getId(), event.getPersister().getEntityName());
	}

	private Object[] getOldState(PostUpdateEvent event) {
		Object[] oldState = event.getOldState();
		if (oldState != null) {
			return oldState;
		}
		EntityPersister persister = event.getPersister();
		return persister.getDatabaseSnapshot(event.getId(), event.getSession());
	}

	protected AbstractEvent merge(AbstractEvent existing, PostInsertEvent event) {
		return event;
	}

	protected AbstractEvent merge(AbstractEvent existing, PostUpdateEvent event) {
		if (existing == null) {
			return new PostUpdateEvent(
					event.getEntity(),
					event.getId(),
					event.getState(),
					getOldState(event),
					event.getDirtyProperties(),
					event.getPersister(),
					event.getSession()
			);
		}
		if (existing instanceof PostInsertEvent) {
			return new PostInsertEvent(
					event.getEntity(),
					event.getId(),
					event.getState(),
					event.getPersister(),
					event.getSession()
			);
		}
		if (existing instanceof PostUpdateEvent) {
			return new PostUpdateEvent(
					event.getEntity(),
					event.getId(),
					event.getState(),
					((PostUpdateEvent) existing).getOldState(),
					event.getDirtyProperties(),
					event.getPersister(),
					event.getSession()
			);
		}
		return null;
	}

	protected AbstractEvent merge(AbstractEvent existing, PostDeleteEvent event) {
		if (existing == null) {
			return event;
		}
		if (existing instanceof PostInsertEvent) {
			return null;
		}
		if (existing instanceof PostUpdateEvent) {
			return event;
		}
		return null;
	}

}
