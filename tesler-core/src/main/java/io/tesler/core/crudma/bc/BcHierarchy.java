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

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BcHierarchy {

	/**
	 * Screen name
	 */
	private final String screenName;

	/**
	 * Name
	 */
	private final String bcName;

	/**
	 * Identifier
	 */
	private final String id;

	/**
	 * Parent
	 */
	private final BcHierarchy parent;

	public String getId(final String bcName) {
		if (Objects.equals(getBcName(), bcName)) {
			return getId();
		}
		for (BcHierarchy parent = getParent(); parent != null; parent = parent.getParent()) {
			if (Objects.equals(parent.getBcName(), bcName)) {
				return parent.getId();
			}
		}
		return null;
	}

	public BcHierarchy withId(String id) {
		return new BcHierarchy(
				screenName,
				bcName,
				id,
				parent
		);
	}

	public BcHierarchy withParentId(String parentId) {
		return new BcHierarchy(
				screenName,
				bcName,
				parentId,
				parent == null ? null : parent.withId(parentId)
		);
	}

}
