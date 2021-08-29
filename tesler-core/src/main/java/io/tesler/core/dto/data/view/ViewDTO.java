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

import io.tesler.api.data.dto.LocaleAware;
import io.tesler.api.util.jackson.deser.convert.Raw2StringDeserializer;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.model.ui.entity.View;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ViewDTO {

	private Long id;

	@SearchParameter
	private String name;

	private String template;

	@LocaleAware
	@SearchParameter
	private String title;

	@SearchParameter
	private String url;

	private Boolean customizable;

	private Boolean editable;

	private List<WidgetDTO> widgets;

	private Integer columns;

	private Integer rowHeight;

	private Boolean readOnly;

	private Boolean ignoreHistory;

	@JsonRawValue
	@JsonDeserialize(using = Raw2StringDeserializer.class)
	private String options;

	public ViewDTO(View view) {
		this.id = view.getId();
		this.name = view.getName();
		this.template = view.getTemplate();
		this.customizable = view.getCustomizable();
		this.title = view.getTitle();
		this.url = view.getUrl();
		this.editable = view.getEditable();
		this.ignoreHistory = view.getIgnoreHistory();
		this.options = view.getOptions();
	}

}
