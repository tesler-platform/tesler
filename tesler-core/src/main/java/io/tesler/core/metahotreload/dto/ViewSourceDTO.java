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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewSourceDTO {

	String name;

	String title;

	String template;

	String url;

	Boolean customizable;

	Boolean editable;

	Boolean ignoreHistory;

	JsonNode options;

	List<ViewWidgetSourceDTO> widgets;

	@Getter
	@Setter
	@EqualsAndHashCode(of = "widgetName")
	public static class ViewWidgetSourceDTO {

		String widgetName;

		Long position;

		Long pageLimit;

		Long gridWidth;

		Long gridBreak;

		Boolean hideByDefault;

		Boolean showExportStamp;

		String descriptionTitle;

		String description;

		String descriptionFile;

		String snippet;

		String snippetFile;

	}

}
