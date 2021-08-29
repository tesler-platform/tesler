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

package io.tesler.core.dto.data.view;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.LocaleAware;
import io.tesler.api.util.jackson.deser.convert.Raw2StringDeserializer;
import io.tesler.api.util.jackson.ser.contextaware.I18NAwareRawStringSerializer;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.Widget;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
@JsonFilter("")
public class WidgetDTO extends DataResponseDTO implements BcSource {

	@SearchParameter
	private String name;

	private Integer widgetId;

	private Long position;

	private String descriptionTitle;

	private String description;

	private String snippet;

	private Boolean showExportStamp;

	private Long limit;

	private String type;

	private String url;

	@SearchParameter(name = "bc")
	private String bcName;

	@LocaleAware
	@SearchParameter
	private String title;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String fields;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String options;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String pivotFields;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String axisFields;

	@JsonRawValue
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String showCondition;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String chart;

	@JsonRawValue
	@JsonSerialize(using = I18NAwareRawStringSerializer.class)
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String graph;

	private Number x;

	private Number y;

	private Number width;

	private Number height;

	private Number minHeight;

	private Number maxHeight;

	private Number minWidth;

	private Number maxWidth;

	private Boolean isDraggable;

	private Boolean isResizable;

	private Long gridWidth;

	private Long gridBreak;

	private Boolean hide;

	public WidgetDTO(ViewWidgets widgetWithPosition, int widgetIdCounter) {
		this(widgetWithPosition.getWidget());
		this.position = widgetWithPosition.getPositon() != null ? widgetWithPosition.getPositon() : 0;
		this.gridWidth = widgetWithPosition.getGridWidth() != null ? widgetWithPosition.getGridWidth() : 1;
		this.gridBreak = widgetWithPosition.getGridBreak() != null ? widgetWithPosition.getGridBreak() : 0;
		this.limit = widgetWithPosition.getLimit() != null ? widgetWithPosition.getLimit() : 0;
		this.descriptionTitle = widgetWithPosition.getDescriptionTitle();
		this.description = widgetWithPosition.getDescription();
		this.snippet = widgetWithPosition.getSnippet();
		this.showExportStamp = widgetWithPosition.getShowExportStamp();
		this.widgetId = widgetIdCounter;
		this.hide = widgetWithPosition.getHide();
	}

	public WidgetDTO(Widget widget) {
		this.id = widget.getId().toString();
		this.name = widget.getName();
		this.type = widget.getType();
		this.bcName = widget.getBc();
		this.title = widget.getTitle();
		this.fields = widget.getFields();
		this.options = widget.getOptions();
		this.axisFields = widget.getAxisFields();
		this.pivotFields = widget.getPivotFields();
		this.showCondition = widget.getShowCondition();
		this.chart = widget.getChart();
		this.graph = widget.getGraph();
	}

}
