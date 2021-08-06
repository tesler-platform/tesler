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

package io.tesler.model.core.listeners.hbn.change.security;

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.security.GroupDirectGroupRelation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(value = "tesler.security.group-direct-group-relation-change-listener.enabled", havingValue = "true", matchIfMissing = true)
public class GroupDirectGroupRelationChangeListener extends AbstractGroupChangeListener<GroupDirectGroupRelation> {

	@Override
	public Class<? extends GroupDirectGroupRelation> getType() {
		return GroupDirectGroupRelation.class;
	}

	@Override
	public boolean canProcess(IChangeVector vector, LOV event) {
		if (!super.canProcess(vector, event)) {
			return false;
		}
		return vector.isDelete() || vector.isNew();
	}

	@Override
	public void process(IChangeVector vector, LOV event) {
		syncChildSuperGroups(vector.unwrap(GroupDirectGroupRelation.class).getRelated());
	}

}
