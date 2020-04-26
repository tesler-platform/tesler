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

package io.tesler.model.core.listeners.jpa;

import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.service.BaseEntityListenerDelegate;
import java.io.Serializable;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DelegatingBaseEntityListener implements Serializable {

	private final BaseEntityListenerDelegate baseEntityListenerDelegate;

	@PostLoad
	public void onLoad(BaseEntity baseEntity) {
		baseEntityListenerDelegate.baseEntityOnLoad(baseEntity);
	}

	@PrePersist
	public void onCreate(BaseEntity baseEntity) {
		baseEntityListenerDelegate.baseEntityOnCreate(baseEntity);
	}

	@PreUpdate
	public void onUpdate(BaseEntity baseEntity) {
		baseEntityListenerDelegate.baseEntityOnUpdate(baseEntity);
	}

}
