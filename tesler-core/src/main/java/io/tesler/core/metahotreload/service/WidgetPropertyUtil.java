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

import static java.util.Optional.ofNullable;

import io.tesler.core.metahotreload.dto.WidgetSourceDTO;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.Widget;
import io.tesler.model.ui.entity.WidgetProperty;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WidgetPropertyUtil {

	private final JpaDao jpaDao;

	public void process(
			@NotNull List<WidgetSourceDTO> widgetDtos,
			@NotNull Map<String, Widget> nameToWidget) {
		widgetDtos.stream()
				.map(widgetDto -> mapToWidgetProperty(widgetDto, nameToWidget.get(widgetDto.getName())))
				.forEach(jpaDao::save);
	}

	@NotNull
	private static WidgetProperty mapToWidgetProperty(@NotNull WidgetSourceDTO dto, @NotNull Widget widget) {
		return new WidgetProperty()
				.setWidget(widget)
				.setIsConclusionType(ofNullable(dto.getIsConclusionWidget()).orElse(false));
	}
}
