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

package io.tesler.model.core.service;

import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TeslerBaseEntityListenerDelegate implements BaseEntityListenerDelegate {

	private final CurrentUserAware<User> currentUserAware;

	@Override
	public void baseEntityOnLoad(BaseEntity baseEntity) {
		baseEntity.setLoadVstamp(baseEntity.getVstamp());
	}

	@Override
	public void baseEntityOnCreate(BaseEntity baseEntity) {
		baseEntity.setCreatedDate(LocalDateTime.now());
		baseEntity.setUpdatedDate(LocalDateTime.now());
		Long currentUser = baseEntity.getCreatedBy();
		if (currentUser == null) {
			currentUser = currentUserAware.getCurrentUser().getId();
		}
		if (currentUser != null) {
			baseEntity.setCreatedBy(currentUser);
			baseEntity.setLastUpdBy(currentUser);
		}
	}

	@Override
	public void baseEntityOnUpdate(BaseEntity baseEntity) {
		Long currentUser = currentUserAware.getCurrentUser().getId();
		baseEntity.setUpdatedDate(LocalDateTime.now());
		if (currentUser != null) {
			baseEntity.setLastUpdBy(currentUser);
		}
	}

}
