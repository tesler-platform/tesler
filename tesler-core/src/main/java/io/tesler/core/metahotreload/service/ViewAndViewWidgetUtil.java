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

package io.tesler.core.metahotreload.service;

import static io.tesler.core.metahotreload.util.JsonUtils.serializeOrElseEmptyArr;
import static java.util.Optional.ofNullable;

import io.tesler.core.metahotreload.dto.ViewSourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgetsPK;
import io.tesler.model.ui.entity.Widget;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ViewAndViewWidgetUtil {

	static void process(
			@NonNull List<ViewSourceDTO> viewDtos,
			@NonNull Map<String, Widget> nameToWidget,
			@NonNull EntityManager entityManager,
			@NonNull ObjectMapper objMapper) {
		viewDtos.forEach(viewDto -> {
			entityManager.persist(mapToView(objMapper, viewDto));
			if (viewDto.getWidgets() != null) {
				viewDto.getWidgets().forEach(viewWidgetDto -> {
					Widget widget = nameToWidget.get(viewWidgetDto.getWidgetName());
					String viewName = viewDto.getName();
					entityManager.persist(mapToViewWidget(viewName, widget, viewWidgetDto));
				});
			}
		});
	}

	@NonNull
	private static View mapToView(@NonNull ObjectMapper objectMapper, @NonNull ViewSourceDTO dto) {
		return new View()
				.setName(dto.getName())
				.setTemplate(dto.getTemplate())
				.setTitle(dto.getTitle())
				.setUrl(dto.getUrl())
				.setCustomizable(ofNullable(dto.getCustomizable()).orElse(false))
				.setEditable(ofNullable(dto.getEditable()).orElse(false))
				.setIgnoreHistory(ofNullable(dto.getIgnoreHistory()).orElse(false))
				.setOptions(serializeOrElseEmptyArr(objectMapper, dto.getOptions()));
	}

	@NonNull
	private static ViewWidgets mapToViewWidget(
			@NonNull String viewName,
			@NonNull Widget widget,
			@NonNull ViewSourceDTO.ViewWidgetSourceDTO dto) {
		return new ViewWidgets()
				.setPk(new ViewWidgetsPK()
						.setViewName(viewName)
						.setWidgetId(widget.getId()))
				.setViewName(viewName)
				.setWidget(widget)
				.setPositon(dto.getPosition())
				.setDescriptionTitle(dto.getDescriptionTitle())
				.setDescription(dto.getDescription())
				.setSnippet(dto.getSnippet())
				.setLimit(dto.getPageLimit())
				.setGridWidth(ofNullable(dto.getGridWidth()).orElse(1L))
				.setGridBreak(ofNullable(dto.getGridBreak()).orElse(0L))
				.setHide(ofNullable(dto.getHideByDefault()).orElse(false))
				.setShowExportStamp(ofNullable(dto.getShowExportStamp()).orElse(false));
	}

}
