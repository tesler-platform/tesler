/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2020 Tesler Contributors
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

package io.tesler.core.service.impl;

import io.tesler.core.config.JacksonConfig;
import io.tesler.core.dto.data.view.ScreenDTO;
import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.core.service.ResponsibilitiesService;
import io.tesler.core.service.UIService;
import io.tesler.core.service.ViewService;
import io.tesler.model.ui.entity.Screen;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({
		ScreenResponsibilityServiceImpl.class,
		JacksonConfig.class
})
public class ScreenResponsibilityServiceImplTest {

	@MockBean
	private ResponsibilitiesService respService;

	@MockBean
	private UIService uiService;

	@MockBean
	private ViewService viewService;

	@InjectMocks
	@Autowired
	private ScreenResponsibilityServiceImpl screenResponsibilityService;

	@Test
	public void getScreens() {
		Screen screenExample = new Screen();
		screenExample.setId(1L);
		ScreenDTO screenMetaExample = new ScreenDTO(screenExample);
		when(respService.getListScreensByUser(null, null)).thenReturn(getRespServiceResponse());
		when(uiService.getCommonScreens()).thenReturn(getUiServiceResponse());
		when(viewService.getScreen(anyString())).thenReturn(screenMetaExample);
		List<ScreenResponsibility> screens = screenResponsibilityService.getScreens(null, null);
		assertThat(screens).isNotNull();
		assertThat(screens.size()).isEqualTo(5);
		assertThat(screens.get(0).getUrl()).isEqualTo("/screen/getting-started");
		assertThat(screens.get(4).getUrl()).isEqualTo("/screen/api-reference");
		assertThat(screens.get(0).getMeta()).isEqualTo(screenMetaExample);
	}

	@Test
	public void getScreensEmpty() {
		when(respService.getListScreensByUser(null, null)).thenReturn("");
		when(uiService.getCommonScreens()).thenReturn(null);
		List<ScreenResponsibility> screens = screenResponsibilityService.getScreens(null, null);
		assertThat(screens).isNotNull();
		assertThat(screens.size()).isEqualTo(0);
	}

	@Test
	public void respServiceInvalidJson() {
		when(respService.getListScreensByUser(null, null)).thenReturn("][");
		List<ScreenResponsibility> result = screenResponsibilityService.getScreens(null, null);
		assertThat(result).isNotNull();
		assertThat(result.isEmpty()).isTrue();
	}

	private String getRespServiceResponse() {
		return "[\n" +
				"    {\n" +
				"      \"name\": \"getting-started\",\n" +
				"      \"text\": \"Getting Started\",\n" +
				"      \"icon\": \"rocket\",\n" +
				"      \"url\": \"/screen/getting-started\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"tutorial\",\n" +
				"      \"text\": \"Tutorial\",\n" +
				"      \"icon\": \"book\",\n" +
				"      \"url\": \"/screen/tutorial\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"components\",\n" +
				"      \"text\": \"Components Overview\",\n" +
				"      \"icon\": \"block\",\n" +
				"      \"url\": \"/screen/components\"\n" +
				"    }\n" +
				"]\"";
	}

	private List<ScreenResponsibility> getUiServiceResponse() {
		ScreenResponsibility featuresScreen = new ScreenResponsibility();
		featuresScreen.setName("features");
		featuresScreen.setText("Features");
		featuresScreen.setIcon("star");
		featuresScreen.setUrl("/screen/features");
		ScreenResponsibility apiScreen = new ScreenResponsibility();
		apiScreen.setName("api-reference");
		apiScreen.setText("API Reference");
		apiScreen.setIcon("api");
		apiScreen.setUrl("/screen/api-reference");
		return Arrays.asList(featuresScreen, apiScreen);
	}
}
