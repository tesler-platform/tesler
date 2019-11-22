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

package io.tesler.core.util;

import io.tesler.api.exception.ServerException;
import io.tesler.core.util.jackson.CustomObjectMapper;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;


public final class JsonUtils {

	private static final ObjectMapper OBJECT_MAPPER = new CustomObjectMapper();

	public static JsonNode readTree(String json) {
		try {
			return OBJECT_MAPPER.readTree(json);
		} catch (IOException e) {
			throw new ServerException("Не удалось распарсить Json:" + json, e);
		}
	}

	public static <T> T readValue(final Class<T> valueClass, final TreeNode treeNode) {
		try {
			return OBJECT_MAPPER.treeToValue(treeNode, valueClass);
		} catch (IOException e) {
			throw new IllegalArgumentException("Не удалось распарсить Json:" + treeNode, e);
		}
	}

	public static <T> T readValue(final Class<T> valueClass, final String json) {
		try {
			return OBJECT_MAPPER.readValue(json, valueClass);
		} catch (Exception e) {
			throw new IllegalArgumentException("Не удалось распарсить Json:" + json, e);
		}
	}

	public static <T> String writeValue(final T value) {
		try {
			return OBJECT_MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Не удалось создать Json строку из класса:" + value, e);
		}
	}

}
