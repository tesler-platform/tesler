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

import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BcHierarchy;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BCFactory {

	private final Set<String> operations = ImmutableSet.of(
			"data", "count", "custom-action", "associate", "row-meta-new", "row-meta-empty", "row-meta"
	);

	private final BcRegistry bcRegistry;

	BcHierarchy getBcHierarchy(HttpServletRequest request) {
		Deque<String> bcList = getRequestParts(request);
		final String operation = bcList.removeFirst();
		if (!operations.contains(operation)) {
			throw new IllegalArgumentException("Operation is not supported");
		}

		String screen = bcList.removeFirst();

		String view = null;
		if ("screen".equals(screen)) {
			screen = bcList.removeFirst();
			if ("view".equals(bcList.peekFirst())) {
				bcList.removeFirst();
				view = bcList.removeFirst();
			}
		}

		if (bcList.isEmpty()) {
			throw new IllegalArgumentException("URI must contain at least one BC");
		} else if (bcList.getLast().equals("null")) {
			bcList.removeLast();
		}
		BcHierarchy bcHierarchy = null;
		while (!bcList.isEmpty()) {
			bcHierarchy = new BcHierarchy(
					screen,
					view,
					bcList.removeFirst(),
					bcList.isEmpty() ? null : bcList.removeFirst(),
					bcHierarchy
			);
		}
		return bcHierarchy;
	}

	private Deque<String> getRequestParts(HttpServletRequest request) {
		String uri = request.getRequestURI();
		for (String prefix : new String[]{request.getContextPath(), request.getServletPath(), "/",}) {
			uri = StringUtils.removeStart(uri, prefix);
		}
		return new LinkedList<>(Arrays.asList(uri.split("/")));
	}

	BusinessComponent getBusinessComponent(HttpServletRequest request, QueryParameters queryParameters) {
		return getBusinessComponent(getBcHierarchy(request), queryParameters);
	}

	public BusinessComponent getBusinessComponent(BcHierarchy bcHierarchy, QueryParameters queryParameters) {
		return new BusinessComponent(
				Optional.of(bcHierarchy).map(BcHierarchy::getId).orElse(null),
				Optional.of(bcHierarchy).map(BcHierarchy::getParent).map(BcHierarchy::getId).orElse(null),
				bcRegistry.getBcDescription(bcHierarchy.getBcName()),
				bcHierarchy,
				queryParameters
		);
	}

}
