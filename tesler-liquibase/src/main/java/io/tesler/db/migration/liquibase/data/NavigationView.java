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
import lombok.Getter;
import lombok.Setter;

/**
 * Entity that represent views in navigation tree.
 * Clients side of tesler framework uses this to create navigation elements such as tabs or menus
 */
@Getter
@Setter
@DBEntity(tableName = "NAVIGATION_VIEW")
public class NavigationView extends LqbAbstractEntity {

	@JsonInclude(Include.NON_NULL)
	@DBField(columnName = "ID")
	private String id;

	/**
	 * Type of view
	 */
	@DBField(columnName = "TYPE_CD")
	private String typeCd;

	/**
	 * name of view refers to the name of VIEWS table
	 */
	@DBField(columnName = "VIEW_NAME")
	private String viewName;

	/**
	 * Name of screen, where is view located
	 */
	@DBField(columnName = "SCREEN_NAME")
	private String screenName;

	/**
	 * Parent group id. Can be null, cause view may be on first level of navigation tree
	 */
	@DBField(columnName = "PARENT_GROUP_ID")
	private String parentGroupId;

	/**
	 * Sequence of view in a parent element
	 */
	@DBField(columnName = "SEQ")
	private Integer seq;

	/**
	 * description for developers
	 */
	@DBField(columnName = "DESCRIPTION")
	private String description;

	/**
	 * is view hidden on navigation bars
	 */
	@DBField(columnName = "HIDDEN")
	private Integer hidden;

}
