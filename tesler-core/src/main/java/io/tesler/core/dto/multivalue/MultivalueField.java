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

package io.tesler.core.dto.multivalue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonSerialize(using = MultivalueFieldSerializer.class)
@JsonDeserialize(using = MultivalueFieldDeserializer.class)
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MultivalueField implements Iterable<MultivalueFieldSingleValue> {

	private List<MultivalueFieldSingleValue> values = new ArrayList<>();

	public static <T> Collector<T, MultivalueField, MultivalueField> toMultivalueField(
			Function<T, String> idMapper,
			Function<T, String> valueMapper) {
		return toMultivalueField(idMapper, valueMapper, Collections.emptyMap());
	}

	public static <T> Collector<T, MultivalueField, MultivalueField> toMultivalueField(
			Function<T, String> idMapper,
			Function<T, String> valueMapper,
			Map<MultivalueOptionType, Function<T, String>> optionsMapper
	) {
		return Collector.of(
				MultivalueField::new,
				(result, value) -> {
					MultivalueFieldSingleValue singleValue = new MultivalueFieldSingleValue(
							idMapper.apply(value),
							valueMapper.apply(value)
					);
					optionsMapper.forEach((optionKey, optionValue) -> singleValue
							.addOption(optionKey, optionValue.apply(value)));
					result.values.add(singleValue);
				},
				(result1, result2) -> {
					result1.values.addAll(result2.values);
					return result1;
				}
		);
	}

	@Override
	public Iterator<MultivalueFieldSingleValue> iterator() {
		return values.iterator();
	}

}
