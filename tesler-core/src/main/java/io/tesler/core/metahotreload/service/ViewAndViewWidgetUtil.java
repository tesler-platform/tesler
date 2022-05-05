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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties;
import io.tesler.core.metahotreload.dto.ViewSourceDTO;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgetsPK;
import io.tesler.model.ui.entity.Widget;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewAndViewWidgetUtil {

	private final JpaDao jpaDao;

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper objMapper;

	final ApplicationContext applicationContext;

	final MetaConfigurationProperties config;

	public void process(
			@NonNull List<ViewSourceDTO> viewDtos,
			@NonNull Map<String, Widget> nameToWidget) {
		viewDtos.forEach(viewDto -> {
			jpaDao.save(mapToView(objMapper, viewDto));
			if (viewDto.getWidgets() != null) {
				viewDto.getWidgets().forEach(viewWidgetDto -> {
					Widget widget = nameToWidget.get(viewWidgetDto.getWidgetNaturalKey());
					String viewName = viewDto.getName();
					jpaDao.save(mapToViewWidget(viewName, widget, viewWidgetDto));
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
	private ViewWidgets mapToViewWidget(
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
				.setDescription(getWidgetDescription(dto))
				.setSnippet(dto.getSnippet())
				.setLimit(dto.getPageLimit())
				.setGridWidth(ofNullable(dto.getGridWidth()).orElse(1L))
				.setGridBreak(ofNullable(dto.getGridBreak()).orElse(0L))
				.setHide(ofNullable(dto.getHideByDefault()).orElse(false))
				.setShowExportStamp(ofNullable(dto.getShowExportStamp()).orElse(false));
	}

	@SneakyThrows
	private String getWidgetDescription(ViewSourceDTO.ViewWidgetSourceDTO dto) {
		if (dto.getDescription() != null && !Objects.equals(dto.getDescription(), "")) {
			return dto.getDescription();
		}
		if (dto.getDescriptionFile() != null && !Objects.equals(dto.getDescriptionFile(), "")) {
			Resource resource = applicationContext.getResource(config.getDirectory() + dto.getDescriptionFile());
			return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
		}
		return null;
	}

}
