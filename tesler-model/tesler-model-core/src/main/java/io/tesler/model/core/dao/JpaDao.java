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

package io.tesler.model.core.dao;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dao.Selector;
import io.tesler.api.data.dao.UpdateSpecification;
import io.tesler.model.core.api.EmbeddedKeyable;
import io.tesler.model.core.entity.AbstractEntity;
import io.tesler.model.core.entity.BaseEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.persistence.EntityGraph;
import javax.persistence.LockModeType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;


public interface JpaDao {

	@Deprecated
	//Deleted in next major
	//You can use getList(Class<T> entityClass, Specification<T> specification) with cb.equal() Specification instead
	<T extends BaseEntity> T findById(String type, Long id);

	<T extends BaseEntity> T findById(Class<T> clazz, Long id);

	<T> EntityGraph<? super T> getEntityGraph(Class<T> clazz, String name);

	<T> void applyGraph(Root<T> root, EntityGraph<? super T> fetchGraph);

	<T> void applyGraph(Class<T> clazz, Root<T> root, String fetchGraph);

	Long getCount(Class<?> clazz);

	<T> Long getCount(Class<T> entityClass, Specification<T> specification);

	<T> T save(Object entity);

	<T extends BaseEntity> T evict(T o);

	void flush();

	void refresh(AbstractEntity o);

	void delete(AbstractEntity o);

	<T extends BaseEntity> T delete(Class<T> clazz, Long id);

	<T> int delete(Class<T> entityClass, Specification<T> spec);

	<T> int update(Class<T> entityClass, Specification<T> spec, UpdateSpecification<T> updateSpec);

	<T> List<T> getList(Class<T> clazz);

	@Deprecated
	//Deleted in next major
	//You can use getList(Class<T> entityClass, Specification<T> specification) with cb.equal() Specification instead
	<T, X> List<T> getList(Class<T> clazz, SingularAttribute<T, X> name, X value);

	<T> List<T> getList(Class<T> entityClass, Specification<T> specification);

	<R, T> ResultPage<T> getPage(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification, PageSpecification page);

	<T> ResultPage<T> getPage(Class<T> entityClass, Specification<T> specification, PageSpecification page);

	<T> Stream<T> getStream(Class<T> entityClass, Specification<T> specification);

	<R, T> Stream<T> getStream(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification);

	<R, T> List<T> getList(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification);

	<T> T getSingleResult(Class<T> entityClass, Specification<T> specification);

	<T extends BaseEntity> T fetchBySpecification(Class<T> entityClass, Specification<T> specification);

	<R, T> T getSingleResult(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification);

	<T> T getSingleResultOrNull(Class<T> entityClass, Specification<T> specification);

	<R, T> T getSingleResultOrNull(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification);

	<T> T getFirstResultOrNull(Class<T> entityClass, Specification<T> specification);

	<R, T> T getFirstResultOrNull(Class<R> rootClass, Class<T> targetClass, Selector<R, T> selector,
			Specification<R> specification);

	<T> boolean exists(Class<T> entityClass, Specification<T> specification);

	<T> List<T> getListByIds(Class<T> clazz, List<Long> ids);

	@Deprecated
	//Deleted in next major
	//You can use save(Object entity) instead
	void saveWithCompositeKey(EmbeddedKeyable o);

	@Deprecated
	//Deleted in next major
	//You can use delete
	void deleteWithCompositeKey(EmbeddedKeyable o);

	@Deprecated
	//Deleted in next major
	//You can use getList(entityClazz, Long.class, (root, cb) -> root.get("id"), searchSpec) instead
	<T> List<Long> getIds(Class<T> entityClazz, Specification<T> searchSpec);

	void clear();

	void lock(AbstractEntity entity, LockModeType lockMode, int timeout);

	void lockAndRefresh(AbstractEntity entity, int timeout);

	void revert(AbstractEntity entity);

	@Deprecated
	//Deleted in next major
	//You can use EntityManager directly
	<T> List<T> selectNativeQuery(Class<T> entityClazz, String sql, Map<String, Object> params);

	<T> EntityType<T> getEntityType(Class<T> cls);

}
