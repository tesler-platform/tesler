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
import static io.tesler.core.metahotreload.util.JsonUtils.serializeOrElseNull;

import io.tesler.core.metahotreload.dto.WidgetSourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.model.ui.entity.Widget;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import javax.validation.constraints.NotNull;

@UtilityClass
public class WidgetUtil {

	@NotNull
	static Map<String, Widget> process(
			@NonNull List<WidgetSourceDTO> widgetDtos,
			@NonNull EntityManager entityManager,
			@NonNull ObjectMapper objMapper) {
		Map<String, Widget> nameToWidget = widgetDtos.stream()
				.map(widgetDto -> mapToWidget(objMapper, widgetDto))
				.collect(Collectors.toMap(Widget::getName, widget -> widget));

		nameToWidget.forEach((name, widget) -> entityManager.persist(widget));
		return nameToWidget;
	}

	@NonNull
	private static Widget mapToWidget(@NonNull ObjectMapper objectMapper, @NonNull WidgetSourceDTO dto) {
		return new Widget()
				.setName(dto.getName())
				.setType(dto.getType())
				.setBc(dto.getBc())
				.setTitle(dto.getTitle())
				.setFields(serializeOrElseEmptyArr(objectMapper, dto.getFields()))
				.setOptions(serializeOrElseEmptyArr(objectMapper, dto.getOptions()))
				.setPivotFields(serializeOrElseNull(objectMapper, dto.getPivotFields()))
				.setAxisFields(serializeOrElseEmptyArr(objectMapper, dto.getAxisFields()))
				.setShowCondition(serializeOrElseEmptyArr(objectMapper, dto.getShowCondition()))
				.setChart(serializeOrElseEmptyArr(objectMapper, dto.getChart()))
				.setGraph(serializeOrElseNull(objectMapper, dto.getGraph()));
	}
}
