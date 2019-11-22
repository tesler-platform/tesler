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

package io.tesler.core.util.jackson;

import io.tesler.api.util.jackson.DtoPropertyFilter;
import io.tesler.api.util.jackson.deser.contextual.TZAwareLDTContextualDeserializer;
import io.tesler.api.util.jackson.ser.contextual.TZAwareJUDContextualSerializer;
import io.tesler.api.util.jackson.ser.contextual.TZAwareLDTContextualSerializer;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Date;


public class CustomObjectMapper extends ObjectMapper {

	private static final CustomObjectMapper INSTANCE = new CustomObjectMapper();

	public CustomObjectMapper() {
		registerModule(buildJavaTimeModule());
		registerModule(new I18NModule());
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
		disable(Feature.FLUSH_PASSED_TO_STREAM);
		setFilterProvider(new SimpleFilterProvider().addFilter("dtoPropertyFilter", new DtoPropertyFilter()));
	}

	public static CustomObjectMapper getInstance() {
		return INSTANCE;
	}

	private JavaTimeModule buildJavaTimeModule() {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addDeserializer(LocalDateTime.class, new TZAwareLDTContextualDeserializer());
		javaTimeModule.addSerializer(LocalDateTime.class, new TZAwareLDTContextualSerializer());
		javaTimeModule.addSerializer(Date.class, new TZAwareJUDContextualSerializer());
		return javaTimeModule;
	}

}
