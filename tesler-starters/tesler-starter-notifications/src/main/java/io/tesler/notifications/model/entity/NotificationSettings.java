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

package io.tesler.notifications.model.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTIFICATION_SETTINGS")
public class NotificationSettings extends BaseEntity {

	@Column(name = "EVENT_NAME_CD")
	private LOV eventName;

	@Column(name = "SETTINGS_TYPE_CD")
	private LOV settingsType;

	@Column(name = "DELIVERY_TYPE")
	private int deliveryType;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "NOTIFY_MYSELF")
	private boolean notifyMyself;

	@OneToMany(mappedBy = "notificationSettings")
	private List<NotificationRecipient> notificationRecipients;

}
