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

package io.tesler.core.util.export.base;

import com.google.common.collect.Lists;
import io.tesler.core.crudma.impl.sql.utils.SqlFieldType;
import io.tesler.core.util.export.base.model.ExportedRecord;
import io.tesler.core.util.export.base.model.TableMeta;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

@Component
public class JdbcTemplateSqlExporter {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public JdbcTemplateSqlExporter(@Qualifier("primaryDS") final DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<ExportedRecord> queryForMap(final String tableName,
			final Parameters parameters, final String... ignoredColumns) {
		final List<ExportedRecord> result = new ArrayList<>();
		if (parameters.isNotEmpty()) {
			final TableMeta tableMeta = getTableMeta(tableName, getAllIgnoredColumns(ignoredColumns));
			for (final List<BigDecimal> ids : Lists.partition(parameters.getIds(), 500)) {
				result.addAll(jdbcTemplate.query(
						String.format(
								"select * from %s where %s in(:ids) order by id",
								tableMeta.getTableName(),
								parameters.getColumnName()
						),
						new MapSqlParameterSource("ids", ids),
						new ExportedRecordRowMapper(tableMeta)
				));
			}
		}
		return result;
	}


	private Set<String> getAllIgnoredColumns(final String... ignoredColumns) {
		final Set<String> result = new HashSet<>(Arrays
				.asList("CREATED_DATE", "UPDATED_DATE", "CREATED_BY_USER_ID", "LAST_UPD_BY_USER_ID", "VSTAMP"));
		Stream.of(ignoredColumns).map(String::toUpperCase).forEach(result::add);
		return result;
	}

	private TableMeta getTableMeta(final String tableName, final Set<String> ignoredColumns) {
		final SqlRowSetMetaData metaData = jdbcTemplate.queryForRowSet(
				String.format("select * from (select * from %s) as exported_table where 1=0", tableName),
				new EmptySqlParameterSource()
		).getMetaData();
		final TableMeta tableMeta = new TableMeta(tableName);
		for (int columnNumber = 1; columnNumber <= metaData.getColumnCount(); columnNumber++) {
			final String columnName = metaData.getColumnName(columnNumber).toUpperCase();
			if (!ignoredColumns.contains(columnName)) {
				tableMeta.addColumn(columnName, SqlFieldType.Holder.getFromSqlType(metaData.getColumnType(columnNumber)));
			}
		}
		return tableMeta;
	}

}
