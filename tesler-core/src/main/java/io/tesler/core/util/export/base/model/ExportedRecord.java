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

package io.tesler.core.util.export.base.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ExportedRecord {

	final String tableName;

	final Map<ColumnMeta, Object> columns = new HashMap<>();

	@Setter
	BigDecimal id;

	public void addColumn(final ColumnMeta meta, final Object value) {
		columns.put(meta, value);
	}

	public Object getValue(final String columnName) {
		return columns.entrySet().stream()
				.filter(entry -> columnName.equalsIgnoreCase(entry.getKey().getName()))
				.findAny()
				.map(Entry::getValue)
				.orElse(null);
	}

}
