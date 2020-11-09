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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.tesler.api.data.dto.LocaleAware;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class ActionDTO {

	@Getter
	private String type;

	@Getter
	@LocaleAware
	private String text;

	@Getter
	private Integer maxGroupVisualButtonsCount;

	@Getter
	@JsonSerialize(using = ActionDTOListSerializer.class)
	private List<ActionDTO> actions;

	@Getter
	@JsonProperty(value = "preInvoke")
	private PreActionDTO preActionDTO;

	@Getter
	private String icon;

	@Getter
	private Map<String, String> customParameters;

	@Getter
	private boolean showOnlyIcon;

	@Getter
	private String scope;

	@Getter
	private boolean autoSaveBefore;

	@Getter
	@JsonIgnore
	private boolean available;

	@JsonAnyGetter
	public Map<String, String> getCustomParameters() {
		return customParameters;
	}


	/**
	 * The constructor used to create one Action
	 */
	public ActionDTO(String type, String text) {
		this.type = type;
		this.text = text;
	}

	/**
	 * The constructor used to create group of Actions
	 */
	public ActionDTO(String type, String text, int maxGroupVisualButtonsCount, List<ActionDTO> actions, String icon, boolean showOnlyIcon) {
		this.type = type;
		this.text = text;
		this.maxGroupVisualButtonsCount = maxGroupVisualButtonsCount;
		this.actions = actions;
		this.icon = icon;
		this.showOnlyIcon = showOnlyIcon;
	}

}
