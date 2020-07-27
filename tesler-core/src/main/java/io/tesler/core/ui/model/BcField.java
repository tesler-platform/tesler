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

package io.tesler.core.ui.model;

import java.util.EnumMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(exclude = "attributes")
@RequiredArgsConstructor
public final class BcField {

	public static final String FIELD_ASSOCIATE = "_associate";

	@Getter
	private final String bc;

	@Getter
	private final String name;

	private final Map<Attribute, Object> attributes = new EnumMap<>(Attribute.class);

	public BcField putAttribute(final Attribute attribute, final Object value) {
		attributes.put(attribute, value);
		return this;
	}

	public <T> T getAttribute(final Attribute attribute) {
		return (T) attributes.get(attribute);
	}

	public boolean containsAttribute(final Attribute attribute) {
		return attributes.containsKey(attribute);
	}

	public enum Attribute {

		/**
		 * widget identifier
		 */
		WIDGET_ID,

		/**
		 * Field type
		 */
		TYPE,

		ICON_TYPE_KEY,

		HINT_KEY,

		/**
		 * List of picklist fields
		 */
		PICK_LIST_FIELDS,

		/**
		 * Field with the current field specified in the picklist
		 */
		PARENT_FIELD,

		/**
		 * BC with the current field specified in the picklist
		 */
		PARENT_BC;

	}

}
