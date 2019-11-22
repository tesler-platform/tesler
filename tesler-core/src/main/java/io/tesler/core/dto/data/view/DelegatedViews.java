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

package io.tesler.core.dto.data.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DelegatedViews {

	private final String primaryBc;

	private final String primaryViewName;

	@JsonIgnore
	private final Map<Long, Map<String, DelegatedView>> views = new HashMap<>();

	public void addView(final Long counterpartyId, final String viewName) {
		views.computeIfAbsent(
				counterpartyId,
				o -> new HashMap<>()
		).put(viewName, new DelegatedView(viewName));
	}

	public void addView(final Long counterpartyId, final DelegatedView view) {
		views.computeIfAbsent(
				counterpartyId,
				o -> new HashMap<>()
		).put(view.getViewName(), view);
	}

	public DelegatedViews merge(DelegatedViews other) {
		DelegatedViews result = new DelegatedViews(primaryBc, primaryViewName);
		result.views.putAll(views);
		other.views.forEach((k, v) -> {
			v.forEach((n, d) -> {
				result.views.computeIfAbsent(k, x -> new HashMap<>())
						.putIfAbsent(n, d);
			});
		});
		return result;
	}

	@JsonIgnore
	public List<String> getAllViews() {
		Set<String> result = new HashSet<>();
		views.forEach((k, v) -> result.addAll(v.keySet()));
		return new ArrayList<>(result);
	}

	@JsonProperty("views")
	public Map<Long, Collection<DelegatedView>> getFlatViews() {
		Map<Long, Collection<DelegatedView>> result = new HashMap<>();
		views.forEach((k, v) -> result.put(k, v.values()));
		return result;
	}


}
