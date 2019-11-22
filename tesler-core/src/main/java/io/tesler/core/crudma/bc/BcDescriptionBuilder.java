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

import io.tesler.core.crudma.Crudma;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.service.ResponseService;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BcDescriptionBuilder {

	@SuppressWarnings("unchecked")
	public static BcDescription build(String name, String parentName, Class<?> serviceClass, boolean refresh) {
		if (ResponseService.class.isAssignableFrom(serviceClass)) {
			return new InnerBcDescription(
					name,
					parentName,
					(Class<? extends ResponseService>) serviceClass,
					refresh
			);
		} else if (Crudma.class.isAssignableFrom(serviceClass)) {
			return new ExtremeBcDescription(
					name,
					parentName,
					(Class<? extends Crudma>) serviceClass,
					refresh
			);
		} else {
			throw new IllegalArgumentException();
		}
	}

}
