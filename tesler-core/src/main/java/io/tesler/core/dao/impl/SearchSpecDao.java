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

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.SearchSpec;
import io.tesler.model.ui.entity.SearchSpec_;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SearchSpecDao {

	private final ParametrizedSSSpecification securitySpecification =
			serviceName -> (root, cq, cb) -> cb.and(
					cb.equal(root.get(SearchSpec_.type), CoreDictionaries.SearchSpecType.SECURITY),
					cb.equal(root.get(SearchSpec_.serviceName), serviceName)
			);

	private final ParametrizedSSSpecification bcSpecification =
			bcName -> (root, cq, cb) -> cb.and(
					cb.equal(root.get(SearchSpec_.type), CoreDictionaries.SearchSpecType.BC),
					cb.equal(root.get(SearchSpec_.bcName), bcName)
			);

	private final ParametrizedSSSpecification linkSpecification =
			serviceName -> (root, cq, cb) -> cb.and(
					cb.equal(root.get(SearchSpec_.type), CoreDictionaries.SearchSpecType.LINK),
					cb.equal(root.get(SearchSpec_.serviceName), serviceName)
			);

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private SessionService sessionService;

	public Specification<SearchSpec> securitySpecification(String serviceName) {
		return securitySpecification.toSpecification(serviceName);
	}

	public Specification<SearchSpec> bcSpecification(String bcName) {
		return bcSpecification.toSpecification(bcName);
	}

	public Specification<SearchSpec> linkSpecification(String serviceName) {
		return linkSpecification.toSpecification(serviceName);
	}

	@Cacheable(
			cacheNames = {CacheConfig.SPECIFICATION_CACHE},
			key = "{#root.methodName, #bcDescription?.name, @sessionService.sessionUserRole}"
	)
	public List<SearchSpec> getSecuritySpecifications(InnerBcDescription bcDescription) {
		if (bcDescription == null || bcDescription.getServiceClass() == null) {
			return Collections.emptyList();
		}
		return jpaDao.getList(
				SearchSpec.class,
				Specification
						.where(securitySpecification.toSpecification(
								bcDescription.getServiceClass().getSimpleName()
						))
						.and((root, cq, cb) -> cb.or(
								cb.equal(root.get(SearchSpec_.roleCd), sessionService.getSessionUserRole()),
								cb.isNull(root.get(SearchSpec_.roleCd))
						))
		);
	}

	@Cacheable(
			cacheNames = {CacheConfig.SPECIFICATION_CACHE},
			key = "{#root.methodName, #bcDescription?.name}"
	)
	public List<SearchSpec> getBcSpecifications(InnerBcDescription bcDescription) {
		if (bcDescription == null || bcDescription.getServiceClass() == null) {
			return Collections.emptyList();
		}
		return jpaDao.getList(
				SearchSpec.class,
				bcSpecification.toSpecification(bcDescription.getName())
		);
	}

	@Cacheable(
			cacheNames = {CacheConfig.SPECIFICATION_CACHE},
			key = "{#root.methodName, #bcDescription?.name}"
	)
	public List<SearchSpec> getLinkSpecifications(InnerBcDescription bcDescription) {
		if (bcDescription == null || bcDescription.getServiceClass() == null) {
			return Collections.emptyList();
		}
		return jpaDao.getList(
				SearchSpec.class,
				(root, cq, cb) -> cb.and(
						cb.equal(root.get(SearchSpec_.type), CoreDictionaries.SearchSpecType.LINK),
						cb.equal(root.get(SearchSpec_.bcName), bcDescription.getName())
				)
		);
	}

	@CacheEvict(cacheNames = CacheConfig.SPECIFICATION_CACHE, allEntries = true)
	public void clearCache() {

	}

	private interface ParametrizedSSSpecification {

		Specification<SearchSpec> toSpecification(String parameter);

	}

}
