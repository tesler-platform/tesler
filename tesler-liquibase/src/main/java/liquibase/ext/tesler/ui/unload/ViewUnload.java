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

import io.tesler.db.migration.liquibase.data.ViewEntity;
import io.tesler.db.migration.liquibase.data.ViewWidgetRelation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.jvm.JdbcConnection;
import org.apache.commons.lang3.StringUtils;

@DatabaseChange(name = "viewunload", description = "Unload views", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ViewUnload extends AbstractEntityUnload {

	@Override
	protected void unload(JdbcConnection connection) throws Exception {
		ObjectMapper mapper = createMapper();
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM VIEWS");
		try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				ViewEntity viewEntity = new ViewEntity();
				viewEntity.setName(resultSet.getString("NAME"));
				viewEntity.setTitle(resultSet.getString("TITLE"));
				viewEntity.setTemplate(resultSet.getString("TEMPLATE"));
				viewEntity.setUrl(resultSet.getString("URL"));
				viewEntity.setOptions(asJson(mapper, resultSet, "OPTIONS"));

				BigDecimal editable = resultSet.getBigDecimal("EDITABLE");
				if (editable != null && editable.compareTo(new BigDecimal(0)) != 0) {
					viewEntity.setEditable(editable.longValue());
				}

				BigDecimal customizable = resultSet.getBigDecimal("CUSTOMIZABLE");
				if (customizable != null && customizable.compareTo(new BigDecimal(0)) != 0) {
					viewEntity.setCustomizable(customizable.longValue());
				}

				List<ViewWidgetRelation> widgets = getWidgets(connection, viewEntity);
				if (widgets != null && !widgets.isEmpty()) {
					viewEntity.setWidgets(widgets);
				}

				String screen = viewEntity.getUrl().split("/")[2];

				File destination = buildDirectory("screens", screen, "views");

				try (FileOutputStream fos = new FileOutputStream(
						new File(destination, String.format("%s.view.json", viewEntity.getName())))) {
					mapper.writeValue(fos, viewEntity);
				}
			}
		}
	}

	private List<ViewWidgetRelation> getWidgets(JdbcConnection connection, ViewEntity viewEntity) throws Exception {
		List<ViewWidgetRelation> result = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT VW.*, W.NAME AS WIDGET_NAME FROM VIEW_WIDGETS VW, WIDGET W "
						+ "WHERE W.ID=VW.WIDGET_ID AND VIEW_NAME = ? ORDER BY VW.POSITON")) {
			statement.setString(1, viewEntity.getName());
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					ViewWidgetRelation widget = new ViewWidgetRelation();
					widget.setWidgetName(resultSet.getString("WIDGET_NAME"));
					if (StringUtils.isBlank(widget.getWidgetName())) {
						widget.setWidgetId(resultSet.getLong("WIDGET_ID"));
					}
					widget.setGridWidth(resultSet.getLong("GRID_WIDTH"));
					widget.setPosition(resultSet.getLong("POSITON"));
					widget.setDescriptionTitle(resultSet.getString("DESCRIPTION_TITLE"));
					String description = resultSet.getString("DESCRIPTION");
					String snippet = resultSet.getString("SNIPPET");

					BigDecimal gridBreak = resultSet.getBigDecimal("GRID_BREAK");
					if (gridBreak != null && gridBreak.compareTo(new BigDecimal(0)) != 0) {
						widget.setGridBreak(gridBreak.longValue());
					}

					BigDecimal hideByDefault = resultSet.getBigDecimal("HIDE_BY_DEFAULT");
					if (hideByDefault != null && hideByDefault.compareTo(new BigDecimal(0)) != 0) {
						widget.setHideByDefault(hideByDefault.longValue());
					}

					BigDecimal showExportStamp = resultSet.getBigDecimal("SHOW_EXPORT_STAMP");
					if (showExportStamp != null && showExportStamp.compareTo(new BigDecimal(0)) != 0) {
						widget.setShowExportStamp(showExportStamp.longValue());
					}

					BigDecimal pageLimit = resultSet.getBigDecimal("PAGE_LIMIT");
					if (pageLimit != null) {
						widget.setPageLimit(pageLimit.longValue());
					}

					String screen = viewEntity.getUrl().split("/")[2];

					File destination = buildDirectory("screens", screen, "views");

					String prefix;
					if (StringUtils.isNotBlank(widget.getWidgetName())) {
						prefix = widget.getWidgetName();
					} else {
						prefix = String.format("%06d", widget.getId());
					}

					if (description != null) {
						File f = new File(destination, "descriptions");
						f.mkdirs();

						try (FileOutputStream fos = new FileOutputStream(new File(f, String
								.format("%s-%s.description.txt", viewEntity.getName(), prefix)))) {
							fos.write(description.getBytes());
						}
						widget.setDescriptionFile(
								String.format("%s/screens/%s/views/descriptions/%s-%s.description.txt", RELATIVE_PATH,
										screen, viewEntity.getName(), prefix
								));
					}

					if (snippet != null) {
						File f = new File(destination, "snippets");
						f.mkdirs();

						try (FileOutputStream fos = new FileOutputStream(new File(
								f,
								String.format("%s-%s.snippet.txt", viewEntity.getName(), prefix)
						))) {
							fos.write(snippet.getBytes());
						}
						widget.setSnippetFile(String.format("%s/screens/%s/views/snippets/%s-%s.snippet.txt",
								RELATIVE_PATH, screen, viewEntity.getName(), prefix
						));
					}

					result.add(widget);
				}
			}
		}
		return result;
	}

}
