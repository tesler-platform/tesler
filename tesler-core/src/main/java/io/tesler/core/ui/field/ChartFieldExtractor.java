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

package io.tesler.core.ui.field;

import static java.util.stream.Stream.of;

import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.BcField.Attribute;
import io.tesler.core.ui.model.json.ChartMetaItem;
import io.tesler.core.util.JsonUtils;
import io.tesler.model.ui.entity.Widget;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public final class ChartFieldExtractor implements FieldExtractor {

	private static Set<BcField> extractFromObject(final Widget widget, final JsonNode seriesNode) {
		return of("param", "value")
				.map(name -> extractField(widget, seriesNode, name))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private static Set<BcField> extractFromStringArray(final Widget widget, final JsonNode seriesNode) {
		return Streams.stream(seriesNode)
				.map(JsonNode::textValue)
				.map(name -> extractField(widget, seriesNode, name))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private static Set<BcField> extractFromObjectArray(final Widget widget, final JsonNode seriesNode) {
		final HashSet<BcField> fields = new HashSet<>();
		for (final JsonNode jsonNode : seriesNode) {
			final JsonNode dataNode = jsonNode.get("data");
			if (dataNode != null) {
				fields.addAll(
						of("categories", "param", "value", "section", "area", "name", "y", "drillDownTarget", "drillDownType")
								.map(name -> extractField(widget, dataNode, name))
								.flatMap(Collection::stream)
								.collect(Collectors.toSet())
				);

				final JsonNode valueNode = dataNode.get("value");
				if (valueNode != null) {
					if (valueNode.isArray()) {
						for (final JsonNode value : valueNode) {
							fields.add(newWidgetField(widget, value.get("value").textValue()));
						}
					}
				}
			}
		}
		return fields;
	}

	private static Set<BcField> extractField(final Widget widget, final JsonNode node, final String name) {
		final JsonNode valueNode = node.get(name);
		if (valueNode != null && valueNode.isTextual()) {
			return Collections.singleton(newWidgetField(widget, valueNode.textValue()));
		}
		return Collections.emptySet();
	}

	private static BcField newWidgetField(final Widget widget, final String name) {
		return new BcField(widget.getBc(), trim(name))
				.putAttribute(Attribute.WIDGET_ID, widget.getId());
	}

	private static String trim(final String value) {
		if (value.startsWith("%") && value.endsWith("%")) {
			return value.substring(1, value.length() - 1);
		}
		throw new IllegalArgumentException("Название поля имеет неверный формат");
	}

	private static JsonNode getJsonNode(final JsonNode jsonNode, final String... path) {
		JsonNode currentNode = jsonNode;
		for (String s : path) {
			currentNode = currentNode.get(s);
			if (currentNode == null) {
				return null;
			}
		}
		return currentNode;
	}

	@Override
	public Set<BcField> extract(final Widget widget) {
		final HashSet<BcField> fields = new HashSet<>();

		final JsonNode jsonNode = JsonUtils.readTree(widget.getChart());
		if (jsonNode.isArray()) {
			for (final JsonNode chart : jsonNode) {
				final JsonNode xAxisNode = getJsonNode(chart, "meta", "xAxis");
				if (xAxisNode != null) {
					fields.addAll(extractField(widget, xAxisNode, "categories"));
				}
				final JsonNode seriesNode = getJsonNode(chart, "meta", "series");
				if (seriesNode != null) {
					final JsonNode engineNode = getJsonNode(chart, "engine");
					final String engine = engineNode == null ? null : engineNode.textValue();

					final JsonNode chartTypeNode = getJsonNode(chart, "meta", "chart", "type");
					final String chartType = chartTypeNode == null ? null : chartTypeNode.textValue();

					if ("List".equals(widget.getType()) || "DataGrid".equals(widget.getType())) {
						if (ChartMetaItem.Engine.HIGHCHARTS.getValue().equals(engine)) {
							if (chartType == null) {
								fields.addAll(extractFromObject(widget, seriesNode));
							} else if ("pie".equals(chartType) || "pie-donut".equals(chartType)) {
								fields.addAll(extractFromObjectArray(widget, seriesNode));
							} else {
								fields.addAll(extractFromStringArray(widget, seriesNode));
							}
						} else if (ChartMetaItem.Engine.HIGHMAPS.getValue().equals(engine)) {
							fields.addAll(extractFromObjectArray(widget, seriesNode));
						}
					} else if ("Pivot".equals(widget.getType())) {
						if (ChartMetaItem.Engine.HIGHCHARTS.getValue().equals(engine)) {
							if (chartType == null) {
								fields.addAll(extractFromObject(widget, seriesNode));
							} else if ("pie".equals(chartType) || "pie-donut".equals(chartType)) {
								fields.addAll(extractFromObjectArray(widget, seriesNode));
							} else {
								fields.addAll(extractFromObject(widget, seriesNode));
							}
						}
					}
				}
			}
		}

		return fields;
	}

	@Override
	public List<String> getSupportedTypes() {
		List<String> result = new ArrayList<>();
		result.add("ChartFields");
		return result;
	}

}
