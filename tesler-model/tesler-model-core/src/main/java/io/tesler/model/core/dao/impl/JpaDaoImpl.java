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

package io.tesler.model.core.dao.impl;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dao.Selector;
import io.tesler.api.data.dao.UpdateSpecification;
import io.tesler.api.exception.ServerException;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.model.core.api.EmbeddedKeyable;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.dao.util.JpaUtils;
import io.tesler.model.core.entity.AbstractEntity;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.BaseEntity_;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Primary
@Transactional
@Repository
public class JpaDaoImpl implements JpaDao {

	@PersistenceContext
	protected EntityManager entityManager;

	@Lazy
	@Autowired
	protected TransactionService txService;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> T findById(String type, Long id) {
		EntityType<BaseEntity> entityType = getEntityType(type);
		if (entityType == null) {
			throw new IllegalArgumentException(type);
		}
		return (T) findById(entityType.getJavaType(), id);
	}

	@Override
	public <T extends BaseEntity> T findById(Class<T> clazz, Long id) {
		return entityManager.unwrap(Session.class).get(clazz, id);
	}

	@Override
	public <T> EntityGraph<? super T> getEntityGraph(Class<T> clazz, String name) {
		return entityManager.getEntityGraphs(clazz).stream()
				.filter(graph -> name.equals(graph.getName()))
				.findFirst().orElse(null);
	}

	@Override
	public <T> void applyGraph(Class<T> clazz, Root<T> root, String fetchGraph) {
		applyGraph(root, getEntityGraph(clazz, fetchGraph));
	}

	@Override
	public <T, X> List<T> getList(Class<T> clazz, SingularAttribute<T, X> name, X value) {
		return getList(clazz, (root, cq, cb) -> cb.equal(root.get(name), value));
	}

	@Override
	public <T> void applyGraph(Root<T> root, EntityGraph<? super T> fetchGraph) {
		if (fetchGraph == null) {
			return;
		}
		applyFetches(root, fetchGraph.getAttributeNodes());
	}

	private void applyFetches(FetchParent fetch, List<AttributeNode<?>> nodes) {
		for (AttributeNode<?> node : nodes) {
			String attributeName = node.getAttributeName();
			FetchParent child = fetch.fetch(attributeName, getJoinType(fetch, attributeName));
			for (Subgraph<?> subgraph : node.getSubgraphs().values()) {
				applyFetches(child, subgraph.getAttributeNodes());
			}
		}
	}

	protected JoinType getJoinType(FetchParent fetch, String attrName) {
		// todo: похоже так всегда и бывает
		if (fetch instanceof From) {
			return getJoinType((From) fetch, attrName);
		}
		return JoinType.LEFT;
	}

	protected JoinType getJoinType(From from, String attrName) {
		JoinType joinType = JoinType.INNER;
		Bindable model = from.getModel();
		if (model.getBindableType() == BindableType.ENTITY_TYPE) {
			ManagedType managedType = (ManagedType) model;
			if (managedType.getAttribute(attrName).isAssociation()) {
				joinType = JoinType.LEFT;
			}
		}
		return joinType;
	}

	@Override
	public <T> List<T> getListByIds(Class<T> clazz, List<Long> ids) {
		return getList(clazz, (root, query, cb) -> cb.or(
				ids.stream()
						.map(id -> cb.equal(root.get("id"), id))
						.toArray(Predicate[]::new)
		));
	}

	@Override
	public Long getCount(Class<?> clazz) {
		return getCount(clazz, (root, cq, cb) -> cb.and());
	}

	@Override
	public <T> Long getCount(Class<T> entityClass, Specification<T> specification) {
		return getSingleResult(entityClass, Long.class, (root, cb) -> cb.count(root), specification);
	}

	@Override
	public void flush() {
		if (txService.isActive()) {
			entityManager.unwrap(Session.class).flush();
		}
	}

	@Override
	public void refresh(AbstractEntity o) {
		if (entityManager.contains(o)) {
			entityManager.unwrap(Session.class).refresh(o);
		}
	}

	@Override
	public <T> T save(Object entity) {
		return (T) entityManager.unwrap(Session.class).save(entity);
	}

	@Override
	public <T extends BaseEntity> T evict(T o) {
		entityManager.unwrap(Session.class).evict(o);
		return o;
	}

	@Override
	public void delete(AbstractEntity o) {
		entityManager.unwrap(Session.class).delete(entityManager.merge(o));
	}

	@Override
	public <T extends BaseEntity> T delete(Class<T> clazz, Long id) {
		T o = findById(clazz, id);
		if (o != null) {
			entityManager.unwrap(Session.class).delete(o);
		} else {
			throw new EntityNotFoundException();
		}
		return o;
	}

	@Override
	public <T> List<T> getList(Class<T> entityClass) {
		return getList(entityClass, null);
	}

	@Override
	public <T> int delete(Class<T> entityClass, Specification<T> spec) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
		Root<T> root = delete.from(entityClass);
		if (spec != null) {
			delete.where(spec.toPredicate(root, cb.createQuery(), cb));
		}
		return entityManager.createQuery(delete).executeUpdate();
	}

	@Override
	public <T> int update(Class<T> entityClass, Specification<T> spec, UpdateSpecification<T> updateSpec) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaUpdate<T> update = cb.createCriteriaUpdate(entityClass);
		Root<T> root = update.from(entityClass);
		if (spec != null) {
			update.where(spec.toPredicate(root, cb.createQuery(), cb));
		}
		updateSpec.apply(update, root, cb);
		return entityManager.createQuery(update).executeUpdate();
	}

	@Override
	public void saveWithCompositeKey(EmbeddedKeyable o) {
		entityManager.unwrap(Session.class).save(o);
	}

	@Override
	public void deleteWithCompositeKey(EmbeddedKeyable o) {
		entityManager.unwrap(Session.class).delete(o);
	}

	@Override
	public <T> List<Long> getIds(Class<T> entityClazz, Specification<T> searchSpec) {
		return getList(entityClazz, Long.class, (root, cb) -> root.get("id"), searchSpec);
	}

	@Override
	public void clear() {
		entityManager.unwrap(Session.class).clear();
	}

	@Override
	public void lock(AbstractEntity entity, LockModeType lockMode, int timeout) {
		if (lockMode != LockModeType.PESSIMISTIC_READ && lockMode != LockModeType.PESSIMISTIC_WRITE) {
			throw new ServerException("Only pessimistic lock modes are supported");
		}
		if (!canLock(entity)) {
			return;
		}
		Map<String, Object> options = new HashMap<>();
		options.put(AvailableSettings.LOCK_TIMEOUT, timeout);
		entityManager.lock(entity, lockMode, options);
	}

	@Override
	public void lockAndRefresh(AbstractEntity entity, int timeout) {
		if (!canLock(entity)) {
			return;
		}
		Map<String, Object> options = new HashMap<>();
		options.put(AvailableSettings.LOCK_TIMEOUT, timeout);
		entityManager.lock(entity, LockModeType.PESSIMISTIC_READ, options);
		entityManager.refresh(entity);
	}

	@Override
	public void revert(AbstractEntity entity) {
		if (canLock(entity)) {
			refresh(entity);
		}
	}


	private boolean canLock(AbstractEntity entity) {
		return !entity.isNew();
	}

	@Override
	public <T> List<T> selectNativeQuery(Class<T> entityClazz, String sql, Map<String, Object> params) {
		final javax.persistence.Query query = entityManager.createNativeQuery(sql, entityClazz);
		for (final Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	@Override
	public <T> T woAutoFlush(Invoker<T, RuntimeException> invoker) {
		Session session = entityManager.unwrap(Session.class);
		FlushMode flushMode = session.getHibernateFlushMode();
		try {
			session.setHibernateFlushMode(FlushMode.MANUAL);
			return invoker.invoke();
		} finally {
			session.setHibernateFlushMode(flushMode);
		}
	}

	@Override
	public <T> List<T> getList(Class<T> entityClass, Specification<T> specification) {
		return getPage(entityClass, specification, null).getResult();
	}

	@Override
	public <R, T> List<T> getList(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		return getPage(rootClass, targetClass, selector, specification, null).getResult();
	}

	@Override
	public <T> ResultPage<T> getPage(Class<T> entityClass, Specification<T> specification, PageSpecification page) {
		return getPage(entityClass, entityClass, Selector.identity(), specification, page);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public <T> Stream<T> getStream(Class<T> entityClass, Specification<T> specification) {
		return getStream(entityClass, entityClass, Selector.identity(), specification);
	}

	@Override
	public <T> T getSingleResult(Class<T> entityClass, Specification<T> specification) {
		return getSingleResult(entityClass, entityClass, Selector.identity(), specification);
	}

	@Override
	public <T extends BaseEntity> T fetchBySpecification(Class<T> entityClass, Specification<T> specification) {
		Long id = getSingleResult(
				entityClass, Long.class, (root, cb) -> root.get(BaseEntity_.id), specification
		);
		return findById(entityClass, id);
	}

	@Override
	public <T> T getSingleResultOrNull(Class<T> entityClass, Specification<T> specification) {
		return getSingleResultOrNull(entityClass, entityClass, Selector.identity(), specification);
	}

	@Override
	public <T> T getFirstResultOrNull(Class<T> entityClass, Specification<T> specification) {
		return getFirstResultOrNull(entityClass, entityClass, Selector.identity(), specification);
	}

	@Override
	public <T> boolean exists(Class<T> entityClass, Specification<T> specification) {
		// todo
		return getCount(entityClass, specification) > 0;
	}

	protected <T> TypedQuery<T> applyPaging(TypedQuery<T> query, PageSpecification page) {
		if (PageSpecification.isValid(page)) {
			return query.setFirstResult(page.getFrom())
					.setMaxResults(page.getPageSize() + 1);
		}
		return query;
	}

	@Override
	public <R, T> ResultPage<T> getPage(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification, PageSpecification page) {
		TypedQuery<T> query = getTypedQuery(rootClass, targetClass, selector, specification);
		List<T> result = applyPaging(query, page).getResultList();
		return ResultPage.of(result, page);
	}


	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public <R, T> Stream<T> getStream(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		return asStream(getTypedQuery(rootClass, targetClass, selector, specification));
	}

	@Override
	public <R, T> T getSingleResult(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		return JpaUtils.getSingleResult(getTypedQuery(rootClass, targetClass, selector, specification));
	}

	@Override
	public <R, T> T getSingleResultOrNull(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		return JpaUtils.getSingleResultOrNull(getTypedQuery(rootClass, targetClass, selector, specification));
	}

	@Override
	public <R, T> T getFirstResultOrNull(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		return JpaUtils.getFirstResultOrNull(
				getTypedQuery(rootClass, targetClass, selector, specification).setMaxResults(1)
		);
	}

	protected <R, T> TypedQuery<T> getTypedQuery(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(targetClass);
		Root<R> root = query.from(rootClass);
		query.select(selector.select(root, cb));
		if (specification != null) {
			query.where(specification.toPredicate(root, query, cb));
		}
		return entityManager.createQuery(query);
	}

	protected <T> Stream<T> asStream(TypedQuery<T> query) {
		Query hibernateQuery = query.unwrap(Query.class);
		ScrollableResults results = hibernateQuery.scroll(ScrollMode.FORWARD_ONLY);
		ScrollableResultsIterator<T> iterator = new ScrollableResultsIterator<>(results);
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL);
		Stream<T> stream = StreamSupport.stream(spliterator, false);
		return stream.onClose(iterator::close);
	}

	@SuppressWarnings("unchecked")
	protected EntityType<BaseEntity> getEntityType(String name) {
		return entityManager.getMetamodel().getEntities()
				.stream().filter(type ->
						StringUtils.equalsIgnoreCase(type.getName(), name)
								&& BaseEntity.class.isAssignableFrom(type.getJavaType())
				)
				.map(EntityType.class::cast)
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public <T> EntityType<T> getEntityType(Class<T> cls) {
		return entityManager.getMetamodel().entity(cls);
	}

}
