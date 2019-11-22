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

package io.tesler.model.core.listeners.hbn.change.notifications;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.notification.EventSettings;
import io.tesler.api.notification.NotificationSettingsProvider;
import io.tesler.api.notification.Recipient;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.notifications.NotificationSettings;
import io.tesler.model.core.entity.notifications.NotificationSettings_;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BaseNotificationSettingsProvider implements NotificationSettingsProvider {

	private final JpaDao jpaDao;

	@Override
	public EventSettings getGlobalSettings(LOV event) {
		Specification<NotificationSettings> specification = (root, cq, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.eventName), event),
				cb.equal(root.get(NotificationSettings_.settingsType), CoreDictionaries.NotificationSettingsType.GLOBAL)
		);
		NotificationSettings settings = jpaDao.getFirstResultOrNull(NotificationSettings.class, specification);
		if (settings == null) {
			return null;
		}
		return new EventSettings(event, settings.getDeliveryType(), settings.isNotifyMyself(), getRecipientTypes(settings));
	}

	@Override
	public EventSettings getUserSettings(LOV event, Long userId) {
		Specification<NotificationSettings> specification = (root, cq, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.eventName), event),
				cb.equal(root.get(NotificationSettings_.settingsType), CoreDictionaries.NotificationSettingsType.PERSONAL),
				cb.equal(root.get(NotificationSettings_.userId), userId)
		);
		NotificationSettings settings = jpaDao.getFirstResultOrNull(NotificationSettings.class, specification);
		if (settings == null) {
			return null;
		}
		return new EventSettings(event, settings.getDeliveryType(), settings.isNotifyMyself(), getRecipientTypes(settings));
	}

	protected List<Recipient> getRecipientTypes(NotificationSettings settings) {
		return settings.getNotificationRecipients().stream()
				.map(r -> new Recipient(r.getRecipientType(), r.isSameDeptOnly()))
				.collect(Collectors.toList());
	}

}
