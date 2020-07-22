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

import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.impl.BcDescription;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class BusinessComponent implements BcIdentifier {

	/**
	 * Identifier
	 */
	private final String id;

	/**
	 * Parent identifier
	 */
	private final String parentId;

	/**
	 * Business component metadata
	 */
	private final BcDescription description;

	private BcHierarchy hierarchy;

	@Setter
	private QueryParameters parameters = QueryParameters.emptyQueryParameters();

	public BusinessComponent(String id, String parentId, BcDescription description) {
		this.id = normalizeNullValue(id);
		this.parentId = normalizeNullValue(parentId);
		this.description = Objects.requireNonNull(description);
	}

	public BusinessComponent(String id, String parentId, BcDescription description, BcHierarchy hierarchy) {
		this(id, parentId, description);
		this.hierarchy = hierarchy;
	}

	public BusinessComponent(String id, String parentId, BcDescription description,
			BcHierarchy hierarchy, QueryParameters parameters) {
		this(id, parentId, description, hierarchy);
		this.parameters = parameters;
	}

	public <T extends BcDescription> T getDescription() {
		return (T) description;
	}

	public Long getIdAsLong() {
		return getAsLong(id);
	}

	public Long getParentIdAsLong() {
		return getAsLong(parentId);
	}

	private Long getAsLong(String id) {
		return id == null ? null : Long.valueOf(id);
	}

	@Override
	public String getName() {
		return description.getName();
	}

	@Override
	public String getParentName() {
		return description.getParentName();
	}

	public List<String> getPreInvokeParameters() {
		return getParameters().getPreInvokeParameters();
	}

	public BusinessComponent withId(String id) {
		return new BusinessComponent(
				id,
				parentId,
				description,
				hierarchy == null ? null : hierarchy.withId(id),
				parameters
		);
	}

	public BusinessComponent withParentId(String parentId) {
		return new BusinessComponent(
				id,
				parentId,
				description,
				hierarchy == null ? null : hierarchy.withParentId(parentId),
				parameters
		);
	}

	private String normalizeNullValue(final String value) {
		if (value == null || "null".equals(value)) {
			return null;
		}
		return value;
	}

}
