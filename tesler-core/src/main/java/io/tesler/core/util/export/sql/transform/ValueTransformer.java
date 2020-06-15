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

package io.tesler.core.util.export.sql.transform;

import io.tesler.core.util.export.sql.TableColumn;
import java.util.HashMap;
import java.util.Map;


public class ValueTransformer {

	private final Map<TableColumn, Transformation> transformations = new HashMap<>();

	public ValueTransformer add(final Transformation transformation, final TableColumn... tableColumns) {
		for (final TableColumn tableColumn : tableColumns) {
			transformations.put(tableColumn, transformation);
		}
		return this;
	}

	public Object get(final String table, final String column, final Object value) {
		final TableColumn tableColumn = new TableColumn(table, column);
		if (!transformations.containsKey(tableColumn)) {
			return value;
		}
		return transformations.get(tableColumn).transform(value);
	}

}
