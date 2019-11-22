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

package io.tesler.core.ui;

import io.tesler.api.util.ServiceUtils;
import io.tesler.core.ui.field.FieldExtractor;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.BcField.Attribute;
import io.tesler.core.ui.model.ViewWidgetsGroup;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;


public final class WidgetUtils {

	private static final Map<String, FieldExtractor> FIELD_EXTRACTOR_MAP;

	static {
		Map<String, FieldExtractor> extractors = new HashMap<>();
		ServiceUtils.loadServices(FieldExtractor.class, WidgetUtils.class)
				.forEach(extractor -> extractor.getSupportedTypes().forEach(type ->
						extractors.compute(type, (key, current) -> Stream.of(current, extractor)
								.filter(Objects::nonNull)
								.min(Comparator.comparing(
										FieldExtractor::getPriority
								)).orElse(extractor))
				));
		FIELD_EXTRACTOR_MAP = Collections.unmodifiableMap(extractors);
	}

	public static Set<BcField> extractFields(final Widget widget) {
		final FieldExtractor fieldExtractor = FIELD_EXTRACTOR_MAP.get(widget.getType());
		if (fieldExtractor == null) {
			return Collections.emptySet();
		}
		return fieldExtractor.extract(widget);
	}

	public static Set<BcField> extractPickListFields(final Widget widget) {
		return extractFields(widget).stream()
				.map(field -> field.<Set<BcField>>getAttribute(Attribute.PICK_LIST_FIELDS))
				.filter(CollectionUtils::isNotEmpty)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	public static Set<BcField> extractShowConditionFields(final Widget widget) {
		return FIELD_EXTRACTOR_MAP.get("ShowConditionFields").extract(widget);
	}

	public static Set<BcField> extractPivotFields(final Widget widget) {
		return FIELD_EXTRACTOR_MAP.get("PivotFields").extract(widget);
	}

	public static Set<BcField> extractChartFields(final Widget widget) {
		return FIELD_EXTRACTOR_MAP.get("ChartFields").extract(widget);
	}

	public static Set<BcField> extractHierarchyFields(final Widget widget) {
		return FIELD_EXTRACTOR_MAP.get("HierarchyFields").extract(widget);
	}

	public static Set<BcField> extractAllFields(final Widget widget) {
		final Set<BcField> fields = new HashSet<>(extractFields(widget));
		fields.addAll(extractShowConditionFields(widget));
		fields.addAll(extractPivotFields(widget));
		fields.addAll(extractChartFields(widget));
		fields.addAll(extractHierarchyFields(widget));
		fields.addAll(
				fields.stream()
						.map(field -> field.<Set<BcField>>getAttribute(Attribute.PICK_LIST_FIELDS))
						.filter(CollectionUtils::isNotEmpty)
						.flatMap(Collection::stream)
						.collect(Collectors.toSet())
		);
		return fields;
	}

	public static List<ViewWidgetsGroup> getViewWidgetsGroups(final List<ViewWidgets> viewWidgetList) {
		final Map<String, List<Widget>> viewWidgets = new HashMap<>();
		for (final ViewWidgets viewWidget : viewWidgetList) {
			viewWidgets.computeIfAbsent(viewWidget.getViewName(), s -> new ArrayList<>()).add(viewWidget.getWidget());
		}
		return viewWidgets.entrySet().stream()
				.map(entry -> new ViewWidgetsGroup(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

}
