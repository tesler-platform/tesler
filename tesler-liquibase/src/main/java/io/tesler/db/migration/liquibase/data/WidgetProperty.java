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
@DBEntity(tableName = "WIDGET_PROPERTY", primaryKey = "WIDGET_ID")
public class WidgetProperty extends AbstractEntity {

	@DBField(columnName = "WIDGET_ID", functionField = "widgetIdFunction")
	private Long widgetId;

	private String widgetName;

	@DBField(columnName = "IS_CONCLUSION_TYPE")
	private boolean conclusionType;

	public String getIdSequence() {
		String sequence = super.getIdSequence();
		if (StringUtils.isBlank(sequence)) {
			sequence = "APP_SEQ";
		}
		return sequence;
	}

	public String getWidgetIdFunction() {
		if (StringUtils.isNotBlank(getWidgetName())) {
			return "(SELECT ID FROM WIDGET WHERE NAME='" + widgetName + "')";
		}
		throw new IllegalStateException("Widget name is empty");
	}

}
