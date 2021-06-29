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
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewSourceDTO {

	private String name;

	private String title;

	private String template;

	private String url;

	private Boolean customizable;

	private Boolean editable;

	private Boolean ignoreHistory;

	private JsonNode options;

	private List<ViewWidgetSourceDTO> widgets;

	@Getter
	@Setter
	@EqualsAndHashCode(of = "widgetName")
	public static class ViewWidgetSourceDTO {

		//specify only "widgetName" instead
		@Deprecated
		String widgetId;

		private String widgetName;

		private Long position;

		private Long pageLimit;

		private Long gridWidth;

		private Long gridBreak;

		private Boolean hideByDefault;

		private Boolean showExportStamp;

		private String descriptionTitle;

		private String description;

		private String descriptionFile;

		private String snippet;

		private String snippetFile;

		public String getWidgetNaturalKey() {
			return Optional.ofNullable(this.widgetId).orElse(this.widgetName);
		}
	}

}
