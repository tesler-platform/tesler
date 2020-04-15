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

import io.tesler.core.exception.ClientException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MultivalueFieldDeserializer extends JsonDeserializer<MultivalueField> {

	@Qualifier("teslerObjectMapper")
	private final ObjectMapper mapper;

	private static final CollectionType collectionType =
			TypeFactory
					.defaultInstance()
					.constructCollectionType(List.class, MultivalueFieldSingleValue.class);

	@Override
	public MultivalueField deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {

		JsonNode node = jsonParser.readValueAsTree();
		if (null == node) {
			return null;
		} else if (!node.isArray()) {
			throw new ClientException("Не удалось десериализовать multivalue поле");
		} else if (!node.elements().hasNext()) {
			return new MultivalueField();
		} else {
			return new MultivalueField(mapper.readerFor(collectionType).readValue(node));
		}
	}

}
