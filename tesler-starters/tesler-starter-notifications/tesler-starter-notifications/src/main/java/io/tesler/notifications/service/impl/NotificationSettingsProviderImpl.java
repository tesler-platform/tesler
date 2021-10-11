/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.notifications.service.impl;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.config.cache.CacheConfig;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.notifications.api.EventSettings;
import io.tesler.notifications.model.hbn.change.BaseNotificationSettingsProvider;
import io.tesler.notifications.service.CacheableNotificationSettingsProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class NotificationSettingsProviderImpl extends BaseNotificationSettingsProvider implements
		CacheableNotificationSettingsProvider {

	public NotificationSettingsProviderImpl(JpaDao jpaDao) {
		super(jpaDao);
	}

	@Override
	@Cacheable(cacheResolver = "teslerCacheResolver", cacheNames = CacheConfig.NOTIFICATION_SETTINGS)
	public EventSettings getGlobalSettings(LOV event) {
		return super.getGlobalSettings(event);
	}

	@Override
	@CacheEvict(cacheResolver = "teslerCacheResolver", cacheNames = CacheConfig.NOTIFICATION_SETTINGS)
	public void evict(LOV event) {

	}

	@Override
	@Cacheable(cacheResolver = "teslerCacheResolver", cacheNames = CacheConfig.NOTIFICATION_SETTINGS)
	public EventSettings getUserSettings(LOV event, Long userId) {
		return super.getUserSettings(event, userId);
	}

	@Override
	@CacheEvict(cacheResolver = "teslerCacheResolver", cacheNames = CacheConfig.NOTIFICATION_SETTINGS)
	public void evict(LOV event, Long userId) {

	}

}
