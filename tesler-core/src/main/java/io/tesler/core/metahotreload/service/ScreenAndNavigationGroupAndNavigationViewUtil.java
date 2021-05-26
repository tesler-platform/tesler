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

import static io.tesler.core.metahotreload.util.JsonUtils.serializeOrElseNull;
import static java.util.Optional.ofNullable;

import io.tesler.core.metahotreload.dto.ScreenSourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.model.ui.entity.Screen;
import io.tesler.model.ui.navigation.NavigationGroup;
import io.tesler.model.ui.navigation.NavigationView;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScreenAndNavigationGroupAndNavigationViewUtil {

	static void process(
			@NonNull List<ScreenSourceDto> screenDtos,
			@NonNull EntityManager entityManager,
			@NonNull ObjectMapper objMapper) {
		screenDtos.forEach(scr -> {
			entityManager.persist(mapToScreen(objMapper, scr));
			if (scr.getNavigation() != null && scr.getNavigation().getMenu() != null) {
				AtomicInteger seq = new AtomicInteger(0);
				scr.getNavigation().getMenu()
						.forEach(menu -> dfs(scr.getName(), seq, null, menu, entityManager));
			}
		});
	}

	@NonNull
	private static Screen mapToScreen(
			@NonNull ObjectMapper objectMapper,
			@NonNull ScreenSourceDto screenSourceDto) {
		return new Screen()
				.setName(screenSourceDto.getName())
				.setTitle(screenSourceDto.getTitle())
				.setPrimary(screenSourceDto.getPrimaryViewName())
				.setPrimaries(serializeOrElseNull(objectMapper, screenSourceDto.getPrimaryViews()));
	}

	private static void dfs(@NonNull String screenName,
			@NonNull AtomicInteger seq,
			@Nullable NavigationGroup parent,
			@NonNull ScreenSourceDto.ScreenNavigationSourceDto.MenuItemSourceDto menuDto,
			@NonNull EntityManager entityManager) {
		if (menuDto.getChild() != null && !menuDto.getChild().isEmpty()) {
			NavigationGroup navigationGroup = mapToNavigationGroup(screenName, seq.get(), parent, menuDto);
			entityManager.persist(navigationGroup);
			seq.incrementAndGet();
			menuDto.getChild().forEach(child -> dfs(screenName, seq, navigationGroup, child, entityManager));
		} else {
			entityManager.persist(mapToNavigationView(screenName, seq.get(), parent, menuDto));
			seq.incrementAndGet();
		}
	}

	@NonNull
	private static NavigationGroup mapToNavigationGroup(
			@NonNull String screenName,
			int seq,
			@Nullable NavigationGroup parentNavigationGroup,
			@NonNull ScreenSourceDto.ScreenNavigationSourceDto.MenuItemSourceDto menu) {
		return new NavigationGroup()
				.setId(UUID.randomUUID().toString().replace("-", ""))
				.setTypeCd(CoreDictionaries.ViewGroupType.NAVIGATION)
				.setScreenName(screenName)
				.setTitle(menu.getTitle())
				.setParent(parentNavigationGroup)
				.setSeq(seq)
				.setDescription(null)
				.setDefaultView(ofNullable(menu.getDefaultView()).orElse(menu.getViewName()))
				.setHidden(ofNullable(menu.getHidden()).orElse(false));
	}

	@NonNull
	private static NavigationView mapToNavigationView(
			@NonNull String screenName,
			int seq,
			@Nullable NavigationGroup parentGroup,
			@NonNull ScreenSourceDto.ScreenNavigationSourceDto.MenuItemSourceDto menuDto) {
		return new NavigationView()
				.setId(UUID.randomUUID().toString().replace("-", ""))
				.setViewName(menuDto.getViewName())
				.setDescription(null)
				.setViewName(ofNullable(menuDto.getViewName()).orElse(menuDto.getDefaultView()))
				.setHidden(ofNullable(menuDto.getHidden()).orElse(false))
				.setSeq(seq)
				.setTypeCd(CoreDictionaries.ViewGroupType.NAVIGATION)
				.setScreenName(screenName)
				.setParentGroup(parentGroup);
	}

}
