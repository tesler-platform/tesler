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

package io.tesler.core.util.export;

import io.tesler.core.crudma.impl.sql.utils.SqlFieldType;
import io.tesler.core.util.export.model.Parameters;
import io.tesler.core.util.export.model.db.ColumnMeta;
import io.tesler.core.util.export.model.db.TableMeta;
import io.tesler.core.util.export.model.query.Insert;
import io.tesler.core.util.export.model.query.UpdateForeignKey;
import io.tesler.core.util.export.transform.ValueTransformer;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

@Service
public class SqlExportQueryBuilder {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	SqlExportQueryBuilder(@Qualifier("primaryDS") final DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<Insert> inserts(final ValueTransformer valueTransformer, final String tableName,
			final Parameters parameters, final String... ignoredColumns) {
		final List<Insert> inserts = new ArrayList<>();
		if (parameters.isNotEmpty()) {
			final TableMeta tableMeta = getTableMeta(tableName, getAllIgnoredColumns(ignoredColumns));
			for (final List<BigDecimal> ids : Lists.partition(parameters.getIds(), 500)) {
				jdbcTemplate.query(
						String.format(
								"select * from %s where %s in(:ids) order by id",
								tableMeta.getName(),
								parameters.getColumnName()
						),
						new MapSqlParameterSource("ids", ids),
						rs -> {
							final Insert insert = new Insert(tableMeta.getName(), rs.getBigDecimal("ID"));
							for (final ColumnMeta fieldMeta : tableMeta.getColumns()) {
								insert.addColumn(
										fieldMeta,
										valueTransformer.get(
												tableName,
												fieldMeta.getName(),
												rs.getObject(fieldMeta.getName(), fieldMeta.getType().getJavaClass())
										)
								);
							}
							inserts.add(insert);
						}
				);
			}
		}
		return inserts;
	}

	public List<UpdateForeignKey> updateForeignKeys(final ValueTransformer valueTransformer, final String tableName,
			final Parameters parameters, final String... columns) {
		final List<UpdateForeignKey> inserts = new ArrayList<>();
		if (parameters.isNotEmpty()) {
			for (final List<BigDecimal> ids : Lists.partition(parameters.getIds(), 500)) {
				jdbcTemplate.query(String.format(
						"select * from %s where %s in(:ids) and (1 = 2%s) order by id",
						tableName,
						parameters.getColumnName(),
						notNullCondition(columns)
				), new MapSqlParameterSource("ids", ids), rs -> {
					final UpdateForeignKey update = new UpdateForeignKey(
							tableName,
							(BigDecimal) valueTransformer.get(tableName, "ID", rs.getBigDecimal("ID"))
					);
					for (final String column : columns) {
						update.addColumn(column, (BigDecimal) valueTransformer.get(tableName, column, rs.getBigDecimal(column)));
					}
					inserts.add(update);
				});
			}
		}
		return inserts;
	}

	private String notNullCondition(final String... columns) {
		final StringBuilder where = new StringBuilder();
		for (final String column : columns) {
			where.append(" or ").append(column).append(" is not null");
		}
		return where.toString();
	}

	private Set<String> getAllIgnoredColumns(final String... ignoredColumns) {
		final Set<String> result = new HashSet<>(Arrays
				.asList("CREATED_DATE", "UPDATED_DATE", "CREATED_BY_USER_ID", "LAST_UPD_BY_USER_ID", "VSTAMP"));
		Collections.addAll(result, ignoredColumns);
		return result;
	}

	private TableMeta getTableMeta(final String tableName, final Set<String> ignoredColumns) {
		final SqlRowSetMetaData metaData = jdbcTemplate.queryForRowSet(
				String.format("select * from (select * from %s) where rownum <= 0", tableName),
				new EmptySqlParameterSource()
		).getMetaData();
		final TableMeta tableMeta = new TableMeta(tableName);
		for (int columnNumber = 1; columnNumber <= metaData.getColumnCount(); columnNumber++) {
			final String columnName = metaData.getColumnName(columnNumber);
			if (!ignoredColumns.contains(columnName)) {
				tableMeta.addColumn(columnName, SqlFieldType.Holder.getFromSqlType(metaData.getColumnType(columnNumber)));
			}
		}
		return tableMeta;
	}

}
