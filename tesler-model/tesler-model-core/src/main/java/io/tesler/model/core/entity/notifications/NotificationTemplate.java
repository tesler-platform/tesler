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

package io.tesler.model.core.entity.notifications;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.notification.INotificationTemplate;
import io.tesler.model.core.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate extends BaseEntity implements INotificationTemplate {

	@Column(name = "EVENT_NAME_CD")
	private LOV eventName;

	@Column(name = "MIME_TYPE_CD")
	private LOV mimeType;

	@Column(name = "SUBJECT")
	private String subject;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "UI_SUBJECT")
	private String uiSubject;

	@Column(name = "UI_MESSAGE")
	private String uiMessage;

	@Column(name = "URL")
	private String url;

}
