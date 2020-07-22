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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.SqlBcDescription;
import io.tesler.core.dao.impl.ViewDAO;
import io.tesler.core.dto.data.view.BcSourceBaseDTO;
import io.tesler.core.dto.data.view.BusinessComponentDTO;
import io.tesler.core.dto.data.view.BusinessObjectDTO;
import io.tesler.core.dto.data.view.ScreenBuildMeta;
import io.tesler.core.dto.data.view.ScreenDTO;
import io.tesler.core.dto.data.view.ViewDTO;
import io.tesler.core.dto.data.view.WidgetDTO;
import io.tesler.core.dto.rowmeta.FilterGroupDTO;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.ViewService;
import io.tesler.core.ui.model.json.WidgetOptions;
import io.tesler.core.util.JsonUtils;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.BcProperties;
import io.tesler.model.ui.entity.FilterGroup;
import io.tesler.model.ui.entity.Screen;
import io.tesler.model.ui.entity.Screen_;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewLayout;
import io.tesler.model.ui.entity.ViewLayout_;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.WidgetLayout;
import io.tesler.model.ui.entity.WidgetLayout_;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {

	private final JpaDao jpaDao;

	private final ViewDAO viewDAO;

	private final BcRegistry bcRegistry;

	private final UIServiceImpl uiService;

	private final SessionService sessionService;

	private ViewDTO buildViewDTO(View view,
			Map<String, List<ViewWidgets>> allViewWidgets,
			Map<String, ViewLayout> allViewLayoutsByUser,
			Map<String, Boolean> responsibilities) {
		int widgetIdCounter = 0;
		ViewLayout viewLayout = allViewLayoutsByUser.get(view.getName());
		List<ViewWidgets> viewWidgetsList = allViewWidgets.get(view.getName());
		if (viewWidgetsList == null) {
			viewWidgetsList = Collections.emptyList();
		}
		ViewDTO result = viewLayout != null ? new ViewDTO(view, viewLayout) : new ViewDTO(view);
		result.setReadOnly(Optional.ofNullable(responsibilities.get(view.getName())).orElse(false));
		Set<WidgetLayout> widgetLayouts = viewLayout != null ? viewLayout.getWidgets() : null;
		List<WidgetDTO> list = new ArrayList<>();
		for (ViewWidgets widgetWithPosition : viewWidgetsList) {
			WidgetLayout layout = null;
			if (widgetLayouts != null) {
				layout = widgetLayouts
						.stream()
						.filter(widget -> widget.getWidgetId().equals(widgetWithPosition.getWidget().getId()))
						.findFirst().orElse(null);
			}
			WidgetDTO widgetDTO = layout != null
					? new WidgetDTO(widgetWithPosition, widgetIdCounter, layout)
					: new WidgetDTO(widgetWithPosition, widgetIdCounter);
			widgetDTO.setUrl(bcRegistry.getUrlFromBc(widgetWithPosition.getWidget().getBc()));
			list.add(widgetDTO);
			widgetIdCounter++;
		}
		result.setWidgets(list);
		return result;
	}

	@Override
	public ScreenDTO getScreen(String name) {
		final Screen screen = jpaDao.getSingleResultOrNull(
				Screen.class,
				(root, query, cb) -> cb.equal(root.get(Screen_.name), name)
		);

		if (screen == null) {
			throw new ClientException(errorMessage("error.screen_not_found", name));
		}

		final List<String> views = sessionService.getViews(screen.getName());
		final Map<String, Boolean> responsibilities = sessionService.getResponsibilities();
		return getScreen(screen, new ScreenBuildMeta(views, responsibilities));
	}

	private ScreenDTO getScreen(Screen screen, ScreenBuildMeta meta) {
		final Map<String, List<ViewWidgets>> allViewWidgets = uiService.getAllWidgetsWithPositionByScreen(
				meta.getViews()
		);
		final Map<String, ViewLayout> allViewLayoutsByUser = uiService.getAllViewLayoutByScreenForUser(
				meta.getViews(),
				sessionService.getSessionUser().getId()
		);

		List<View> views = uiService.getViews(meta.getViews());

		final List<ViewDTO> viewDTOs = views.stream()
				.map(view -> buildViewDTO(view, allViewWidgets, allViewLayoutsByUser, meta.getResponsibilities()))
				.collect(Collectors.toList());

		final ScreenDTO result = new ScreenDTO(screen);
		result.setNavigation(uiService.getScreenNavigation(screen));
		result.setViews(viewDTOs);
		result.setBo(getBusinessObject(viewDTOs));
		return result;
	}

	@Override
	public void saveLayout(String viewName, List<WidgetLayout> widgets) {
		View view = viewDAO.findByName(viewName);
		Specification<ViewLayout> spec = (root, cq, cb) -> cb.and(
				cb.equal(root.get(ViewLayout_.userId), sessionService.getSessionUser().getId()),
				cb.equal(root.get(ViewLayout_.viewName), view.getName())
		);

		List<ViewLayout> results = jpaDao.getList(ViewLayout.class, spec);

		if (results.isEmpty()) {
			ViewLayout layout = new ViewLayout();
			layout.setUserId(sessionService.getSessionUser().getId());
			layout.setViewName(view.getName());
			jpaDao.save(layout);
			widgets.forEach(widgetLayout -> widgetLayout.setLayout(layout));
			widgets.forEach(jpaDao::save);
			return;
		}

		ViewLayout layout = results.get(0);
		Set<WidgetLayout> prevWidgets = layout.getWidgets();
		widgets.forEach(widget -> {
			Optional<WidgetLayout> existedWidget = prevWidgets.stream()
					.filter(prevWidget -> prevWidget.getWidgetId().equals(widget.getWidgetId()))
					.findFirst();
			if (existedWidget.isPresent()) {
				existedWidget.get().merge(widget);
			} else {
				widget.setLayout(layout);
			}
			WidgetLayout widgetLayout = existedWidget.orElse(widget);
			if (widgetLayout.getId() == null) {
				jpaDao.save(widgetLayout);
			}
		});
	}

	@Override
	public void clearLayout(String viewName) {
		View view = viewDAO.findByName(viewName);

		Specification<ViewLayout> spec = (root, cq, cb) -> cb.and(
				cb.equal(root.get(ViewLayout_.userId), sessionService.getSessionUser().getId()),
				cb.equal(root.get(ViewLayout_.viewName), view.getName())
		);

		ViewLayout layout = jpaDao.getSingleResultOrNull(ViewLayout.class, spec);
		if (layout != null) {
			jpaDao.delete(WidgetLayout.class, (root, query, cb) -> cb.equal(root.get(WidgetLayout_.layout), layout));
			jpaDao.delete(layout);
		}
	}

	private BusinessObjectDTO getBusinessObject(List<ViewDTO> viewDTOs) {
		BusinessObjectDTO businessObjectDTO = new BusinessObjectDTO(
				viewDTOs.stream().map(ViewDTO::getWidgets)
						.flatMap(Collection::stream)
						.filter(widgetDTO -> Objects.nonNull(widgetDTO.getBcName()))
						.map(this::getWidgetBc)
						.flatMap(Collection::stream)
						.peek(this::setBcId)
						.distinct()
						.sorted(Comparator.comparing(BusinessComponentDTO::getUrl))
						.collect(Collectors.toList())
		);
		setBcParameters(businessObjectDTO);
		setFilterGroups(businessObjectDTO);
		return businessObjectDTO;
	}

	private void setBcId(BusinessComponentDTO dto) {
		BcDescription description = bcRegistry.getBcDescription(dto.getName());
		if (description instanceof SqlBcDescription) {
			dto.setId(((SqlBcDescription) description).getId());
		}
	}

	private void setBcParameters(final BusinessObjectDTO boDto) {
		Map<String, BcProperties> defaultBcPropertiesMap = uiService.getStringDefaultBcPropertiesMap(boDto);
		boDto.getBc().forEach(dto -> {
			BcProperties bcProperties = defaultBcPropertiesMap.get(dto.getName());
			if (bcProperties != null) {
				Optional.ofNullable(bcProperties.getLimit()).ifPresent(dto::setLimit);
				Optional.ofNullable(bcProperties.getReportPeriod()).ifPresent(dto::setReportPeriod);
				Optional.ofNullable(bcProperties.getSort()).ifPresent(dto::setDefaultSort);
				Optional.ofNullable(bcProperties.getFilter()).ifPresent(dto::setDefaultFilter);
				Optional.ofNullable(bcProperties.getDimFilterSpec()).ifPresent(dto::setDimFilterSpec);
			}
			BcDescription bcDescription = bcRegistry.getBcDescription(dto.getName());
			dto.setParentName(bcDescription.getParentName());
			dto.setRefresh(bcDescription.isRefresh());
			if (bcDescription instanceof SqlBcDescription) {
				dto.setBinds(((SqlBcDescription) bcDescription).getBindsString());
				dto.setLimit(((SqlBcDescription) bcDescription).getPageLimit());
			}
		});
	}

	private void setFilterGroups(final BusinessObjectDTO boDto) {
		Map<String, List<FilterGroup>> filterGroupMap = uiService.getFilterGroups(boDto);
		boDto.getBc().forEach(dto -> {
			List<FilterGroup> filterGroups = filterGroupMap.get(dto.getName());
			if (filterGroups != null && !filterGroups.isEmpty()) {
				List<FilterGroupDTO> result = new ArrayList<>();
				filterGroups.forEach(fg -> {
					FilterGroupDTO filterGroupDTO = FilterGroupDTO.builder()
							.entity(fg)
							.build();
					result.add(filterGroupDTO);
				});
				dto.setFilterGroups(result);
			}
		});
	}

	/**
	 * Gets a DB from a widget, taking into account whether widgets with a hierarchy have dependent BCS without their own widget
	 */
	@SneakyThrows
	private List<BusinessComponentDTO> getWidgetBc(WidgetDTO widgetDTO) {
		List<BusinessComponentDTO> result = new ArrayList<>();
		result.add(new BusinessComponentDTO(widgetDTO));
		Optional.ofNullable(widgetDTO.getOptions())
				.map(JsonUtils::readTree)
				.filter(JsonNode::isObject)
				.map(options -> JsonUtils.readValue(WidgetOptions.class, options))
				.map(WidgetOptions::getHierarchy)
				.ifPresent(list -> list.forEach(item -> {
							String bcName = item.getBcName();
							String url = bcRegistry.getUrlFromBc(bcName);
							result.add(new BusinessComponentDTO(new BcSourceBaseDTO(bcName, url)));
						}
				));
		return result;
	}

}
