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

import io.tesler.db.migration.liquibase.data.WidgetEntity;
import io.tesler.db.migration.liquibase.data.WidgetProperty;
import java.util.List;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import org.apache.commons.lang3.BooleanUtils;

@DatabaseChange(name = "widgetload", description = "Create widget", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class WidgetChange extends AbstractEntityChange<WidgetEntity> {

	@Override
	protected Class<WidgetEntity> getElementType() {
		return WidgetEntity.class;
	}

	@Override
	protected List<SqlStatement> generateStatements(
			Database database,
			ResourceAccessor resourceAccessor,
			WidgetEntity entity
	) throws Exception {
		List<SqlStatement> resultStatements = super.generateStatements(database, resourceAccessor, entity);
		AbstractEntityChange<WidgetProperty> widgetChange = getEntityChange(
				database,
				resourceAccessor,
				WidgetProperty.class
		);
		WidgetProperty widgetProperty = new WidgetProperty();
		widgetProperty.setConclusionType(BooleanUtils.toBooleanDefaultIfNull(entity.getIsConclusionWidget(), false));
		widgetProperty.setWidgetId(entity.getId());
		widgetProperty.setWidgetName(entity.getName());
		resultStatements.addAll(widgetChange.generateStatements(database, resourceAccessor, widgetProperty));
		return resultStatements;
	}

}
