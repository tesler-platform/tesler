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

package io.tesler.acl.service;

import io.tesler.acl.entity.AclUser;
import io.tesler.acl.entity.Group;
import io.tesler.acl.entity.GroupDirectGroupRelation;
import io.tesler.acl.entity.GroupDirectGroupRelation_;
import io.tesler.acl.entity.GroupRelation_;
import io.tesler.acl.entity.GroupSuperGroupRelation;
import io.tesler.acl.entity.GroupSuperGroupRelation_;
import io.tesler.acl.entity.GroupUserRelation;
import io.tesler.acl.entity.GroupUserRelation_;
import io.tesler.acl.entity.Group_;
import io.tesler.acl.entity.types.GroupRelationType;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupService {

	private final JpaDao jpaDao;

	@Override
	public Set<Long> getDirectGroupMembers(Group group) {
		return jpaDao.getStream(
				GroupUserRelation.class,
				Long.class,
				(root, cb) -> root.get(GroupUserRelation_.related).get(GroupRelation_.relatedId),
				(root, query, cb) -> cb.equal(
						root.get(GroupUserRelation_.group),
						group
				)
		).collect(Collectors.toSet());
	}

	@Override
	public Set<Long> getAllGroupMembers(Group group) {
		return jpaDao.getStream(
				GroupUserRelation.class,
				Long.class,
				(root, cb) -> root.get(GroupUserRelation_.related).get(GroupRelation_.relatedId),
				(root, query, cb) -> {
					Subquery<Long> subq = query.subquery(Long.class);
					Root<GroupSuperGroupRelation> subqRoot = subq.from(GroupSuperGroupRelation.class);
					subq.select(subqRoot.get(GroupSuperGroupRelation_.group).get(Group_.id));
					subq.where(cb.equal(
							subqRoot.get(GroupSuperGroupRelation_.related),
							GroupRelationType.SUPER_GROUP.toRelation(group.getId())
					));
					return root.get(GroupUserRelation_.group).get(Group_.id).in(subq);
				}
		).collect(Collectors.toSet());
	}

	@Override
	public Subquery<Long> getDirectGroupsSubquery(AclUser user, CriteriaQuery<?> cq, CriteriaBuilder cb) {
		Subquery<Long> subq = cq.subquery(Long.class);
		Root<GroupUserRelation> subqRoot = subq.from(GroupUserRelation.class);
		subq.select(subqRoot.get(GroupUserRelation_.group).get(Group_.id));
		return subq.where(cb.equal(
				subqRoot.get(GroupUserRelation_.related),
				GroupRelationType.MEMBER_USER.toRelation(user.getId())
		));
	}

	@Override
	public Subquery<Long> getAllGroupsSubquery(AclUser user, CriteriaQuery<?> cq, CriteriaBuilder cb) {
		Subquery<Long> subq = cq.subquery(Long.class);
		Root<GroupSuperGroupRelation> subqRoot = subq.from(GroupSuperGroupRelation.class);
		subq.select(subqRoot.get(GroupSuperGroupRelation_.related).get(GroupRelation_.relatedId));
		return subq.where(subqRoot.get(GroupSuperGroupRelation_.group).get(Group_.id).in(
				getDirectGroupsSubquery(user, cq, cb)
		));
	}

	@Override
	public Set<Long> getUserDirectGroups(User user) {
		return jpaDao.getStream(
				GroupUserRelation.class,
				Long.class,
				(root, cb) -> root.get(GroupUserRelation_.group).get(Group_.id),
				(root, query, cb) -> cb.equal(
						root.get(GroupUserRelation_.related),
						GroupRelationType.MEMBER_USER.toRelation(user.getId())
				)
		).collect(Collectors.toSet());
	}

	@Override
	public Set<Long> getUserAllGroups(AclUser user) {
		return jpaDao.getStream(
				GroupSuperGroupRelation.class,
				Long.class,
				(root, cb) -> root.get(GroupSuperGroupRelation_.related).get(GroupRelation_.relatedId),
				(root, query, cb) -> {
					// все родительские группы
					return root.get(GroupSuperGroupRelation_.group).get(Group_.id).in(
							getDirectGroupsSubquery(user, query, cb)
					);
				}
		).collect(Collectors.toSet());
	}

	@Override
	public void addUserToGroup(Group parent, User user) {
		if (!isUserInGroup(parent, user)) {
			jpaDao.lock(parent, LockModeType.PESSIMISTIC_WRITE, -1);
			if (!isUserInGroup(parent, user)) {
				GroupUserRelation relation = new GroupUserRelation();
				relation.setGroup(parent);
				relation.setRelated(
						GroupRelationType.MEMBER_USER.toRelation(user.getId())
				);
				jpaDao.save(relation);
			}
		}
	}

	@Override
	public boolean isUserInGroup(Group parent, User user) {
		return jpaDao.exists(GroupUserRelation.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(GroupUserRelation_.group), parent),
				cb.equal(
						root.get(GroupUserRelation_.related),
						GroupRelationType.MEMBER_USER.toRelation(user.getId())
				)
		));
	}

	@Override
	public void removeUserFromGroup(Group parent, User user) {
		if (isUserInGroup(parent, user)) {
			jpaDao.lock(parent, LockModeType.PESSIMISTIC_WRITE, -1);
			if (isUserInGroup(parent, user)) {
				jpaDao.getStream(GroupUserRelation.class, (root, query, cb) -> cb.and(
						cb.equal(
								root.get(GroupUserRelation_.related),
								GroupRelationType.MEMBER_USER.toRelation(user.getId())
						),
						cb.equal(root.get(GroupUserRelation_.group), parent)
				)).forEach(jpaDao::delete);
			}
		}
	}

	@Override
	public void addGroupToGroup(Group parent, Group group) {
		if (!isGroupInGroup(parent, group)) {
			jpaDao.lock(parent, LockModeType.PESSIMISTIC_WRITE, -1);
			if (!isGroupInGroup(parent, group)) {
				GroupDirectGroupRelation relation = new GroupDirectGroupRelation();
				relation.setGroup(parent);
				relation.setRelated(
						GroupRelationType.MEMBER_GROUP.toRelation(group.getId())
				);
				jpaDao.save(relation);
			}
		}
	}

	@Override
	public void removeGroupFromGroup(Group parent, Group group) {
		if (isGroupInGroup(parent, group)) {
			jpaDao.lock(parent, LockModeType.PESSIMISTIC_WRITE, -1);
			if (isGroupInGroup(parent, group)) {
				jpaDao.getStream(GroupDirectGroupRelation.class, (root, query, cb) -> cb.and(
						cb.equal(root.get(GroupDirectGroupRelation_.group), parent),
						cb.equal(
								root.get(GroupDirectGroupRelation_.related),
								GroupRelationType.MEMBER_GROUP.toRelation(group.getId())
						)
				)).forEach(jpaDao::delete);
			}
		}
	}

	@Override
	public boolean isGroupInGroup(Group parent, Group group) {
		return jpaDao.exists(GroupDirectGroupRelation.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(GroupDirectGroupRelation_.group), parent),
				cb.equal(
						root.get(GroupDirectGroupRelation_.related),
						GroupRelationType.MEMBER_GROUP.toRelation(group.getId())
				)
		));
	}

}
