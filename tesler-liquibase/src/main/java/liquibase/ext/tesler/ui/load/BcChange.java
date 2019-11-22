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

package liquibase.ext.tesler.ui.load;

import io.tesler.db.migration.liquibase.data.BcEntity;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;

@DatabaseChange(name = "bcload", description = "Create bc", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class BcChange extends AbstractEntityChange<BcEntity> {

	@Override
	protected Class<BcEntity> getElementType() {
		return BcEntity.class;
	}

}
