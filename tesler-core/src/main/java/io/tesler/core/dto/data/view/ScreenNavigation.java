/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.dto.data.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tesler.api.data.dto.LocaleAware;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The navigation object that is transmitted to the client side to create the menu structure
 */
@Getter
@Setter
public final class ScreenNavigation {

	private List<MenuItem> menu;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public abstract static class MenuItem {

		private String id;

		/**
		 * Commentary for developers
		 */
		@JsonProperty(value = "CommentDevelop")
		private String commentDevelop;

		/**
		 * Indicates that the navigation element not showed in
		 * navigation tabs.
		 */
		private boolean hidden;

		@JsonIgnore
		private Integer seq;

	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ViewGroup extends MenuItem {

		/**
		 * Title of category in navigation tabs.
		 */
		@LocaleAware
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

	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SingleView extends MenuItem {

		/**
		 * Unique name of view refers to the name of view.json file.
		 * Title of view in navigation tabs specified in “title” field in view.json file.
		 */
		private String viewName;

	}

}
