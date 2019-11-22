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

import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.ext.tesler.stmt.InsertTranslationStatement;
import liquibase.statement.core.InsertStatement;


@DatabaseChange(name = "loadTranslationData", description = "Loads translation data from a CSV file into an existing table", priority = Integer.MAX_VALUE)
public class LoadTranslationDataChange extends LoadDataChangeCustom {

	private String mainTableName;

	private String surrogateKey;

	private String naturalKey;

	private String languageKey;

	@DatabaseChangeProperty(description = "Main table name", requiredForDatabase = "all")
	public String getMainTableName() {
		return mainTableName;
	}

	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}

	@DatabaseChangeProperty(description = "Main table surrogate key", requiredForDatabase = "all")
	public String getSurrogateKey() {
		return surrogateKey;
	}

	public void setSurrogateKey(String surrogateKey) {
		this.surrogateKey = surrogateKey;
	}

	@DatabaseChangeProperty(description = "Comma delimited list of the columns for the natural key", requiredForDatabase = "all")
	public String getNaturalKey() {
		return naturalKey;
	}

	public void setNaturalKey(String naturalKey) {
		this.naturalKey = naturalKey;
	}

	@DatabaseChangeProperty(description = "Language column", requiredForDatabase = "all")
	public String getLanguageKey() {
		return languageKey;
	}

	public void setLanguageKey(String languageKey) {
		this.languageKey = languageKey;
	}

	@Override
	protected boolean hasPreparedStatementsImplemented() {
		return false;
	}

	@Override
	protected InsertStatement createStatement(String catalogName, String schemaName, String tableName) {
		return new InsertTranslationStatement(
				catalogName,
				schemaName,
				tableName,
				getMainTableName(),
				getSurrogateKey(),
				getNaturalKey(),
				getLanguageKey()
		);
	}

}
