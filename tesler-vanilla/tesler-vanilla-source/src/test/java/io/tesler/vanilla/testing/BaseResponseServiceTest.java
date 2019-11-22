/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.testing;

import io.tesler.core.service.ResponseService;
import io.tesler.testing.tests.AbstractResponseServiceTest;
import io.tesler.vanilla.VanillaServiceAssociation.VanillaBcSupplier;
import io.tesler.vanilla.config.VanillaTestApplicationConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;


@ContextHierarchy({
		@ContextConfiguration(
				name = "child",
				classes = {
						VanillaBcSupplier.class,
				}
		),
		@ContextConfiguration(
				name = "root",
				classes = {
						VanillaTestApplicationConfig.class
				}
		)
})
public abstract class BaseResponseServiceTest<T extends ResponseService> extends AbstractResponseServiceTest<T> {

}
