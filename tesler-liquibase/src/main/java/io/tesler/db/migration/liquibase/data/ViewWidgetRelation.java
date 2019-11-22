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

package io.tesler.db.migration.liquibase.data;

import io.tesler.db.migration.liquibase.annotations.DBEntity;
import io.tesler.db.migration.liquibase.annotations.DBField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@DBEntity(tableName = "VIEW_WIDGETS")
public class ViewWidgetRelation extends AbstractEntity {

	@DBField(columnName = "VIEW_NAME")
	private String viewName;

	@DBField(columnName = "WIDGET_ID", functionField = "widgetIdFunction")
	private Long widgetId;

	private String widgetName;

	@DBField(columnName = "POSITON", insertNulls = true)
	private Long position;

	@DBField(columnName = "PAGE_LIMIT", insertNulls = true)
	private Long pageLimit;

	@DBField(columnName = "GRID_WIDTH")
	private Long gridWidth;

	@DBField(columnName = "GRID_BREAK")
	private Long gridBreak;

	@DBField(columnName = "HIDE_BY_DEFAULT")
	private Long hideByDefault;

	@DBField(columnName = "SHOW_EXPORT_STAMP")
	private Long showExportStamp;

	@DBField(columnName = "DESCRIPTION_TITLE", insertNulls = true)
	private String descriptionTitle;

	@DBField(columnName = "DESCRIPTION", fileField = "descriptionFile", insertNulls = true)
	private String description;

	private String descriptionFile;

	@DBField(columnName = "SNIPPET", fileField = "snippetFile", insertNulls = true)
	private String snippet;

	private String snippetFile;

	public String getWidgetIdFunction() {
		if (StringUtils.isNotBlank(getWidgetName())) {
			return "(SELECT ID FROM WIDGET WHERE NAME='" + widgetName + "')";
		}
		throw new IllegalStateException("Widget name is empty");
	}

}
