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

package io.tesler.model.core.listeners.hbn.change.notifications.unmanaged;

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.api.notifications.INotificationEventBuilder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.listeners.hbn.change.notifications.AbstractEventGenerator;


public abstract class AbstractUnManagedEventGenerator<E extends BaseEntity> extends AbstractEventGenerator<E> {

	@Override
	public boolean canProcess(IChangeVector vector, LOV event) {
		if (!super.canProcess(vector, event)) {
			return false;
		}
		return vector.isUnManaged() && isEventSupported(event);
	}

	@Override
	public void process(IChangeVector vector, LOV event) {
		builder(vector.unwrap(getType()), event).publish();
	}

	protected INotificationEventBuilder builder(E entity, LOV event) {
		return new DefaultBuilder(entity, event);
	}

	protected abstract boolean isEventSupported(LOV event);

}
