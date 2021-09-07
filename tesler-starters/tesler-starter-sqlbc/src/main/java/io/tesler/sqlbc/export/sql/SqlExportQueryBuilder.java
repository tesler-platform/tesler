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

package io.tesler.sqlbc.export.sql;

import com.google.common.collect.Lists;
import io.tesler.api.config.TeslerBeanProperties;
import io.tesler.sqlbc.export.base.JdbcTemplateSqlExporter;
import io.tesler.sqlbc.export.base.Parameters;
import io.tesler.sqlbc.export.sql.query.UpdateForeignKey;
import io.tesler.sqlbc.export.sql.transform.ValueTransformer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import io.tesler.sqlbc.export.sql.query.Insert;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SqlExportQueryBuilder {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final JdbcTemplateSqlExporter jdbcTemplateSqlExporter;

	SqlExportQueryBuilder(final JdbcTemplateSqlExporter jdbcTemplateSqlExporter, ApplicationContext applicationContext, TeslerBeanProperties teslerBeanProperties
	) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(applicationContext.getBean(teslerBeanProperties.getDataSource(), DataSource.class));
		this.jdbcTemplateSqlExporter = jdbcTemplateSqlExporter;
	}

	public List<Insert> inserts(final ValueTransformer valueTransformer, final String tableName,
								final Parameters parameters, final String... ignoredColumns) {
		final List<Insert> inserts = new ArrayList<>();
		if (parameters.isNotEmpty()) {
			jdbcTemplateSqlExporter.queryForMap(tableName, parameters, ignoredColumns).forEach(
					exportedRecord -> {
						final Insert insert = new Insert(tableName, exportedRecord.getId());
						exportedRecord.getColumns().forEach((key, value) ->
								insert.addColumn(
										key,
										valueTransformer.get(
												tableName,
												key.getName(),
												value
										)
								));
						inserts.add(insert);
					}
			);
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

}
