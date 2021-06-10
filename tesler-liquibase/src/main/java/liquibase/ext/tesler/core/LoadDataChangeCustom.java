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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import liquibase.change.DatabaseChange;
import liquibase.change.core.LoadDataChange;
import liquibase.change.core.LoadDataColumnConfig;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.ext.tesler.stmt.InsertPreparedStatement;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.BatchDmlExecutablePreparedStatement;
import liquibase.statement.ExecutablePreparedStatementBase;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertSetStatement;

@DatabaseChange(name = "loadData", description = "Loads data from a CSV file into an existing table", priority = Integer.MAX_VALUE)
public class LoadDataChangeCustom extends LoadDataChange {

	@Override
	public SqlStatement[] generateStatements(Database database) {
		if (Boolean.TRUE.equals(getUsePreparedStatements())) {
			return super.generateStatements(database);
		}
		List<SqlStatement> result = new ArrayList<>();
		for (SqlStatement statement : super.generateStatements(database)) {
			result.addAll(simplify(statement));
		}
		return result.toArray(new SqlStatement[0]);
	}

	private List<SqlStatement> simplify(List<? extends SqlStatement> sqlStatements) {
		List<SqlStatement> result = new ArrayList<>(sqlStatements.size());
		for (SqlStatement sqlStatement : sqlStatements) {
			result.addAll(simplify(sqlStatement));
		}
		return result;
	}

	private List<SqlStatement> simplify(SqlStatement sqlStatement) {
		if (sqlStatement instanceof BatchDmlExecutablePreparedStatement) {
			return simplify(((BatchDmlExecutablePreparedStatement) sqlStatement).getIndividualStatements());
		}
		if (sqlStatement instanceof InsertSetStatement) {
			return simplify(((InsertSetStatement) sqlStatement).getStatements());
		}
		if (sqlStatement instanceof InsertPreparedStatement) {
			return Collections.singletonList(((InsertPreparedStatement) sqlStatement).simplify());
		}
		return Collections.singletonList(sqlStatement);
	}

	@Override
	protected ExecutablePreparedStatementBase createPreparedStatement(Database database, String catalogName,
																																		String schemaName, String tableName, List<LoadDataColumnConfig> columns, ChangeSet changeSet,
																																		ResourceAccessor resourceAccessor) {
		return new InsertPreparedStatement(database, catalogName, schemaName, tableName, columns,
				changeSet, resourceAccessor
		);
	}


}
