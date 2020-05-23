/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.data.dto.rowmeta;

import io.tesler.api.data.dto.LocaleAware;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class ActionDTO {

	private String type;

	@LocaleAware
	private String text;

	private Integer maxGroupVisualButtonsCount;

	@JsonSerialize(using = ActionDTOListSerializer.class)
	private List<ActionDTO> actions;

	@JsonProperty(value = "preInvoke")
	private PreActionDTO preActionDTO;

	private String icon;

	private String bcKey;

	private boolean showOnlyIcon;

	private String scope;

	private boolean autoSaveBefore;

	@JsonIgnore
	private boolean available;

	/**
	 * Конструктор, используемый для создания Action'а - одиночки
	 */
	public ActionDTO(String type, String text) {
		this.type = type;
		this.text = text;
	}

	/**
	 * Конструктор, используемый для создания группы Action'ов
	 */
	public ActionDTO(String type, String text, int maxGroupVisualButtonsCount, List<ActionDTO> actions) {
		this.type = type;
		this.text = text;
		this.maxGroupVisualButtonsCount = maxGroupVisualButtonsCount;
		this.actions = actions;
	}

	/**
	 * Конструктор, используемый для создания группы Action'ов для WF
	 */
	public ActionDTO(String text, int maxGroupVisualButtonsCount, List<ActionDTO> actions) {
		this.text = text;
		this.maxGroupVisualButtonsCount = maxGroupVisualButtonsCount;
		this.actions = actions;
	}

}
