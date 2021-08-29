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

package io.tesler.core.metahotreload.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreenSourceDto {

	/**
	 * used only if tesler.meta.view-allowed-roles-enabled=true (see {@link io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties#viewAllowedRolesEnabled})
	 */
	private int order = 0;

	/**
	 * used only if tesler.meta.view-allowed-roles-enabled=true (see {@link io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties#viewAllowedRolesEnabled})
	 */
	private String icon;

	private String name;

	/**
	 * used only if tesler.meta.view-allowed-roles-enabled=true (see {@link io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties#viewAllowedRolesEnabled})
	 */
	private String title;

	private String primaryViewName;

	private JsonNode primaryViews;

	private ScreenNavigationSourceDto navigation;

	@Getter
	@Setter
	public static class ScreenNavigationSourceDto {

		private List<MenuItemSourceDto> menu;

		@Getter
		@Setter
		public static class MenuItemSourceDto {

			private Boolean hidden;

			private String title;

			private String defaultView;

			private List<MenuItemSourceDto> child;

			private String viewName;

		}

	}

}
