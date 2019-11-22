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

package io.tesler.core.dao;

import io.tesler.api.data.ResultPage;
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.model.core.dao.JpaDao;
import javax.persistence.EntityGraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;


public interface BaseDAO extends JpaDao {

	<T, X> Long getCount(Class<T> clazz, Class dtoClazz, SingularAttribute<T, X> name, X value,
			QueryParameters queryParameters);

	Long getCount(CriteriaQuery<Long> cq, Root<?> root, Class dtoClazz, Predicate defaultSearchSpec,
			QueryParameters parameters);

	<T> Long getCount(Class<T> entityClass, Class<?> dtoClazz, Specification<T> searchSpec, QueryParameters parameters);

	<T> ResultPage<T> getList(CriteriaQuery<T> cq, Root<T> root, Class dtoClazz, Predicate defaultSearchSpec,
			QueryParameters parameters);

	<T> ResultPage<T> getList(CriteriaQuery<T> cq, Root<T> root, Class dtoClazz, Predicate defaultSearchSpec,
			QueryParameters parameters, EntityGraph<? super T> fetchGraph);

	<T> ResultPage<T> getList(Class<T> entityClazz, Class dtoClazz, Specification<T> defaultSearchSpec,
			QueryParameters parameters);

	<T> ResultPage<T> getList(Class<T> entityClazz, Class dtoClazz, Specification<T> defaultSearchSpec,
			QueryParameters parameters, EntityGraph<? super T> fetchGraph);

	Predicate getPredicateFromSearchParams(CriteriaBuilder cb, Root<?> root, Class dtoClazz,
			FilterParameters searchParams);


}
