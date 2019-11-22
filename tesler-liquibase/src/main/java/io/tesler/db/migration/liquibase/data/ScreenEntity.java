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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@DBEntity(tableName = "SCREEN", primaryKey = "NAME")
public class ScreenEntity extends AbstractEntity {

	@DBField(columnName = "NAME", insertNulls = true)
	private String name;

	@DBField(columnName = "TITLE", insertNulls = true)
	private String title;

	@DBField(columnName = "PRIMARY_VIEW_NAME", insertNulls = true)
	private String primaryViewName;

	@DBField(columnName = "PRIMARY_VIEWS")
	private JsonNode primaryViews;

	private ScreenNavigation navigation;

	public String getIdSequence() {
		String sequence = super.getIdSequence();
		if (StringUtils.isBlank(sequence)) {
			sequence = "APP_SEQ";
		}
		return sequence;
	}

	@Getter
	@Setter
	public static class ScreenNavigation {

		private List<Menu> menu;

		@Getter
		@Setter
		@JsonPropertyOrder({"id", "CommentDevelop"})
		public static class Menu {

			private Long id;

			@JsonProperty(value = "CommentDevelop")
			private String commentDevelop;

			private String title;

			private String categoryName;

			private List<SubMenu> child;

		}

		@Getter
		@Setter
		public static class SubMenu extends Menu {

			private String viewName;

		}

	}

}
