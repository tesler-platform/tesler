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

package io.tesler.acl.service;

import io.tesler.acl.entity.AclUser;
import io.tesler.acl.entity.SecurableEntity;
import io.tesler.acl.entity.types.Permission;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.api.EffectiveUserAware;
import io.tesler.model.core.dao.JpaDao;
import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service(AccessService.SERVICE_NAME)
public class AccessServiceImpl extends BaseAccessService {

	private final SessionService sessionService;

	private final AclSessionService aclSessionService;

	private final EffectiveUserAware effectiveUserAware;

	public AccessServiceImpl(GroupService groupService, JpaDao jpaDao, SessionService sessionService,
			AclSessionService aclSessionService, EffectiveUserAware effectiveUserAware) {
		super(groupService, jpaDao, effectiveUserAware);
		this.sessionService = sessionService;
		this.aclSessionService = aclSessionService;
		this.effectiveUserAware = effectiveUserAware;
	}

	@Override
	protected Set<Long> getAllUserGroups() {
		return aclSessionService.getAllUserGroups();
	}

	@Override
	protected AclUser getSessionUser() {
		return () -> sessionService.getSessionUser().getId();
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

