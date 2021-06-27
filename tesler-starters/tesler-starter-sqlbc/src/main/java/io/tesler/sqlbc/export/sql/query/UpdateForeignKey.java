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

package io.tesler.sqlbc.export.sql.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateForeignKey implements Query {

	private final String tableName;

	private final BigDecimal lineId;

	private final Map<String, BigDecimal> columns = new LinkedHashMap<>();

	public void addColumn(final String name, final BigDecimal value) {
		if (value != null) {
			columns.put(name, value);
		}
	}

	@Override
	public String toSql() {
		final List<String> columns = new ArrayList<>();
		for (final Map.Entry<String, BigDecimal> column : this.columns.entrySet()) {
			columns.add(column.getKey() + " = " + column.getValue());
		}
		return String.format("UPDATE %s SET %s WHERE ID = %s;", tableName, StringUtils.join(columns, ", "), lineId);
	}

}
