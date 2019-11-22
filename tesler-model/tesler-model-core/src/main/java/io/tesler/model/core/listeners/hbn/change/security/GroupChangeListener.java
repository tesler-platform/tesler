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
import io.tesler.model.core.entity.security.Group;
import io.tesler.model.core.entity.security.GroupAccessorRelation;
import io.tesler.model.core.entity.security.GroupAccessorRelation_;
import io.tesler.model.core.entity.security.GroupDirectGroupRelation;
import io.tesler.model.core.entity.security.GroupDirectGroupRelation_;
import io.tesler.model.core.entity.security.GroupRelation;
import io.tesler.model.core.entity.security.types.GroupRelationType;
import org.springframework.stereotype.Service;


@Service
public class GroupChangeListener extends AbstractGroupChangeListener<Group> {

	@Override
	public Class<? extends Group> getType() {
		return Group.class;
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
		Group group = vector.unwrap(Group.class);
		if (vector.isNew()) {
			// ссылка на саму себя, чтобы работала иерархия
			syncChildSuperGroups(
					GroupRelationType.SUPER_GROUP.toRelation(group.getId())
			);
		}
		if (vector.isDelete()) {
			jpaDao.getStream(
					GroupDirectGroupRelation.class,
					GroupRelation.class,
					(root, cb) -> root.get(GroupDirectGroupRelation_.related),
					(root, query, cb) -> cb.equal(root.get(GroupDirectGroupRelation_.group), group)
			).forEach(this::syncChildSuperGroups);

			jpaDao.delete(
					GroupAccessorRelation.class,
					(root, query, cb) -> cb.or(
							cb.equal(root.get(GroupAccessorRelation_.group), group),
							cb.equal(
									root.get(GroupAccessorRelation_.related),
									GroupRelationType.SUPER_GROUP.toRelation(group.getId())
							)
					)
			);
		}
	}

}
