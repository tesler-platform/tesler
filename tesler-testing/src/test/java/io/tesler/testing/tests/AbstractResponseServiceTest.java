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

import io.tesler.core.bc.InnerBcTypeAware;
import io.tesler.core.config.JacksonConfig;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.BcRegistryImpl;
import io.tesler.core.dao.impl.SearchSpecDao;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ValidatorsProviderImpl;
import io.tesler.core.dto.mapper.DtoConstructorService;
import io.tesler.core.dto.mapper.RequestValueCache;
import io.tesler.core.service.DTOMapper;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.core.ui.BcUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextHierarchy(
		@ContextConfiguration(
				name = "child",
				classes = {
						DtoConstructorService.class,
						DTOMapper.class,
						RequestValueCache.class,
						BcUtils.class,
						InnerBcTypeAware.class,
						ResponseFactory.class,
						ResponseBuilder.class,
						BcRegistryImpl.class,
						SearchSpecDao.class,
						ValidatorsProviderImpl.class,
						JacksonConfig.class
				}
		)
)
public abstract class AbstractResponseServiceTest<T extends ResponseService> extends BaseDAOAwareTest {

	protected abstract Class<T> getServiceClass();

	protected T getService() {
		return applicationContext.getBean(getServiceClass());
	}

	protected BusinessComponent createBc(BcDescription bc) {
		return createBc(bc, null);
	}

	protected BusinessComponent createBc(BcDescription bc, String id) {
		return createBc(bc, id, null);
	}

	protected BusinessComponent createBc(BcDescription bc, String id, String parentId) {
		return new BusinessComponent(id, parentId, bc);
	}

}

