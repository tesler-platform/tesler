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

package io.tesler.core.metahotreload.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

	@SneakyThrows
	@Nullable
	public static String serializeOrElseNull(@NonNull ObjectMapper objectMapper, @Nullable JsonNode jsonNode) {
		return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : null;
	}

	@SneakyThrows
	@Nullable
	public static String serializeOrElseEmptyArr(@NonNull ObjectMapper objectMapper, @Nullable JsonNode jsonNode) {
		return objectMapper.writeValueAsString(jsonNode != null ? jsonNode : new ArrayList<>());
	}
}
