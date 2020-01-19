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

@Getter
@Setter
@DBEntity(tableName = "NAVIGATION_GROUP")
public class NavigationGroup extends LqbAbstractEntity {

	@JsonInclude(Include.NON_NULL)
	@DBField(columnName = "ID")
	private String id;

	/**
	 * Type of group
	 */
	@DBField(columnName = "TYPE_CD")
	private String typeCd;


	/**
	 * Name of screen, where is group located
	 */
	@DBField(columnName = "SCREEN_NAME")
	private String screenName;

	/**
	 * Title of group. Navigation element shows it to user.
	 */
	@DBField(columnName = "TITLE")
	private String title;

	/**
	 * since navigation is a tree, groups can be nested to each other
	 */
	@DBField(columnName = "PARENT_ID")
	private String parentId;

	/**
	 * Sequence of group in a parent element
	 */
	@DBField(columnName = "SEQ")
	private Integer seq;

	/**
	 * description for developers
	 */
	@DBField(columnName = "DESCRIPTION")
	private String description;

	/**
	 * default view, that opens when user click on group;
	 */
	@DBField(columnName = "DEFAULT_VIEW")
	private String defaultView;

	/**
	 * is group hidden on navigation bars
	 */
	@DBField(columnName = "HIDDEN")
	private Integer hidden;

}

