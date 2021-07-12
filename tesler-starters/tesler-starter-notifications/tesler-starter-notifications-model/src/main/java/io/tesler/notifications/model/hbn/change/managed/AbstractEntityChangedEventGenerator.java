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

package io.tesler.notifications.model.hbn.change.managed;

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.api.Tracked;
import io.tesler.notifications.model.api.INotificationEventBuilder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.notifications.model.hbn.change.AbstractEventGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;


public abstract class AbstractEntityChangedEventGenerator<E extends BaseEntity> extends AbstractEventGenerator<E> {

	private final List<EntityManager> entityManagers;

	public AbstractEntityChangedEventGenerator(List<EntityManager> entityManagers) {
		this.entityManagers = entityManagers;
	}

	@Override
	public void process(IChangeVector vector, LOV event) {
		E entity = vector.unwrap(getType());
		if (vector.isUpdate()) {
			Set<String> changedFields = new HashSet<>();
			getTrackedFields(getEvent()).forEach((attribute, title) -> {
				if (vector.hasChanged(attribute)) {
					changedFields.add(title);
				}
			});
			if (!changedFields.isEmpty()) {
				getBuilder(entity)
						.addModel("changed_fields", changedFields)
						.publish();
			}
		}
	}

	protected INotificationEventBuilder getBuilder(E entity) {
		return builder(entity, getEvent())
				.setPerformer(getPerformer(entity));
	}

	protected abstract LOV getEvent();

	@Override
	public boolean canProcess(IChangeVector vector, LOV event) {
		if (!super.canProcess(vector, event)) {
			return false;
		}
		return vector.isUpdate();
	}

	protected INotificationEventBuilder builder(E entity, LOV event) {
		return new DefaultBuilder(entity, event)
				.setPerformer(getPerformer(entity));
	}

	protected Map<Attribute<?, ?>, String> getTrackedFields(LOV event) {
		EntityType<? extends E> entityType = entityManagers.stream()
				.filter(
						entityManager -> entityManager.getMetamodel()
								.getEntities().stream().anyMatch(
										type -> Objects.equals(type.getBindableJavaType(), getType())
								)
				)
				.findFirst().map(EntityManager::getMetamodel).map(metamodel -> metamodel.entity(getType())).orElse(null);
		return Objects.requireNonNull(entityType).getAttributes().stream()
				.map(a -> new DefaultKeyValue<>(a, getFieldTitle(entityType, a, event)))
				.filter(kv -> kv.getValue() != null)
				.collect(Collectors.toMap(DefaultKeyValue::getKey, DefaultKeyValue::getValue));
	}

	private String getFieldTitle(EntityType<? extends E> entityType, Attribute<?, ?> attribute, LOV event) {
		Member member = attribute.getJavaMember();
		if (!(member instanceof Field)) {
			return null;
		}
		Tracked tracked = ((Field) member).getAnnotation(Tracked.class);
		if (tracked == null) {
			return null;
		}
		String[] events = tracked.events();
		if (events.length == 0 || Arrays.asList(events).contains(event.getKey())) {
			return String.format("%s.%s", entityType.getName(), attribute.getName());
		}
		return null;
	}

}
