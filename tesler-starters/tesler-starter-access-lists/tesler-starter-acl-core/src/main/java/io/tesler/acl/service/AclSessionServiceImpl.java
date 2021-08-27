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


import io.tesler.core.config.CacheConfig;
import io.tesler.core.util.session.SessionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Slf4j
@Service("aclSessionService")
@RequiredArgsConstructor
public class AclSessionServiceImpl implements AclSessionService {

	private final GroupService groupService;

	private final SessionService sessionService;

	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "#root.methodName")
	@Override
	public Set<Long> getAllUserGroups() {
		return groupService.getUserAllGroups(() -> sessionService.getSessionUser().getId());
	}

}
