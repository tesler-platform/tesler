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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.tesler.db.migration.liquibase.annotations.DBEntity;
import io.tesler.db.migration.liquibase.annotations.DBField;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@DBEntity(tableName = "SCREEN", primaryKey = "NAME")
public class ScreenEntity extends LqbBaseEntity {

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

		private List<MenuItem> menu;

		@Getter
		@Setter
		@JsonPropertyOrder({"CommentDevelop"})
		@JsonInclude(Include.NON_NULL)
		public static class MenuItem {

			/**
			 * Commentary for developers
			 */
			private String commentDevelop;

			/**
			 * Indicates that the navigation element not showed in
			 * navigation tabs.
			 */
			private boolean hidden;

			/**
			 * Title of category in navigation tabs.
			 */
			private String title;

			/**
			 * name of view, which is located below group (in child element or lower)
			 * If specified, click on group in navigation tab redirects on view with following name.
			 * If not specified, click on group in navigation tab redirects on first view, which is found using
			 * the breadth-first search algorithm
			 */
			private String defaultView;

			/**
			 * Array of navigation elements specified below group(View or inner Group)
			 */
			private List<MenuItem> child;


			/**
			 * Unique name of view refers to the name of view.json file.
			 * Title of view in navigation tabs specified in “title” field in view.json file.
			 */
			private String viewName;

		}

	}

}
