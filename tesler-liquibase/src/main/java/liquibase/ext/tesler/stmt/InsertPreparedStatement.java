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

import io.tesler.db.migration.liquibase.util.ColumnUtils;
import java.util.List;
import liquibase.change.ColumnConfig;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.InsertExecutablePreparedStatement;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;


public class InsertPreparedStatement extends InsertExecutablePreparedStatement {

	public InsertPreparedStatement(Database database, String tableName, List<ColumnConfig> columns, ChangeSet changeSet,
			ResourceAccessor resourceAccessor) {
		super(database, null, null, tableName, columns, changeSet, resourceAccessor);
	}

	public InsertPreparedStatement(
			Database database,
			String catalogName,
			String schemaName,
			String tableName,
			List<ColumnConfig> columns,
			ChangeSet changeSet,
			ResourceAccessor resourceAccessor
	) {
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

	public SqlStatement simplify() {
		if (ColumnUtils.hasBlobs(getColumns())) {
			return this;
		}
		InsertStatement statement = new InsertStatement(getCatalogName(), getSchemaName(), getTableName());
		for (ColumnConfig column : getColumns()) {
			if (database.supportsAutoIncrement() && Boolean.TRUE.equals(column.isAutoIncrement() != null)) {
				continue;
			}
			statement.addColumnValue(column.getName(), column.getValueObject());
		}
		return statement;
	}

	@Override
	protected String generateSql(List<ColumnConfig> cols) {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		StringBuilder params = new StringBuilder("VALUES(");
		sql.append(database.escapeTableName(getCatalogName(), getSchemaName(), getTableName()));
		sql.append("(");
		for (ColumnConfig column : getColumns()) {
			if (database.supportsAutoIncrement() && Boolean.TRUE.equals(column.isAutoIncrement())) {
				continue;
			}
			sql.append(database.escapeColumnName(getCatalogName(), getSchemaName(), getTableName(), column.getName()));
			sql.append(", ");
			Object value = column.getValueObject();
			if (value instanceof DatabaseFunction) {
				params.append(database.generateDatabaseFunctionValue((DatabaseFunction) value));
				params.append(", ");
			} else {
				params.append("?, ");
				cols.add(column);
			}
		}
		sql.deleteCharAt(sql.lastIndexOf(" "));
		sql.deleteCharAt(sql.lastIndexOf(","));
		params.deleteCharAt(params.lastIndexOf(" "));
		params.deleteCharAt(params.lastIndexOf(","));
		params.append(")");
		sql.append(") ");
		sql.append(params);
		return sql.toString();
	}

}
