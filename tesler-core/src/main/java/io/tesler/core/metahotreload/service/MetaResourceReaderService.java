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

import io.tesler.core.metahotreload.conf.properties.MetaConfigurationProperties;
import io.tesler.core.metahotreload.dto.ScreenSourceDto;
import io.tesler.core.metahotreload.dto.ViewSourceDTO;
import io.tesler.core.metahotreload.dto.WidgetSourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
public class MetaResourceReaderService {

	final ApplicationContext applicationContext;

	final MetaConfigurationProperties config;

	final ObjectMapper objMapper;

	@NonNull
	public List<ScreenSourceDto> getScreens() {
		return readFilesToDto(ScreenSourceDto.class, config.getDirectory() + config.getScreenPath());
	}

	@NonNull
	public List<ViewSourceDTO> getViews() {
		return readFilesToDto(ViewSourceDTO.class, config.getDirectory() + config.getViewPath());
	}

	@NonNull
	public List<WidgetSourceDTO> getWidgets() {
		return readFilesToDto(WidgetSourceDTO.class, config.getDirectory() + config.getWidgetPath());
	}

	@NonNull
	@SneakyThrows
	private <T> List<T> readFilesToDto(@NonNull Class<T> clazz, @NonNull String locationPattern) {
		return Arrays.stream(applicationContext.getResources(locationPattern))
				.map(resource -> readDto(resource, clazz))
				.collect(Collectors.toList());
	}

	@NonNull
	@SneakyThrows
	private <T> T readDto(@NonNull Resource resource, @NonNull Class<T> valueType) {
		return objMapper.readValue(new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)), valueType);
	}
}
