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

import io.tesler.api.exception.ServerException;
import io.tesler.notifications.service.IDeliveryService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Getter
@Component
public class DeliveryServiceRegistry {

	@Autowired
	private List<IDeliveryService> serviceList;

	@PostConstruct
	protected void init() {
		Set<Integer> seen = new HashSet<>();
		for (IDeliveryService service : serviceList) {
			int serviceId = service.getServiceId();
			boolean valid = (serviceId & (serviceId - 1)) == 0;
			if (!valid || !seen.add(serviceId)) {
				throw new ServerException(String.format("service %s is invalid", service.getClass()));
			}
		}
	}

}
