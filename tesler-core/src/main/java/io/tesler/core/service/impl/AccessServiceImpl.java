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

package io.tesler.core.service.impl;

import io.tesler.core.config.CacheConfig;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.api.GroupService;
import io.tesler.model.core.api.security.AccessService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.security.SecurableEntity;
import io.tesler.model.core.entity.security.types.Permission;
import io.tesler.model.core.service.BaseAccessService;
import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service(AccessService.SERVICE_NAME)
public class AccessServiceImpl extends BaseAccessService {

	private final SessionService sessionService;

	public AccessServiceImpl(GroupService groupService, JpaDao jpaDao, SessionService sessionService) {
		super(groupService, jpaDao, sessionService);
		this.sessionService = sessionService;
	}

	@Override
	protected Set<Long> getAllUserGroups() {
		return sessionService.getAllUserGroups();
	}

	@Override
	protected User getSessionUser() {
		return sessionService.getSessionUser();
	}

	@Override
	@Cacheable(
			cacheNames = CacheConfig.REQUEST_CACHE,
			key = "{#root.targetClass, #root.methodName, #entity.id}"
	)
	public Permission getPermission(SecurableEntity entity) {
		return super.getPermission(entity);
	}


}
