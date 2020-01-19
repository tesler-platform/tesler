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
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@DBEntity(tableName = "VIEWS", primaryKey = "NAME")
public class ViewEntity extends LqbBaseEntity {

	@DBField(columnName = "NAME", insertNulls = true)
	private String name;

	@DBField(columnName = "TITLE", insertNulls = true)
	private String title;

	@DBField(columnName = "TEMPLATE", insertNulls = true)
	private String template;

	@DBField(columnName = "URL", functionField = "urlFunc", insertNulls = true)
	private String url;

	@DBField(columnName = "CUSTOMIZABLE")
	private Long customizable;

	@DBField(columnName = "EDITABLE")
	private Long editable;

	@DBField(columnName = "OPTIONS", insertNulls = true)
	private JsonNode options;

	private List<ViewWidgetRelation> widgets;

	public String getIdSequence() {
		String sequence = super.getIdSequence();
		if (StringUtils.isBlank(sequence)) {
			sequence = "APP_SEQ";
		}
		return sequence;
	}

}
