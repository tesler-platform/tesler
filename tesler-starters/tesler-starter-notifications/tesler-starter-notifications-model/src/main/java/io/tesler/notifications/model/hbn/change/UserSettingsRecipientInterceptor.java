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

package io.tesler.notifications.model.hbn.change;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.notifications.api.EventSettings;
import io.tesler.notifications.api.NotificationSettingsProvider;
import io.tesler.notifications.model.api.EventRecipientInterceptor;
import io.tesler.model.core.entity.User;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Service
public class UserSettingsRecipientInterceptor implements EventRecipientInterceptor {

	private final NotificationSettingsProvider notificationSettingsCache;

	@Override
	public void modifyRoles(EventSettings globalSettings, Set<LOV> recipientRoles, User user) {
		EventSettings settings = notificationSettingsCache.getUserSettings(globalSettings.getEvent(), user.getId());
		// если есть пользовательские настройки,
		// то убираем исключенные роли из рассылки,
		// в противном случае используем глобальные
		if (settings != null) {
			settings.getRecipients().forEach(role ->
					recipientRoles.remove(role.getRole())
			);
		}
	}

}
