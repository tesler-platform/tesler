///*-
// * #%L
// * IO Tesler - Core
// * %%
// * Copyright (C) 2018 - 2020 Tesler Contributors
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//package io.tesler.core.controller;
//
//import io.tesler.api.service.session.CoreSessionService;
//import io.tesler.core.dto.data.view.ScreenResponsibility;
//import io.tesler.core.exception.ExceptionHandlerSettings;
//import io.tesler.core.service.ScreenResponsibilityService;
//import io.tesler.core.util.session.SessionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import java.util.Arrays;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ContextConfiguration(classes = CoreSessionService.class)
//@WebMvcTest(ScreenController.class)
//public class ScreenControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	private ScreenResponsibilityService screenResponsibilityService;
//
//	@MockBean
//	private SessionService sessionService;
//
//	@MockBean
//	ExceptionHandlerSettings exceptionHandlerSettingsæ;
//
//	private final String expectedResponse = "[{\"id\":null,\"name\":\"screen1\",\"text\":null,\"url\":null,\"icon\":null,\"defaultScreen\":false,\"meta\":null},{\"id\":null,\"name\":\"screen2\",\"text\":null,\"url\":null,\"icon\":null,\"defaultScreen\":false,\"meta\":null}]";
//
//	@Test
//	@WithMockUser(username = "vanilla", password = "vanilla")
//	@SneakyThrows
//	void controllerExists() {
//		this.mockMvc.perform(get("/screens")).andExpect(status().isOk());
//	}
//
//	@Test
//	@WithMockUser(username = "vanilla", password = "vanilla")
//	@SneakyThrows
//	void controllerCallsService() {
//		ScreenResponsibility screen1 = new ScreenResponsibility();
//		ScreenResponsibility screen2 = new ScreenResponsibility();
//		screen1.setName("screen1");
//		screen2.setName("screen2");
//		when(screenResponsibilityService.getScreens(null, null)).thenReturn(Arrays.asList(screen1, screen2));
//		this.mockMvc.perform(get("/screens"))
//				.andDo(print())
//				.andExpect(
//						content().json(expectedResponse)
//				);
//	}
//}
