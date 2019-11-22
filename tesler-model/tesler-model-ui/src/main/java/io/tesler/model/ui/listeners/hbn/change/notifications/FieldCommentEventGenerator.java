/*-
 * #%L
 * IO Tesler - Model UI
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

package io.tesler.model.ui.listeners.hbn.change.notifications;

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.api.notifications.INotificationEventBuilder;
import io.tesler.model.core.listeners.hbn.change.notifications.AbstractEventGenerator;
import io.tesler.model.ui.entity.FieldComment;
import org.springframework.stereotype.Component;


@Component
public class FieldCommentEventGenerator extends AbstractEventGenerator<FieldComment> {

	@Override
	public void process(IChangeVector vector, LOV event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canProcess(IChangeVector vector, LOV event) {
		return false;
	}

	@Override
	public Class<? extends FieldComment> getType() {
		return FieldComment.class;
	}

	public INotificationEventBuilder builder(FieldComment comment, LOV event) {
		return new DefaultBuilder(comment, event);
	}

}
