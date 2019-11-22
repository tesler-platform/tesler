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

package io.tesler.core.dto;

import io.tesler.api.data.dictionary.LOV;
import lombok.Getter;

@Getter
public final class FieldDrillDown {

	private final String taskField;

	private final DrillDownType type;

	private final String url;

	public FieldDrillDown(String field, LOV drillDownTypeCd, String screen, String view, String url) {
		this.taskField = field;
		this.type = DrillDownType.of(drillDownTypeCd.getKey());
		this.url = String.format("screen/%s/view/%s", screen, view) + url;
	}

}
