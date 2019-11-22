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

package liquibase.ext.tesler.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.ValidationErrors;
import liquibase.ext.tesler.stmt.InsertTranslationStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.core.DeleteStatement;
import liquibase.statement.core.InsertStatement;


public class InsertTranslationGenerator extends AbstractSqlGenerator<InsertTranslationStatement> {

	@Override
	public int getPriority() {
		return Integer.MAX_VALUE;
	}

	@Override
	public ValidationErrors validate(InsertTranslationStatement statement, Database database,
			SqlGeneratorChain sqlGeneratorChain) {
		ValidationErrors validationErrors = new ValidationErrors();
		validationErrors.checkRequiredField("tableName", statement.getTableName());
		validationErrors.checkRequiredField("mainTableName", statement.getMainTableName());
		validationErrors.checkRequiredField("mainTablePrimaryKey", statement.getSurrogateKey());
		validationErrors.checkRequiredField("languageColumn", statement.getLanguageKey());
		validationErrors.checkRequiredField("primaryKey", statement.getNaturalKey());
		validationErrors.checkRequiredField("columns", statement.getColumnValues());
		return validationErrors;
	}

	@Override
	public Sql[] generateSql(InsertTranslationStatement statement, Database database,
			SqlGeneratorChain sqlGeneratorChain) {
		List<Sql> result = new ArrayList<>();
		Collections.addAll(result, generateDeleteStatement(statement, database));
		Collections.addAll(result, generateInsertStatement(statement, database));
		return result.toArray(new Sql[0]);
	}

	protected Sql[] generateInsertStatement(InsertTranslationStatement statement, Database database) {
		InsertStatement insertStatement = new InsertStatement(
				statement.getCatalogName(),
				statement.getSchemaName(),
				statement.getTableName()
		);
		insertStatement.addColumnValue(
				statement.getSurrogateKey(),
				new DatabaseFunction(
						getSelectPrimaryKeyStatement(statement, database)
				)
		);
		for (Map.Entry<String, Object> e : statement.getColumnValues().entrySet()) {
			String columnName = e.getKey();
			if (statement.getNaturalKey().contains(columnName)) {
				continue;
			}
			insertStatement.addColumnValue(columnName, e.getValue());
		}
		return SqlGeneratorFactory.getInstance().generateSql(insertStatement, database);
	}

	protected Sql[] generateDeleteStatement(InsertTranslationStatement statement, Database database) {
		DeleteStatement deleteStatement = new DeleteStatement(
				statement.getCatalogName(),
				statement.getSchemaName(),
				statement.getTableName()
		);
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(toColumnPredicate(
				statement,
				database,
				statement.getLanguageKey(),
				statement.getColumnValue(statement.getLanguageKey())
		));
		whereBuilder.append(" AND ");
		whereBuilder.append(toColumnPredicate(
				statement,
				database,
				statement.getSurrogateKey(),
				new DatabaseFunction(
						getSelectPrimaryKeyStatement(statement, database)
				)
		));
		deleteStatement.setWhere(whereBuilder.toString());
		return SqlGeneratorFactory.getInstance().generateSql(deleteStatement, database);
	}

	public String getSelectPrimaryKeyStatement(InsertTranslationStatement statement, Database database) {
		StringBuilder subqeury = new StringBuilder("(SELECT ");
		subqeury.append(toDatabaseColumn(statement, database, statement.getSurrogateKey()));
		subqeury.append(" FROM ");
		subqeury.append(database.escapeTableName(
				statement.getCatalogName(),
				statement.getSchemaName(),
				statement.getMainTableName()
		));
		subqeury.append(" WHERE (");
		Iterator<String> iter = statement.getNaturalKey().iterator();
		while (iter.hasNext()) {
			String columnName = iter.next();
			subqeury.append(toColumnPredicate(
					statement,
					database,
					columnName,
					statement.getColumnValue(columnName)
			));
			if (iter.hasNext()) {
				subqeury.append(" AND ");
			}
		}
		subqeury.append("))");
		return subqeury.toString();
	}

	protected String toColumnPredicate(InsertTranslationStatement statement, Database database, String columnName,
			Object value) {
		StringBuilder builder = new StringBuilder();
		builder.append(toDatabaseColumn(statement, database, columnName));
		if (isNull(value)) {
			builder.append(" IS ");
		} else {
			builder.append(" = ");
		}
		builder.append(toDatabaseString(value, database));
		return builder.toString();
	}

	protected String toDatabaseColumn(InsertTranslationStatement statement, Database database, String columnName) {
		return database.escapeColumnName(
				statement.getCatalogName(),
				statement.getSchemaName(),
				statement.getTableName(),
				columnName
		);
	}

	protected String toDatabaseString(Object value, Database database) {
		if (isNull(value)) {
			return "NULL";
		}
		return DataTypeFactory.getInstance()
				.fromObject(value, database)
				.objectToSql(value, database);
	}

	protected boolean isNull(Object value) {
		return value == null || "NULL".equalsIgnoreCase(value.toString());
	}

}
