/*-
 * #%L
 * IO Tesler - Liquibase
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

package liquibase.ext.tesler.stmt;

import static liquibase.util.SqlUtil.replacePredicatePlaceholders;

import io.tesler.db.migration.liquibase.util.ColumnUtils;
import java.util.List;
import liquibase.change.ColumnConfig;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.UpdateExecutablePreparedStatement;


public class UpdatePreparedStatement extends UpdateExecutablePreparedStatement {

	public UpdatePreparedStatement(Database database, String catalogName, String schemaName,
			String tableName, List<ColumnConfig> columns, ChangeSet changeSet,
			ResourceAccessor resourceAccessor) {
		super(
				database,
				catalogName,
				schemaName,
				tableName,
				ColumnUtils.normalizeLobs(
						columns,
						changeSet,
						resourceAccessor
				),
				changeSet,
				resourceAccessor
		);
	}

	@Override
	protected String generateSql(List<ColumnConfig> cols) {
		StringBuilder sql = new StringBuilder("UPDATE ")
				.append(database.escapeTableName(getCatalogName(), getSchemaName(), getTableName()));
		StringBuilder params = new StringBuilder(" SET ");
		for (ColumnConfig column : getColumns()) {
			params.append(database.escapeColumnName(getCatalogName(), getSchemaName(), getTableName(), column.getName()));
			params.append(" = ");
			Object value = column.getValueObject();
			if (value instanceof DatabaseFunction) {
				params.append(database.generateDatabaseFunctionValue((DatabaseFunction) value));
				params.append(", ");
			} else {
				params.append("?, ");
				cols.add(column);
			}
		}
		params.deleteCharAt(params.lastIndexOf(" "));
		params.deleteCharAt(params.lastIndexOf(","));
		sql.append(params);
		if (getWhereClause() != null) {
			sql.append(" WHERE ").append(replacePredicatePlaceholders(
					database,
					getWhereClause(),
					getWhereColumnNames(),
					getWhereParameters()
			));
		}

		return sql.toString();
	}

}
