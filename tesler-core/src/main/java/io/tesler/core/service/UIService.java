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

package io.tesler.core.service;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.dto.data.view.BusinessObjectDTO;
import io.tesler.core.dto.data.view.ScreenNavigation;
import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.model.core.entity.User;
import io.tesler.model.ui.entity.BcProperties;
import io.tesler.model.ui.entity.FilterGroup;
import io.tesler.model.ui.entity.Screen;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewWidgets;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;


public interface UIService {

	List<ScreenResponsibility> getCommonScreens();

	JsonNode getUserSettings();

	boolean isCommonScreen(String screenName);

	Map<String, Boolean> getResponsibilities(User user, LOV userRole);

	String getFirstViewFromResponsibilities(User user, LOV userRole, String... views);

	String getFirstViewFromResponsibilities(User user, String... views);

	List<String> getViews(String screenName, User user, LOV userRole);

	Screen findScreenByName(String name);

	ScreenNavigation getScreenNavigation(final Screen screen);

	List<View> getViews(final List<String> views);

	Map<String, List<ViewWidgets>> getAllWidgetsWithPositionByScreen(List<String> views);

	Map<String, List<FilterGroup>> getFilterGroups(BusinessObjectDTO boDto);

	Map<String, BcProperties> getStringDefaultBcPropertiesMap(BusinessObjectDTO boDto);

	void invalidateCache();

}
