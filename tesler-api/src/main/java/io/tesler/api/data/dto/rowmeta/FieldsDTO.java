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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonSerialize(using = FieldsDTOSerializer.class)
public class FieldsDTO implements Iterable<FieldDTO> {

	protected Map<String, FieldDTO> fields = new HashMap<>();

	public static FieldsDTO of(Iterable<FieldDTO> fields) {
		FieldsDTO fieldsDTO = new FieldsDTO();
		fields.forEach(fieldsDTO::add);
		return fieldsDTO;
	}

	public void add(FieldDTO fieldDTO) {
		fields.put(fieldDTO.getKey(), fieldDTO);
	}

	public FieldDTO get(final String key) {
		return fields.get(key);
	}

	@Override
	public Iterator<FieldDTO> iterator() {
		return fields.values().iterator();
	}

}
