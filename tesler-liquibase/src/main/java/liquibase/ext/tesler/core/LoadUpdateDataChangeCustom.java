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
import liquibase.change.core.LoadUpdateDataChange;

@DatabaseChange(name = "loadUpdateData", description = "Loads or updates data from a CSV file into an existing table", priority = Integer.MAX_VALUE)
public class LoadUpdateDataChangeCustom extends LoadUpdateDataChange {

	public LoadUpdateDataChangeCustom() {
		super();
	}

}
