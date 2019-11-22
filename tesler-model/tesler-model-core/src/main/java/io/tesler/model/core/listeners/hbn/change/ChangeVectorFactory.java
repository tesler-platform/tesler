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

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.CoreDictionaries.DatabaseEventType;
import io.tesler.api.data.dictionary.LOV;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.metamodel.Attribute;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;


class ChangeVectorFactory {

	static IChangeVector getVector(AbstractEvent event) {
		if (event instanceof PostUpdateEvent) {
			return new UpdateVector((PostUpdateEvent) event);
		}
		if (event instanceof PostInsertEvent) {
			return new InsertVector((PostInsertEvent) event);
		}
		if (event instanceof PostDeleteEvent) {
			return new DeleteVector((PostDeleteEvent) event);
		}
		throw new RuntimeException("Unsupported event " + event.getClass());
	}

	static class UpdateVector implements IChangeVector {

		private final PostUpdateEvent event;

		private final Map<String, Integer> names;

		private UpdateVector(PostUpdateEvent event) {
			this.event = event;
			names = new HashMap<>();
			String[] attributes = event.getPersister().getPropertyNames();
			for (int i = 0; i < attributes.length; i++) {
				names.put(attributes[i], i);
			}
		}

		@Override
		public LOV getEventName() {
			return DatabaseEventType.UPDATE;
		}

		@Override
		public Object getEntity() {
			return event.getEntity();
		}

		@Override
		public boolean isUpdate() {
			return true;
		}

		@Override
		public boolean hasChanged(Attribute<?, ?> attribute) {
			// todo: сюда могут прилетать пустые состояния
			// если мы шарим сущность между потоками
			Object[] oldState = event.getOldState();
			Object[] state = event.getState();
			if (oldState != null && state != null && names.containsKey(attribute.getName())) {
				int index = names.get(attribute.getName());
				return !Objects.equals(oldState[index], state[index]);
			}
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getOldValue(Attribute<?, ?> attribute) {
			// todo: сюда могут прилетать пустые состояния
			// если мы шарим сущность между потоками
			Object[] oldState = event.getOldState();
			if (oldState != null && names.containsKey(attribute.getName())) {
				int index = names.get(attribute.getName());
				return (T) oldState[index];
			}
			return null;
		}

	}

	static class InsertVector implements IChangeVector {

		private final PostInsertEvent event;

		private InsertVector(PostInsertEvent event) {
			this.event = event;
		}

		@Override
		public LOV getEventName() {
			return DatabaseEventType.INSERT;
		}

		@Override
		public Object getEntity() {
			return event.getEntity();
		}

		@Override
		public boolean isNew() {
			return true;
		}

		@Override
		public boolean hasChanged(Attribute<?, ?> attribute) {
			return true;
		}

	}

	static class DeleteVector implements IChangeVector {

		private final PostDeleteEvent event;

		private DeleteVector(PostDeleteEvent event) {
			this.event = event;
		}

		@Override
		public LOV getEventName() {
			return DatabaseEventType.DELETE;
		}

		@Override
		public Object getEntity() {
			return event.getEntity();
		}

		@Override
		public boolean isDelete() {
			return true;
		}

		@Override
		public boolean hasChanged(Attribute<?, ?> attribute) {
			return true;
		}

	}


}
