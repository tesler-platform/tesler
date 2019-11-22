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

package io.tesler.core.service.notification.impl;

import io.tesler.core.dao.impl.notifications.NotificationSpecifications;
import io.tesler.core.service.notification.IDeliveryService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.notifications.Notification;
import io.tesler.model.core.entity.notifications.Notification_;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractDeliveryService implements IDeliveryService {

	@Autowired
	protected JpaDao jpaDao;


	public List<Notification> queryNotifications() {
		return jpaDao.getList(Notification.class, NotificationSpecifications.notDelivered(getServiceId()));
	}

	public final void markDelivered(Notification notification) {
		jpaDao.update(Notification.class, (root, cq, cb) -> cb.equal(root.get(Notification_.id), notification.getId()),
				NotificationSpecifications.markDelivered(getServiceId(), true)
		);
	}

}
