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

package io.tesler.core.dao.impl.notifications;

import io.tesler.api.data.dao.UpdateSpecification;
import io.tesler.model.core.entity.notifications.Notification;
import io.tesler.model.core.entity.notifications.Notification_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;


@UtilityClass
public class NotificationSpecifications {

	public static Specification<Notification> forService(int serviceId) {
		return (root, query, cb) -> cb.equal(bitAnd(cb, root.get(Notification_.deliveryType), serviceId), serviceId);
	}


	public static Specification<Notification> notDelivered(int serviceId) {
		return Specifications.where(forService(serviceId))
				.and((root, cq, cb) -> cb.equal(bitAnd(cb, root.get(Notification_.deliveryStatus), serviceId), 0));
	}

	private static Expression<Integer> bitAnd(CriteriaBuilder cb, Path<Integer> path, Integer value) {
		return bitAnd(cb, Integer.class, path, value);
	}

	private static <T extends Number> Expression<T> bitAnd(CriteriaBuilder cb, Class<T> cls, Path<T> path, T value) {
		return cb.function("BITAND", cls, path, cb.literal(value));
	}

	public static UpdateSpecification<Notification> markDelivered(int serviceId, boolean status) {
		if (status) {
			// выставить бит в 1: bitor(field, serviceId) = field + serviceId - bitand(field , serviceId)
			return (update, root, cb) -> update.set(
					root.get(Notification_.deliveryStatus),
					cb.diff(
							cb.sum(root.get(Notification_.deliveryStatus), cb.literal(serviceId)),
							bitAnd(cb, root.get(Notification_.deliveryStatus), serviceId)
					)
			);
		} else {
			// выставить бит в 0
			return (update, root, cb) -> update.set(
					root.get(Notification_.deliveryStatus),
					bitAnd(cb, root.get(Notification_.deliveryStatus), Integer.MAX_VALUE ^ serviceId)
			);
		}
	}

}
