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

import io.tesler.db.migration.liquibase.data.ViewEntity;
import io.tesler.db.migration.liquibase.data.ViewWidgetRelation;
import java.util.ArrayList;
import java.util.List;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DeleteStatement;

@DatabaseChange(name = "viewload", description = "Create view", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ViewChange extends AbstractEntityChange<ViewEntity> {

	@Override
	protected Class<ViewEntity> getElementType() {
		return ViewEntity.class;
	}

	@Override
	protected List<SqlStatement> generateStatements(
			Database database,
			ResourceAccessor resourceAccessor, ViewEntity entity
	) throws Exception {
		// удаляем связи с widget прежде чем вставлять
		DeleteStatement delete = new DeleteStatement(null, null, "VIEW_WIDGETS");
		delete.setWhere("VIEW_NAME = ?");
		delete.addWhereParameter(entity.getName());
		List<SqlStatement> result = new ArrayList<>();
		result.add(delete);
		result.addAll(super.generateStatements(database, resourceAccessor, entity));
		AbstractEntityChange<ViewWidgetRelation> widgetChange = getEntityChange(
				database,
				resourceAccessor,
				ViewWidgetRelation.class
		);
		if (entity.getWidgets() != null) {
			for (ViewWidgetRelation widget : entity.getWidgets()) {
				widget.setViewName(entity.getName());
				result.addAll(widgetChange.generateStatements(database, resourceAccessor, widget));
			}
		}
		return result;
	}

}
