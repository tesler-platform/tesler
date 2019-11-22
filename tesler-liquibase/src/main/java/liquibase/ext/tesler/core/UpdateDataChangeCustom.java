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

package liquibase.ext.tesler.core;

import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.core.UpdateDataChange;
import liquibase.database.Database;
import liquibase.database.core.OracleDatabase;
import liquibase.ext.tesler.stmt.UpdatePreparedStatement;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.UpdateStatement;

@DatabaseChange(name = "update", description = "Updates data in an existing table", priority = Integer.MAX_VALUE)
public class UpdateDataChangeCustom extends UpdateDataChange {

	public UpdateDataChangeCustom() {
		super();
	}

	@Override
	public SqlStatement[] generateStatements(Database database) {

		boolean needsPreparedStatement = false;
		for (ColumnConfig column : getColumns()) {
			if (column.getValueBlobFile() != null) {
				needsPreparedStatement = true;
			}
			if (column.getValueClobFile() != null) {
				needsPreparedStatement = true;
			}

			if ((database instanceof OracleDatabase) && (column.getType() != null) && "CLOB".equalsIgnoreCase(column
					.getType()) && (column.getValue() != null) && (column.getValue().length() >= 4000)) {
				needsPreparedStatement = true;
			}
		}

		if (needsPreparedStatement) {
			UpdatePreparedStatement statement = new UpdatePreparedStatement(
					database,
					getCatalogName(),
					getSchemaName(),
					getTableName(),
					getColumns(),
					getChangeSet(),
					getResourceAccessor()
			);

			statement.setWhereClause(where);

			for (ColumnConfig whereParam : whereParams) {
				if (whereParam.getName() != null) {
					statement.addWhereColumnName(whereParam.getName());
				}
				statement.addWhereParameter(whereParam.getValueObject());
			}

			return new SqlStatement[]{
					statement
			};
		}

		UpdateStatement statement = new UpdateStatement(getCatalogName(), getSchemaName(), getTableName());

		for (ColumnConfig column : getColumns()) {
			statement.addNewColumnValue(column.getName(), column.getValueObject());
		}

		statement.setWhereClause(where);

		for (ColumnConfig whereParam : whereParams) {
			if (whereParam.getName() != null) {
				statement.addWhereColumnName(whereParam.getName());
			}
			statement.addWhereParameter(whereParam.getValueObject());
		}

		return new SqlStatement[]{
				statement
		};
	}


}
