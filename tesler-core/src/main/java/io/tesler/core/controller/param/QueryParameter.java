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

package io.tesler.core.controller.param;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.core.util.SpringBeanUtils;
import io.tesler.core.util.TypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;


public interface QueryParameter {

	static <T> T getValue(String value, Class<T> cls, T defaultValue) {
		return Optional.ofNullable(value)
				.map(v -> TypeConverter.to(cls, v))
				.orElse(defaultValue);
	}

	@SneakyThrows
	static <T> List<T> getListValue(String stringValue, Class<T> cls) {
		if (StringUtils.isBlank(stringValue)) {
			return Collections.emptyList();
		}
		ObjectMapper teslerObjectMapper = SpringBeanUtils.getBean("teslerObjectMapper");
		List<String> strings = teslerObjectMapper
				.readValue(stringValue, new StringListTypeReference());
		return strings.stream()
				.map(value -> TypeConverter.to(cls, value))
				.collect(Collectors.toList());
	}

	String getName();

	<R> R apply(BiFunction<String, String, R> biFunction);

	class StringListTypeReference extends TypeReference<List<String>> {

	}

}
