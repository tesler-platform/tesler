/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.notifications.api;

import io.tesler.api.data.dictionary.LOV;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationEvent extends ApplicationEvent {

	private LOV event;

	private LOV mimeType;

	private String subject;

	private String message;

	private String uiSubject;

	private String uiMessage;

	private String url;

	private Long recipientId;

	private int deliveryType;

	public NotificationEvent(Object source) {
		super(source);
	}


}
