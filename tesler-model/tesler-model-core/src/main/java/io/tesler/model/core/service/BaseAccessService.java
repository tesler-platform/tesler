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

import io.tesler.model.core.api.EffectiveUserAware;
import io.tesler.model.core.api.GroupService;
import io.tesler.model.core.api.security.AccessService;
import io.tesler.model.core.api.security.IAccessorSupplier;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.security.AccessList;
import io.tesler.model.core.entity.security.AccessRecord;
import io.tesler.model.core.entity.security.AccessRecord_;
import io.tesler.model.core.entity.security.Accessor;
import io.tesler.model.core.entity.security.Accessor_;
import io.tesler.model.core.entity.security.SecurableEntity;
import io.tesler.model.core.entity.security.SecurableEntity_;
import io.tesler.model.core.entity.security.types.AccessListType;
import io.tesler.model.core.entity.security.types.AccessorType;
import io.tesler.model.core.entity.security.types.Permission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service(AccessService.SERVICE_NAME)
public class BaseAccessService implements AccessService {

	private final GroupService groupService;

	private final JpaDao jpaDao;

	private final EffectiveUserAware<User> effectiveUserAware;

	protected Set<Long> getAllUserGroups() {
		return groupService.getUserAllGroups(getSessionUser());
	}

	protected User getSessionUser() {
		return effectiveUserAware.getEffectiveSessionUser();
	}

	protected int getMaxInlineUserGroups() {
		// todo: добавить конфигурацию
		return 100;
	}

	protected boolean isMACLEnabled() {
		// todo: добавить конфигурацию
		return true;
	}

	@Override
	public <T extends SecurableEntity> Specification<T> getSecuritySpecification(Permission permission) {
		return getSecuritySpecification(getSessionUser(), this::getAllUserGroups, permission);
	}

	@Override
	public <T extends SecurableEntity> Specification<T> getSecuritySpecification(User user, Permission permission) {
		return getSecuritySpecification(user, () -> groupService.getUserAllGroups(user), permission);
	}

	private <T extends SecurableEntity> Specification<T> getSecuritySpecification(
			User user,
			Supplier<Set<Long>> groups,
			Permission permission) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			// базовый запрос, проверяющий, что у пользователя есть права
			Subquery<Integer> general = cq.subquery(Integer.class);
			Root<AccessRecord> generalRoot = general.from(AccessRecord.class);
			general.select(cb.literal(1)).where(cb.and(
					cb.equal(root.get(SecurableEntity_.accessList), generalRoot.get(AccessRecord_.accessList)),
					cb.greaterThanOrEqualTo(generalRoot.get(AccessRecord_.permission), permission),
					getAccessorPredicate(user, groups, cq, cb, generalRoot)
			));
			predicates.add(cb.exists(general));

			// проверка мандатного доступа - отсуствуют обязательные записи, к которым не относится пользователь
			if (isMACLEnabled()) {
				Subquery<Integer> mandatory = cq.subquery(Integer.class);
				Root<AccessRecord> mandatoryRoot = mandatory.from(AccessRecord.class);
				mandatory.select(cb.literal(1)).where(cb.and(
						cb.equal(root.get(SecurableEntity_.accessList), mandatoryRoot.get(AccessRecord_.accessList)),
						cb.equal(mandatoryRoot.get(AccessRecord_.mandatory), cb.literal(true)),
						cb.not(getAccessorPredicate(user, groups, cq, cb, mandatoryRoot))
				));
				predicates.add(cb.not(cb.exists(mandatory)));
			}

			return cb.or(
					// пустой список доступа
					root.get(SecurableEntity_.accessList).isNull(),
					cb.and(predicates.toArray(new Predicate[0]))
			);
		};
	}

	private Predicate getAccessorPredicate(
			User user,
			Supplier<Set<Long>> groups,
			CriteriaQuery<?> cq,
			CriteriaBuilder cb,
			Path<AccessRecord> recordRoot) {
		Set<Long> allGroups = groups.get();
		// todo добавить конфигурацию
		if (allGroups.size() > getMaxInlineUserGroups()) {
			return getAccessorPredicate(
					user,
					groupService.getAllGroupsSubquery(user, cq, cb),
					cb,
					recordRoot
			);
		}
		return getAccessorPredicate(user, allGroups, cb, recordRoot);
	}


	private Predicate getAccessorPredicate(
			User user,
			Subquery<Long> groups,
			CriteriaBuilder cb,
			Path<AccessRecord> recordRoot) {
		return cb.or(
				cb.and(
						recordRoot.get(AccessRecord_.accessor).get(Accessor_.ACCESSOR_ID).in(groups),
						cb.equal(recordRoot.get(AccessRecord_.accessor).get(Accessor_.ACCESSOR_TYPE), AccessorType.GROUP)
				),
				cb.and(cb.equal(recordRoot.get(AccessRecord_.accessor), user.getAccessor()))
		);
	}

	private Predicate getAccessorPredicate(
			User user,
			Set<Long> groups,
			CriteriaBuilder cb,
			Path<AccessRecord> recordRoot) {
		List<Predicate> predicates = groups.stream()
				.map(AccessorType.GROUP::toAccessor)
				.map(accessor -> cb.equal(
						recordRoot.get(AccessRecord_.accessor), accessor
				)).collect(Collectors.toList());
		predicates.add(
				cb.equal(
						recordRoot.get(AccessRecord_.accessor),
						user.getAccessor()
				)
		);
		return cb.or(predicates.toArray(new Predicate[0]));
	}

	public Permission getPermission(SecurableEntity entity) {
		return Optional.ofNullable(entity).map(SecurableEntity::getAccessList)
				.map(this::getPermission).orElse(Permission.DELETE);
	}

	@Override
	public Permission getPermission(SecurableEntity entity, User user) {
		return Optional.ofNullable(entity).map(SecurableEntity::getAccessList)
				.map(accessList -> getPermission(accessList, user)).orElse(Permission.DELETE);
	}

	@Override
	public Permission getPermission(AccessList accessList) {
		return getPermission(accessList, getSessionUser(), getAllUserGroups());
	}

	@Override
	public Permission getPermission(AccessList accessList, User user) {
		return getPermission(accessList, user, groupService.getUserAllGroups(user));
	}

	private Permission getPermission(AccessList accessList, User user, Set<Long> groups) {
		Set<Long> mandatory = new HashSet<>();
		return jpaDao.getStream(AccessRecord.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(AccessRecord_.accessList), accessList)
		)).peek(accessRecord -> {
			if (isMACLEnabled() && accessRecord.isMandatory()) {
				mandatory.add(accessRecord.getAccessor().getAccessorId());
			}
		}).filter(accessRecord -> {
					Accessor accessor = accessRecord.getAccessor();
					switch (accessor.getAccessorType()) {
						case USER:
							return Objects.equals(accessor.getAccessorId(), user.getId());
						case GROUP:
							return groups.contains(accessor.getAccessorId());
						default:
							return false;
					}
				}
		).map(AccessRecord::getPermission).max(Comparator.comparing(Function.identity()))
				.filter(permission -> groups.containsAll(mandatory)).orElse(Permission.NONE);
	}

	@Override
	public AccessRecord getAccessRecord(AccessList accessList, IAccessorSupplier supplier) {
		return jpaDao.getStream(AccessRecord.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(AccessRecord_.accessList), accessList),
				cb.equal(root.get(AccessRecord_.accessor), supplier.getAccessor())
		)).findAny().orElse(null);
	}

	@Override
	public void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission) {
		grantPermission(entity, supplier, permission, null);
	}

	@Override
	public void grantPermission(SecurableEntity entity, IAccessorSupplier supplier, Permission permission,
			Boolean mandatory) {
		AccessList accessList = entity.getAccessList();
		if (accessList != null) {
			AccessRecord record = getAccessRecord(accessList, supplier);
			boolean needCopy = isChanging(record, permission, mandatory) && accessList.getType() != AccessListType.PRIVATE;
			if (needCopy) {
				accessList = copy(accessList, AccessListType.PRIVATE);
			}
			grantPermission(accessList, supplier, permission);
		}
	}

	private boolean isChanging(AccessRecord record, Permission permission, Boolean mandatory) {
		if (record == null) {
			return true;
		}
		if (record.getPermission() != permission) {
			return true;
		}
		return mandatory != null && !mandatory.equals(record.isMandatory());
	}

	@Override
	public void removeAccessor(SecurableEntity entity, IAccessorSupplier supplier) {
		AccessList accessList = entity.getAccessList();
		AccessRecord record = getAccessRecord(accessList, supplier);
		boolean needCopy = record != null && accessList.getType() != AccessListType.PRIVATE;
		if (needCopy) {
			accessList = copy(accessList, AccessListType.PRIVATE);
		}
		removeAccessor(accessList, supplier);
	}

	@Override
	public void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission) {
		grantPermission(entity, supplier, permission, null);
	}

	@Override
	public void grantPermission(AccessList entity, IAccessorSupplier supplier, Permission permission, Boolean mandatory) {
		AccessRecord accessRecord = getAccessRecord(entity, supplier);
		if (accessRecord == null) {
			accessRecord = new AccessRecord();
			accessRecord.setAccessList(entity);
			accessRecord.setAccessor(supplier.getAccessor());
			accessRecord.setPermission(permission);
			if (mandatory != null) {
				accessRecord.setMandatory(mandatory);
			}
			jpaDao.save(accessRecord);
		} else if (isChanging(accessRecord, permission, mandatory)) {
			accessRecord.setPermission(permission);
			if (mandatory != null) {
				accessRecord.setMandatory(mandatory);
			}
		}
	}

	@Override
	public void removeAccessor(AccessList entity, IAccessorSupplier supplier) {
		Optional.ofNullable(getAccessRecord(entity, supplier))
				.ifPresent(jpaDao::delete);
	}

	@Override
	public void assignAccessList(SecurableEntity entity, AccessList accessList) {
		entity.setAccessList(accessList);
	}

	@Override
	public AccessList copy(AccessList accessList, AccessListType targetType) {
		AccessList newList = new AccessList();
		newList.setType(targetType);
		jpaDao.save(newList);
		jpaDao.getStream(
				AccessRecord.class,
				(root, query, cb) -> cb.equal(
						root.get(AccessRecord_.accessList), accessList
				)
		).forEach(record -> jpaDao.save(copyRecordTo(record, newList)));
		return newList;
	}


	private AccessRecord copyRecordTo(AccessRecord existing, AccessList targetList) {
		AccessRecord record = new AccessRecord();
		record.setAccessor(existing.getAccessor());
		record.setPermission(existing.getPermission());
		record.setMandatory(existing.isMandatory());
		record.setAccessList(targetList);
		return record;
	}

}
