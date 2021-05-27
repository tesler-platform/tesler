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

	String name;

	String title;

	String primaryViewName;

	JsonNode primaryViews;

	ScreenNavigationSourceDto navigation;

	@Getter
	@Setter
	public static class ScreenNavigationSourceDto {

		List<MenuItemSourceDto> menu;

		@Getter
		@Setter
		public static class MenuItemSourceDto {

			Boolean hidden;

			String title;

			String defaultView;

			List<MenuItemSourceDto> child;

			String viewName;

		}

	}

}
