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

package io.tesler.core.service.impl;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.dto.data.view.BusinessObjectDTO;
import io.tesler.core.dto.data.view.ScreenNavigation;
import io.tesler.core.dto.data.view.ScreenNavigation.SubMenu;
import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.core.service.UIService;
import io.tesler.core.util.jackson.CustomObjectMapper;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.User_;
import io.tesler.model.ui.entity.BcProperties;
import io.tesler.model.ui.entity.BcProperties_;
import io.tesler.model.ui.entity.FilterGroup;
import io.tesler.model.ui.entity.FilterGroup_;
import io.tesler.model.ui.entity.Screen;
import io.tesler.model.ui.entity.ScreenViewGroup;
import io.tesler.model.ui.entity.ScreenViewGroupData;
import io.tesler.model.ui.entity.ScreenViewGroupData_;
import io.tesler.model.ui.entity.ScreenViewGroup_;
import io.tesler.model.ui.entity.Screen_;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewLayout;
import io.tesler.model.ui.entity.ViewLayout_;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgets_;
import io.tesler.model.ui.entity.View_;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UIServiceImpl implements UIService {

	private final CustomObjectMapper objectMapper;

	private final ResponsibilitiesService responsibilitiesService;

	private final UserRoleService userRoleService;

	private final JpaDao jpaDao;

	private final UICache uiCache;

	private final CacheManager cacheManager;

	private final TransactionService txService;

	private JsonNode defaultUserSettings;

	private List<ScreenResponsibility> commonScreens;

	@SneakyThrows
	@PostConstruct
	protected void init() {
		InputStream userSettings = getClass().getResourceAsStream("/userSettings.json");
		if (userSettings != null) {
			defaultUserSettings = objectMapper.readTree(userSettings);
		}
		InputStream screens = getClass().getResourceAsStream("/commonScreens.json");
		if (screens != null) {
			commonScreens = objectMapper.readValue(screens, ScreenResponsibility.LIST_TYPE_REFERENCE);
		}
	}

	@Override
	public List<ScreenResponsibility> getCommonScreens() {
		return commonScreens;
	}

	@Override
	public boolean isCommonScreen(String screenName) {
		List<ScreenResponsibility> commonScreens = getCommonScreens();
		for (ScreenResponsibility screen : commonScreens) {
			if (screenName.equals(screen.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Получить пользовательские настройки
	 *
	 * @return JsonNode
	 */
	public JsonNode getUserSettings() {
		return defaultUserSettings;
	}

	@Override
	public Map<String, Boolean> getResponsibilities(User user, LOV userRole) {
		return responsibilitiesService.getListRespByUser(user, userRole);
	}

	@Override
	public String getFirstViewFromResponsibilities(User user, LOV userRole, String... views) {
		Set<String> responsibilities = getResponsibilities(user, userRole).keySet();
		if (responsibilities.isEmpty() && views.length > 0) {
			return views[0];
		}
		for (String view : views) {
			if (responsibilities.contains(view)) {
				return view;
			}
		}
		return null;
	}

	@Override
	public String getFirstViewFromResponsibilities(User user, String... views) {
		return getFirstViewFromResponsibilities(user, userRoleService.getMainUserRoleKey(user), views);
	}

	@Override
	public List<String> getViews(final String screenName, final User user, final LOV userRole) {
		final Set<String> responsibilities = getResponsibilities(user, userRole).keySet();
		final boolean getAll = Objects.equals(userRole, CoreDictionaries.InternalRole.ADMIN) || isCommonScreen(screenName);
		return jpaDao.getList(ScreenViewGroupData.class, (root, query, cb) -> cb.and(
				cb.equal(
						root.get(ScreenViewGroupData_.viewGroup).get(ScreenViewGroup_.screenName),
						screenName
				),
				cb.equal(
						root.get(ScreenViewGroupData_.viewGroup).get(ScreenViewGroup_.typeCd),
						CoreDictionaries.ViewGroupType.NAVIGATION
				),
				getAll ? cb.and() : root.get(ScreenViewGroupData_.viewName).in(responsibilities)
		)).stream().map(ScreenViewGroupData::getViewName).distinct().collect(Collectors.toList());
	}

	public Map<String, BcProperties> getStringDefaultBcPropertiesMap(BusinessObjectDTO boDto) {
		Map<String, BcProperties> result = new HashMap<>(boDto.getBc().size());
		Map<String, BcProperties> allProperties = uiCache.getBcProperties();
		boDto.getBc().forEach(bc -> result.put(bc.getName(), allProperties.get(bc.getName())));
		return result;
	}

	public Map<String, List<FilterGroup>> getFilterGroups(BusinessObjectDTO boDto) {
		HashMap<String, List<FilterGroup>> result = new HashMap<>(boDto.getBc().size());
		Map<String, List<FilterGroup>> all = uiCache.getFilterGroups();
		boDto.getBc().forEach(bc -> result.put(bc.getName(), all.get(bc.getName())));
		return result;
	}

	public Map<String, List<ViewWidgets>> getAllWidgetsWithPositionByScreen(final List<String> views) {
		HashMap<String, List<ViewWidgets>> result = new HashMap<>(views.size());
		Map<String, List<ViewWidgets>> all = uiCache.getWidgets();
		views.forEach(view -> result.put(view, all.get(view)));
		return result;
	}

	public Map<String, ViewLayout> getAllViewLayoutByScreenForUser(final List<String> views, final Long userId) {
		return jpaDao.getList(ViewLayout.class, (root, cq, cb) -> {
			Predicate[] predicates = views.stream()
					.map(view -> cb.equal(root.get(ViewLayout_.viewName), view))
					.toArray(Predicate[]::new);
			return cb.and(cb.or(predicates), cb.equal(root.get(ViewLayout_.user).get(User_.id), userId));
		}).stream().collect(
				Collectors.toMap(ViewLayout::getViewName, Function.identity())
		);
	}

	public List<View> getViews(final List<String> views) {
		List<View> result = new ArrayList<>(views.size());
		Map<String, View> allViews = uiCache.getViews();
		views.forEach(view -> result.add(allViews.get(view)));
		return result;
	}

	public ScreenNavigation getScreenNavigation(final Screen screen) {
		return uiCache.getScreenNavigation(screen);
	}

	public Screen findScreenByName(String name) {
		return jpaDao.getSingleResultOrNull(Screen.class, (root, cq, cb) -> cb.equal(root.get(Screen_.name), name));
	}

	@Override
	public void invalidateCache() {
		txService.invokeAfterCompletion(Invoker.of(
				() -> cacheManager.getCache(CacheConfig.UI_CACHE).clear()
		));
	}

	@Component
	@RequiredArgsConstructor
	public static class UICache {

		private final JpaDao jpaDao;

		@Cacheable(
				cacheNames = CacheConfig.UI_CACHE,
				key = "{#root.methodName}"
		)
		public Map<String, List<ViewWidgets>> getWidgets() {
			return jpaDao.getList(ViewWidgets.class, (root, cq, cb) -> {
				root.fetch(ViewWidgets_.widget);
				return cb.isNotNull(root.get(ViewWidgets_.viewName));
			}).stream().collect(
					Collectors.groupingBy(ViewWidgets::getViewName)
			);
		}

		@Cacheable(
				cacheNames = CacheConfig.UI_CACHE,
				key = "{#root.methodName}"
		)
		public Map<String, View> getViews() {
			return jpaDao.getList(View.class, (root, cq, cb) ->
					cb.isNotNull(root.get(View_.name))
			).stream().collect(
					Collectors.toMap(View::getName, Function.identity())
			);
		}

		@Cacheable(
				cacheNames = CacheConfig.UI_CACHE,
				key = "{#root.methodName}"
		)
		public Map<String, BcProperties> getBcProperties() {
			return jpaDao.getList(BcProperties.class, (root, cq, cb) ->
					cb.isNotNull(root.get(BcProperties_.bc))
			).stream().collect(Collectors.toMap(BcProperties::getBc, Function.identity()));
		}

		@Cacheable(
				cacheNames = CacheConfig.UI_CACHE,
				key = "{#root.methodName}"
		)
		public Map<String, List<FilterGroup>> getFilterGroups() {
			return jpaDao.getList(FilterGroup.class, (root, cq, cb) ->
					cb.isNotNull(root.get(FilterGroup_.bc))
			).stream().collect(
					Collectors.groupingBy(FilterGroup::getBc)
			);
		}

		@Cacheable(
				cacheNames = CacheConfig.UI_CACHE,
				key = "{#root.methodName, #screen.name}"
		)
		public ScreenNavigation getScreenNavigation(final Screen screen) {
			final Map<Long, ScreenNavigation.Menu> map = new HashMap<>();

			final List<ScreenViewGroup> groups = jpaDao.getList(ScreenViewGroup.class, (root, query, cb) -> {
				query.orderBy(cb.asc(root.get(ScreenViewGroup_.seq)));
				return cb.and(
						cb.equal(root.get(ScreenViewGroup_.screenName), screen.getName()),
						cb.equal(root.get(ScreenViewGroup_.typeCd), CoreDictionaries.ViewGroupType.NAVIGATION)
				);
			});
			final List<ScreenViewGroupData> views = jpaDao.getList(ScreenViewGroupData.class, (root, query, cb) -> {
				query.orderBy(cb.asc(root.get(ScreenViewGroupData_.seq)));
				return cb.and(
						cb.equal(root.get(ScreenViewGroupData_.viewGroup).get(ScreenViewGroup_.screenName), screen.getName()),
						cb.equal(
								root.get(ScreenViewGroupData_.viewGroup).get(ScreenViewGroup_.typeCd),
								CoreDictionaries.ViewGroupType.NAVIGATION
						)
				);
			});

			final List<ScreenNavigation.Menu> menus = new ArrayList<>();
			for (final ScreenViewGroup group : groups) {
				final ScreenNavigation.Menu menu = map.computeIfAbsent(
						group.getId(),
						key -> group.getParent() == null ? new ScreenNavigation.Menu() : new ScreenNavigation.SubMenu()
				);
				menu.setCommentDevelop(group.getDescription());
				if (BooleanUtils.isTrue(group.getRoot())) {
					menu.setTitle(group.getTitle());
				} else {
					menu.setCategoryName(group.getTitle());
				}
				if (group.getParent() == null) {
					menus.add(group.getSeq() > menus.size() ? menus.size() : group.getSeq(), menu);
				} else if (menu instanceof ScreenNavigation.SubMenu) {
					final ScreenNavigation.Menu parentMenu = map.computeIfAbsent(
							group.getParent().getId(),
							key -> group.getParent().getParent() == null ? new ScreenNavigation.Menu()
									: new ScreenNavigation.SubMenu()
					);
					if (parentMenu.getChild() == null) {
						parentMenu.setChild(new ArrayList<>());
					}
					final List<SubMenu> child = parentMenu.getChild();
					child.add(group.getSeq() > child.size() ? child.size() : group.getSeq(), (SubMenu) menu);
				}
			}

			for (final ScreenViewGroupData view : views) {
				final SubMenu menu = new SubMenu();
				menu.setViewName(view.getViewName());
				final ScreenNavigation.Menu parentMenu = map.get(view.getViewGroup().getId());
				if (parentMenu.getChild() == null) {
					parentMenu.setChild(new ArrayList<>());
				}
				final List<SubMenu> child = parentMenu.getChild();
				child.add(view.getSeq() > child.size() ? child.size() : view.getSeq(), menu);
			}

			final ScreenNavigation screenNavigation = new ScreenNavigation();
			screenNavigation.setMenu(menus);
			return screenNavigation;
		}

		@CacheEvict(cacheNames = CacheConfig.UI_CACHE, allEntries = true)
		public void evict() {
		}


	}

}
