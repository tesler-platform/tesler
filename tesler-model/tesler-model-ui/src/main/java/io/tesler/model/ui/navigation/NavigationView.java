/*-
 * #%L
 * IO Tesler - Model UI
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

package io.tesler.model.ui.navigation;

import io.tesler.api.data.dictionary.LOV;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity that represent views in navigation tree.
 * Clients side of tesler framework uses this to create navigation elements such as tabs or menus
 */
@Getter
@Setter
@Entity(name = "NAVIGATION_VIEW")
public class NavigationView {

	/**
	 * Identifier in navigation tree. Also primary key
	 */
	@Id
	@Column(name = "ID")
	protected String id;

	/**
	 * Type of group
	 */
	@Column(name = "TYPE_CD")
	private LOV typeCd;

	/**
	 * name of view refers to the name of VIEWS table
	 */
	@Column(name = "VIEW_NAME")
	private String viewName;

	/**
	 * Name of screen, where is view located
	 */
	@Column(name = "SCREEN_NAME")
	private String screenName;

	/**
	 * Parent group id. Can be null, cause view may be on first level of navigation tree
	 */
	@ManyToOne
	@JoinColumn(name = "PARENT_GROUP_ID")
	private NavigationGroup parentGroup;

	/**
	 * Sequence of view in a parent element
	 */
	@Column(name = "SEQ")
	private Integer seq;

	/**
	 * description for developers
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	/**
	 * is view hidden on navigation bars
	 */
	@Column(name = "HIDDEN")
	private Boolean hidden;

}
