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

import io.tesler.api.data.dao.databaselistener.IChangeListener;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.security.Group;
import io.tesler.model.core.entity.security.GroupDirectGroupRelation;
import io.tesler.model.core.entity.security.GroupDirectGroupRelation_;
import io.tesler.model.core.entity.security.GroupRelation;
import io.tesler.model.core.entity.security.GroupRelation_;
import io.tesler.model.core.entity.security.GroupSuperGroupRelation;
import io.tesler.model.core.entity.security.GroupSuperGroupRelation_;
import io.tesler.model.core.entity.security.Group_;
import io.tesler.model.core.entity.security.types.GroupRelationType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractGroupChangeListener<T> implements IChangeListener<T> {

	@Autowired
	protected JpaDao jpaDao;

	protected void syncChildSuperGroups(GroupRelation childRelation) {
		// ориентируемся только на прямые связи
		Group child = jpaDao.findById(Group.class, childRelation.getRelatedId());
		Set<Long> actual = collectParentGroups(
				Collections.singletonList(childRelation.getRelatedId()),
				new HashSet<>()
		);
		Set<Long> existing = jpaDao.getStream(
				GroupSuperGroupRelation.class,
				Long.class,
				(root, cb) -> root.get(GroupSuperGroupRelation_.related).get(GroupRelation_.relatedId),
				(root, query, cb) -> cb.equal(
						root.get(GroupSuperGroupRelation_.group),
						child
				)
		).collect(Collectors.toSet());
		// создаем нехватающие
		actual.stream().filter(id -> !existing.contains(id)).forEach(id -> {
					GroupSuperGroupRelation relation = new GroupSuperGroupRelation();
					relation.setGroup(child);
					relation.setRelated(GroupRelationType.SUPER_GROUP.toRelation(id));
					jpaDao.save(relation);
				}
		);
		// записываем изменения чтобы они были видны
		// в запросах - лиснер работает без autoflush
		jpaDao.flush();
		// удаляем лишние
		jpaDao.delete(GroupSuperGroupRelation.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(GroupSuperGroupRelation_.group), child),
				cb.or(
						existing.stream().filter(id -> !actual.contains(id)).map(id -> cb.equal(
								root.get(GroupSuperGroupRelation_.related),
								GroupRelationType.SUPER_GROUP.toRelation(id)
						)).toArray(Predicate[]::new)
				)
		));
	}

	private Set<Long> collectParentGroups(Collection<Long> groupIds, Set<Long> parents) {
		if (groupIds == null || groupIds.isEmpty()) {
			return parents;
		}
		// считаем что группа входит сама в себя
		parents.addAll(groupIds);
		// вызываем getList() чтобы закрывался курсор БД
		Set<Long> found = jpaDao.getStream(
				GroupDirectGroupRelation.class, Long.class,
				(root, cb) -> root.get(GroupDirectGroupRelation_.group).get(Group_.id),
				(root, query, cb) -> cb.or(
						groupIds.stream().map(id -> cb.equal(
								root.get(GroupDirectGroupRelation_.related),
								GroupRelationType.MEMBER_GROUP.toRelation(id)
						)).toArray(Predicate[]::new)
				)
		).collect(Collectors.toSet());
		// удаляем уже известные
		found.removeAll(parents);
		return collectParentGroups(found, parents);
	}

}
