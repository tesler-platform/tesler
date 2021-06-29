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

package io.tesler.core.dto.rowmeta;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.data.dto.DataResponseDTO;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EngineFieldsMeta<T extends DataResponseDTO> extends FieldsMeta<T> {


	public EngineFieldsMeta(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	public final void addEngineFilterValues(String field, List<SimpleDictionary> valuesList) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField))
				.ifPresent(fieldDTO -> valuesList.forEach(fieldDTO::addFilterValue));
	}

	public final void addEngineConcreteValues(String field, List<SimpleDictionary> valuesList) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField))
				.ifPresent(fieldDTO -> valuesList.forEach(fieldDTO::addValue));
	}

}
