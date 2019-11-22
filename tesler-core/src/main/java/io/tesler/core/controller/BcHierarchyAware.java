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

package io.tesler.core.controller;

import io.tesler.core.crudma.bc.BcHierarchy;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.impl.BcDescription;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class BcHierarchyAware {

	private final BcHierarchy bcHierarchy;

	private final BcRegistry bcRegistry;

	public BcHierarchyAware(HttpServletRequest request, BCFactory bcFactory, BcRegistry bcRegistry) {
		this.bcHierarchy = getBcHierarchy(request, bcFactory);
		this.bcRegistry = bcRegistry;
	}

	private BcHierarchy getBcHierarchy(HttpServletRequest request, BCFactory bcFactory) {
		try {
			return bcFactory.getBcHierarchy(request);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean hasHierarchy() {
		return bcHierarchy != null;
	}

	@Deprecated
	public BcDescription getBcDescription() {
		if (bcHierarchy == null) {
			throw new IllegalStateException();
		}
		return bcRegistry.getBcDescription(bcHierarchy.getBcName());
	}

	@Deprecated
	public String getScreenName() {
		if (bcHierarchy == null) {
			throw new IllegalStateException();
		}
		return bcHierarchy.getScreenName();
	}

}
