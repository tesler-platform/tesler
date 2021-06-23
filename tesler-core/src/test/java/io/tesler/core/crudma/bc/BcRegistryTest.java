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

package io.tesler.core.crudma.bc;

import static io.tesler.core.crudma.bc.TestServiceAssociation.childBcWithRefreshExample;
import static io.tesler.core.crudma.bc.TestServiceAssociation.extremeBcExample;
import static io.tesler.core.crudma.bc.TestServiceAssociation.innerBcExample;
import static io.tesler.core.crudma.bc.TestServiceAssociation.overridableBcExample;
import static org.assertj.core.api.Assertions.assertThat;

import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.BcRegistryImpl;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.inner.InnerCrudmaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@DirtiesContext
@SpringJUnitConfig({
		BcRegistryImpl.class,
		TestBcSupplier.class,
		TestEmptyDeploymentTransactionSupport.class,
		TestCrudma.class,
		TestReponseService.class,
		TestBcOverrider.class
})
class BcRegistryTest {

	@Autowired
	BcRegistry bcRegistry;

	@Test
	void bcRegistryShouldReturnCorrentNumberOfBusinessComponents() throws Exception {
		assertThat(bcRegistry.getAllBcNames()).isNotEmpty().hasSize(4);
	}

	@Test
	void bcRegistryShouldReturnInnerBc() throws Exception {
		BcDescription bcDescription = bcRegistry.getBcDescription(innerBcExample.getName());
		assertThat(bcDescription).isInstanceOf(InnerBcDescription.class);
		assertThat(bcDescription.getCrudmaService()).isEqualTo(InnerCrudmaService.class);
		assertThat(((InnerBcDescription) bcDescription).getServiceClass()).isEqualTo(TestReponseService.class);
		assertThat(bcDescription.getName()).isEqualTo(innerBcExample.getName());
	}

	@Test
	void bcRegistryShouldReturnExtremeBc() throws Exception {
		BcDescription bcDescription = bcRegistry.getBcDescription(extremeBcExample.getName());
		assertThat(bcDescription).isInstanceOf(ExtremeBcDescription.class);
		assertThat(bcDescription.getCrudmaService()).isEqualTo(TestCrudma.class);
		assertThat(bcDescription.getName()).isEqualTo(extremeBcExample.getName());
	}

	@Test
	void bcRegistryShouldOverrideBcCorrectly() throws Exception {
		BcDescription bcDescription = bcRegistry.getBcDescription(overridableBcExample.getName());
		assertThat(bcDescription).isInstanceOf(InnerBcDescription.class);
		assertThat(bcDescription.getCrudmaService()).isEqualTo(InnerCrudmaService.class);
		assertThat(((InnerBcDescription) bcDescription).getServiceClass()).isEqualTo(TestReponseService.class);
	}

	@Test
	void bcRegistryShouldReturnChildBusinessComponents() throws Exception {
		BcDescription bcDescription = bcRegistry.getBcDescription(childBcWithRefreshExample.getName());
		assertThat(bcDescription.getParentName()).isEqualTo(extremeBcExample.getName());
		assertThat(bcDescription.isRefresh()).isEqualTo(true);
	}

}
