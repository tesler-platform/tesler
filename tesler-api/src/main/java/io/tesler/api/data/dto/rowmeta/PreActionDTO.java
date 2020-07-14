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
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;


@Setter
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreActionDTO {

	private String type;

	private String message;

	private Map<String, String> customParameter;

	@JsonAnyGetter
	public Map<String, String> getCustomParameter() {
		return customParameter;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

}
