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

package io.tesler.notifications.dao.impl;

import com.google.common.collect.Lists;
import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.api.util.Collections;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.notifications.dao.NotificationDAO;
import io.tesler.notifications.model.entity.Notification;
import io.tesler.notifications.model.entity.Notification_;
import io.tesler.notifications.service.NotificationDeferredResult;
import io.tesler.notifications.service.impl.PushDeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Repository
@Transactional
public class NotificationDAOImpl implements NotificationDAO {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private PushDeliveryService pushDeliveryService;

	@Override
	public ResultPage<Notification> getNotifications(Long recipientId, boolean unread, Long offset,
			PageSpecification page) {
		return jpaDao.getPage(
				Notification.class,
				getNotificationSpecification(recipientId, unread, offset, true),
				page.isProvided() ? page : null
		);
	}

	@Override
	public long countNotifications(Long recipientId, boolean unread, Long offset) {
		return jpaDao.getCount(
				Notification.class,
				getNotificationSpecification(recipientId, unread, offset, false)
		);
	}

	private Specification<Notification> getNotificationSpecification(Long recipientId, boolean unread, Long offset,
			boolean order) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get(Notification_.recipientId), recipientId));
			predicates.add(cb.equal(root.get(Notification_.pushBit), 1));
			if (unread) {
				predicates.add(
						cb.equal(root.get(Notification_.readBit), 0)
				);
			}
			if (offset != null) {
				predicates.add(
						cb.lessThan(root.get(Notification_.id), offset)
				);
			}
			if (order) {
				cq.orderBy(cb.desc(root.get(Notification_.createdDate)));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	@Override
	public Map<Long, List<Notification>> checkNewNotifications(List<NotificationDeferredResult> recipients) {
		List<Notification> notifications = new ArrayList<>();
		for (List<NotificationDeferredResult> chunk : Lists.partition(recipients, 500)) {
			notifications.addAll(jpaDao.getList(
					Notification.class, (root, cq, cb) -> cb.and(
							cb.equal(root.get(Notification_.pushBit), 1),
							cb.or(chunk.stream().map(e -> {
										List<Predicate> predicates = new ArrayList<>();
										predicates.add(cb.equal(root.get(Notification_.recipientId), e.getRecipientId()));
										if (e.getLatestNotificationId() > 0) {
											predicates.add(cb.greaterThan(root.get(Notification_.id), e.getLatestNotificationId()));
										}
										return cb.and(predicates.toArray(new Predicate[0]));
									}
							).toArray(Predicate[]::new))
					)
			));
		}
		return notifications.stream().collect(Collectors.groupingBy(
				Notification::getRecipientId,
				Collections.toSortedList(Comparator.comparing(Notification::getId).reversed())
		));
	}

	@Override
	public void markNotificationsAsRead(List<Long> notificationId, Boolean mark, Long recipientId) {
		for (List<Long> chunk : Lists.partition(notificationId, 500)) {
			jpaDao.update(
					Notification.class, (root, cq, cb) -> cb.and(
							root.get(Notification_.id).in(chunk),
							cb.equal(root.get(Notification_.recipientId), recipientId)
					),
					NotificationSpecifications.markDelivered(pushDeliveryService.getServiceId(), mark)
			);
		}
	}

	@Override
	public void markDelivered(Notification notification, int serviceId) {
		jpaDao.update(Notification.class, (root, cq, cb) -> cb.equal(root.get(Notification_.id), notification.getId()),
				NotificationSpecifications.markDelivered(serviceId, true)
		);
	}


	@Override
	public void deleteNotifications(List<Long> notificationId, Long recipientId) {
		for (List<Long> chunk : Lists.partition(notificationId, 500)) {
			jpaDao.delete(Notification.class, (root, cq, cb) -> cb.and(
					root.get(Notification_.id).in(chunk),
					cb.equal(root.get(Notification_.recipientId), recipientId)
			));
		}
	}

	@Override
	public Long saveNotification(String url, String message, Long recipientId) {
		Notification notification = Notification.builder().uiMessage(message)
				.deliveryType(pushDeliveryService.getServiceId())
				.url(url).recipientId(recipientId).build();
		return jpaDao.save(notification);
	}

}
