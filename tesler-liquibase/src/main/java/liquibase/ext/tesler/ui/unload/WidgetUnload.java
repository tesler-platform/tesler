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

package liquibase.ext.tesler.ui.unload;

import io.tesler.db.migration.liquibase.data.WidgetEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.jvm.JdbcConnection;
import org.apache.commons.lang3.StringUtils;

@DatabaseChange(name = "widgetunload", description = "Unload widgets", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class WidgetUnload extends AbstractEntityUnload {

	@Override
	protected void unload(JdbcConnection connection) throws Exception {
		ObjectMapper mapper = createMapper();
		try (PreparedStatement statement = connection
				.prepareStatement("SELECT WIDGET.*, BC.ID AS BC_ID FROM WIDGET, BC WHERE WIDGET.BC=BC.NAME(+)")) {
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					WidgetEntity widgetEntity = new WidgetEntity();
					widgetEntity.setName(resultSet.getString("NAME"));
					if (StringUtils.isBlank(widgetEntity.getName())) {
						widgetEntity.setId(resultSet.getLong("ID"));
					}
					widgetEntity.setBc(resultSet.getString("BC"));
					widgetEntity.setType(resultSet.getString("TYPE"));
					widgetEntity.setTitle(resultSet.getString("TITLE"));
					widgetEntity.setTemplate(resultSet.getString("TEMPLATE"));
					widgetEntity.setAxisFields(asJson(mapper, resultSet, "AXIS_FIELDS"));
					widgetEntity.setChart(asJson(mapper, resultSet, "CHART"));
					widgetEntity.setFields(asJson(mapper, resultSet, "FIELDS"));
					widgetEntity.setGraph(asJson(mapper, resultSet, "GRAPH"));
					widgetEntity.setOptions(asJson(mapper, resultSet, "OPTIONS"));
					widgetEntity.setPivotFields(asJson(mapper, resultSet, "PIVOT_FIELDS"));
					widgetEntity.setShowCondition(asJson(mapper, resultSet, "SHOW_CONDITION"));

					File destination;
					if (resultSet.getBigDecimal("BC_ID") != null) {
						destination = buildDirectory("sqlbc", widgetEntity.getBc());
					} else {
						destination = buildDirectory("widgets");
					}

					String fileName;
					if (StringUtils.isNotBlank(widgetEntity.getName())) {
						fileName = String.format("%s.widget.json", widgetEntity.getName());
					} else {
						fileName = String.format("%06d.widget.json", widgetEntity.getId());
					}

					try (FileOutputStream fos = new FileOutputStream(new File(destination, fileName))) {
						mapper.writeValue(fos, widgetEntity);
					}
				}
			}
		}
	}

}
