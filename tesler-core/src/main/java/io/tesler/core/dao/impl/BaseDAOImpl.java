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

package io.tesler.core.dao.impl;

import io.tesler.api.data.ResultPage;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.controller.param.SortParameters;
import io.tesler.core.dao.BaseDAO;
import io.tesler.core.dao.IPdqExtractor;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;
import io.tesler.model.core.dao.impl.JpaDaoImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional
public class BaseDAOImpl extends JpaDaoImpl implements BaseDAO {

	private final Optional<IPdqExtractor> pdqExtractor;

	private final List<ClassifyDataProvider> providers;

	private final Database primaryDatabase;

	public BaseDAOImpl(
			Set<EntityManager> entityManagers,
			TransactionService txService,
			Optional<IPdqExtractor> pdqExtractor,
			List<ClassifyDataProvider> providers,
			@Qualifier("primaryDatabase") Database primaryDatabase
	) {
		super(entityManagers, txService);
		this.pdqExtractor = pdqExtractor;
		this.providers = providers;
		this.primaryDatabase = primaryDatabase;
	}

	private Specification getPdqSearchSpec(final QueryParameters queryParameters) {
		return pdqExtractor.map(pdqExtractor -> pdqExtractor.extractPdq(queryParameters.getPdqName())).orElse(null);
	}

	@Override
	public <T, X> Long getCount(Class<T> clazz, Class dtoClazz, SingularAttribute<T, X> name, X value,
			QueryParameters queryParameters) {
		return getCount(clazz, dtoClazz, (root, cq, cb) -> cb.equal(root.get(name), value), queryParameters);
	}

	@Override
	public <T> ResultPage<T> getList(Class<T> entityClazz, Class dtoClazz,
			Specification<T> defaultSearchSpec, QueryParameters parameters) {
		return getList(entityClazz, dtoClazz, defaultSearchSpec, parameters, null);
	}

	@Override
	public <T> ResultPage<T> getList(CriteriaQuery<T> cq, Root<T> root, Class dtoClazz,
			Predicate defaultSearchSpec, QueryParameters parameters) {
		return getList(cq, root, dtoClazz, defaultSearchSpec, parameters, null);
	}

	public Predicate getPredicateFromSearchParams(
			CriteriaBuilder cb,
			Root<?> root,
			Class dtoClazz,
			FilterParameters searchParams
	) {
		return MetadataUtils.getPredicateFromSearchParams(cb, root, dtoClazz, searchParams, providers);
	}

	@Override
	public Long getCount(
			CriteriaQuery<Long> cq,
			Root<?> root,
			Class dtoClazz,
			Predicate defaultSearchSpec,
			QueryParameters queryParameters
	) {
		EntityManager entityManager = getSupportedEntityManager(root.getModel().getBindableJavaType().getName());
		queryParameters = emptyIfNull(queryParameters);
		FilterParameters searchParams = queryParameters.getFilter();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		if (defaultSearchSpec == null) {
			defaultSearchSpec = cb.and();
		}
		Specification pdqSearchSpec = getPdqSearchSpec(queryParameters);
		if (pdqSearchSpec == null) {
			pdqSearchSpec = (root1, cq1, cb1) -> cb1.and();
		}
		cq.select(cb.count(root));
		Predicate searchParamsPredicate = getPredicateFromSearchParams(cb, root, dtoClazz, searchParams);
		cq.where(cb.and(defaultSearchSpec, searchParamsPredicate, pdqSearchSpec.toPredicate(root, cq, cb)));
		return entityManager.createQuery(cq).getSingleResult();
	}

	@Override
	public <T> Long getCount(
			Class<T> entityClass,
			Class<?> dtoClazz,
			Specification<T> searchSpec,
			QueryParameters queryParameters
	) {
		EntityManager entityManager = getSupportedEntityManager(entityClass.getName());
		queryParameters = emptyIfNull(queryParameters);
		FilterParameters parameters = queryParameters.getFilter();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(entityClass);
		cq.select(cb.count(root));
		List<Predicate> predicates = new ArrayList<>();
		if (searchSpec != null) {
			predicates.add(searchSpec.toPredicate(root, cq, cb));
		}
		Specification pdqSearchSpec = getPdqSearchSpec(queryParameters);
		if (pdqSearchSpec != null) {
			predicates.add(pdqSearchSpec.toPredicate(root, cq, cb));
		}
		predicates.add(getPredicateFromSearchParams(cb, root, dtoClazz, parameters));
		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		return entityManager.createQuery(cq).getSingleResult();
	}

	@Override
	public <T> ResultPage<T> getList(
			CriteriaQuery<T> cq,
			Root<T> root,
			Class dtoClazz,
			Predicate defaultSearchSpec,
			QueryParameters parameters,
			EntityGraph<? super T> fetchGraph
	) {
		EntityManager entityManager = getSupportedEntityManager(root.getModel().getBindableJavaType().getName());
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		SortParameters sort = parameters.getSort();
		FilterParameters filter = parameters.getFilter();
		if (defaultSearchSpec == null) {
			defaultSearchSpec = cb.and();
		}
		Specification pdqSearchSpec = getPdqSearchSpec(parameters);
		if (pdqSearchSpec == null) {
			pdqSearchSpec = (root1, cq1, cb1) -> cb1.and();
		}
		int joinsInRoot = root.getJoins().size();
		Predicate searchParamsPredicate = getPredicateFromSearchParams(cb, root, dtoClazz, filter);

		// TODO: Narrow it down based on criteria
		boolean distinctRequired = root.getJoins().size() > joinsInRoot;
		/**
		 * Non-Oracle DB can handle distinct for CLOBs so it can be applied
		 * as sql clause.
		 *
		 * @see https://hibernate.atlassian.net/browse/HHH-3606
		 */
		if (!this.primaryDatabase.equals(Database.ORACLE) && distinctRequired) {
			cq.distinct(true);
		}

		if (cq.getRestriction() != null) {
			cq.where(cb.and(
					cq.getRestriction(),
					defaultSearchSpec,
					searchParamsPredicate,
					pdqSearchSpec.toPredicate(root, cq, cb)
			));
		} else {
			cq.where(cb.and(
					defaultSearchSpec,
					searchParamsPredicate,
					pdqSearchSpec.toPredicate(root, cq, cb)
			));
		}
		MetadataUtils.addSorting(dtoClazz, root, cq, cb, sort);

		// query.setHint("javax.persistence.fetchgraph", fetchGraph)
		// more correct but causes performance troubles
		applyGraph(root, fetchGraph);

		Query<T> query = entityManager.unwrap(Session.class).getSession().createQuery(cq);
		applyPaging(query, parameters.getPage());

		/**
		 * Joins from filters (e.g. multivalue fields) can cause duplications in result set,
		 * which are handled by applying `distinct` in memory.
		 *
		 * Deprecated `setResultTransformer` usage due to DB-level `distinct` clause is not applicable
		 * because it does not support CLOBs and criteria query's `distinct()` can't properly handle
		 * `QueryHints.HINT_PASS_DISTINCT_THROUGH` in nested (i.e. paginated) requests.
		 *
		 * Downside is that when distinct actually does filters out duplicates the number of records
		 * in result set will be less than required by pagination parameters.
		 *
		 * @see https://hibernate.atlassian.net/browse/HHH-11726
		 * @see https://discourse.hibernate.org/t/hibernate-resulttransformer-is-deprecated-what-to-use-instead/232
		 *
		 */
		if (this.primaryDatabase.equals(Database.ORACLE) && distinctRequired) {
			query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		}

		return ResultPage.of(query.getResultList(), parameters.getPage());
	}

	@Override
	public <T> ResultPage<T> getList(
			Class<T> entityClazz,
			Class dtoClazz,
			Specification<T> defaultSearchSpec,
			QueryParameters parameters,
			EntityGraph<? super T> fetchGraph
	) {
		EntityManager entityManager = getSupportedEntityManager(entityClazz.getName());
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClazz);
		Root<T> root = cq.from(entityClazz);
		Predicate predicate = defaultSearchSpec.toPredicate(root, cq, cb);
		return getList(cq, root, dtoClazz, predicate, parameters, fetchGraph);
	}


	private QueryParameters emptyIfNull(QueryParameters queryParameters) {
		if (queryParameters == null) {
			return QueryParameters.emptyQueryParameters();
		}
		return queryParameters;
	}

}
