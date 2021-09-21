/*-
 * #%L
 * IO Tesler - Testing
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

package io.tesler.testing.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.tesler.api.data.ResultPage;
import io.tesler.core.dao.BaseDAO;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.testing.conf.PersistenceTestContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.type.Type;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

@ContextHierarchy(
		@ContextConfiguration(
				name = "root",
				classes = {
						PersistenceTestContext.class
				}
		)
)
public abstract class BaseDAOAwareTest extends AbstractTeslerTest {

	protected final AtomicLong idSequence = new AtomicLong(Long.MIN_VALUE);

	protected List<EntityManager> entityManagers;

	@Autowired
	protected BaseDAO baseDAO;

	protected Map<EntityKey, Object> entities = new HashMap<>();

	protected Map<QueryKey, List<?>> queries = new HashMap<>();

	@AfterEach
	protected void tearDown() {
		queries.clear();
		entities.clear();
	}

	@AfterAll
	protected void tearDownALL() {
		reset(baseDAO);
	}

	@BeforeAll
	protected void tearUpALL() {
		when(baseDAO.save(any(BaseEntity.class))).thenAnswer(
				(Answer<Long>) invocation -> {
					BaseEntity entity = invocation.getArgument(0);
					if (entity.getId() == null) {
						Long id = idSequence.incrementAndGet();
						entity.setId(id);
					}
					entities.put(createEntityKey(entity), entity);
					return entity.getId();
				}
		);
		when(baseDAO.findById(any(Class.class), anyLong())).thenAnswer(
				(Answer<?>) invocation -> {
					Class<?> cls = invocation.getArgument(0);
					Long id = invocation.getArgument(1);
					return entities.get(createEntityKey(cls, id));
				}
		);
		when(baseDAO.getEntityGraph(any(), any())).thenReturn(null);
		when(baseDAO.getList(any(), any(), any(Specification.class), any(), any()))
				.thenAnswer(
						(Answer<?>) invocation -> {
							Class<?> cls = invocation.getArgument(0);
							Specification specification = invocation.getArgument(2);
							return ResultPage.of(
									queries.getOrDefault(
											createQueryKey(cls, specification),
											Collections.emptyList()
									),
									false
							);
						}
				);
	}

	protected <T> void addResultSet(Class<T> cls, Specification<T> spec, List<T> data) {
		queries.put(createQueryKey(cls, spec), data);
	}

	private <T> QueryKey createQueryKey(Class<T> cls, Specification<T> spec) {
		AbstractProducedQuery<T> query = compile(cls, spec);
		QueryParameters queryParameters = query.getQueryParameters();
		return new QueryKey(
				query.getQueryString(),
				queryParameters.getPositionalParameterTypes(),
				queryParameters.getPositionalParameterValues(),
				queryParameters.getNamedParameters()
		);
	}

	private <T> AbstractProducedQuery<T> compile(Class<T> cls, Specification<T> spec) {
		CriteriaBuilder cb = entityManagers.get(0).getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.where(spec.toPredicate(root, cq, cb));
		return (AbstractProducedQuery<T>) entityManagers.get(0).createQuery(cq).unwrap(AbstractProducedQuery.class);
	}

	private EntityKey createEntityKey(Class<?> cls, Serializable id) {
		return new EntityKey(id, getModelName(cls));
	}

	private EntityKey createEntityKey(BaseEntity entity) {
		return createEntityKey(Hibernate.unproxy(entity).getClass(), entity.getId());
	}

	private String getModelName(Class<?> cls) {
		return entityManagers.get(0).getMetamodel().entity(cls).getName();
	}

	@Data
	private static class EntityKey {

		private final Serializable id;

		private final String name;

	}

	@Data
	private static class QueryKey {

		private final String query;

		private final Type[] positionalParameterTypes;

		private final Object[] positionalParameterValues;

		private final Map<String, TypedValue> namedParameters;

	}

}
