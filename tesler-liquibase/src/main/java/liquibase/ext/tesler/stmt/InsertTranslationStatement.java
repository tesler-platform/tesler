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

import java.util.Arrays;
import java.util.List;
import liquibase.statement.core.InsertStatement;


public class InsertTranslationStatement extends InsertStatement {

	private final String mainTableName;

	private final String surrogateKey;

	private final List<String> naturalKey;

	private final String languageKey;

	public InsertTranslationStatement(String catalogName, String schemaName, String tableName, String mainTableName,
			String surrogateKey, String naturalKey, String languageKey) {
		super(catalogName, schemaName, tableName);
		this.mainTableName = mainTableName;
		this.surrogateKey = surrogateKey;
		this.naturalKey = Arrays.asList(naturalKey.split(","));
		this.languageKey = languageKey;
	}

	public String getMainTableName() {
		return mainTableName;
	}

	public String getSurrogateKey() {
		return surrogateKey;
	}

	public List<String> getNaturalKey() {
		return naturalKey;
	}

	public String getLanguageKey() {
		return languageKey;
	}

}
