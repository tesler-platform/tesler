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

import io.tesler.api.data.dto.LocaleAware;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ScreenNavigation {

	private List<Menu> menu;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Menu {

		@JsonProperty(value = "CommentDevelop")
		private String commentDevelop;

		@LocaleAware
		private String title;

		@LocaleAware
		private String categoryName;

		private List<SubMenu> child;

	}

	@Getter
	@Setter
	public static class SubMenu extends Menu {

		private String viewName;

	}

}
