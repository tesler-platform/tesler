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

package io.tesler.api.util.jackson.ser.contextual;

import static io.tesler.api.util.tz.TimeZoneUtil.isTzAware;

import io.tesler.api.util.jackson.ser.contextaware.TZAwareJUDSerializer;
import io.tesler.api.util.jackson.ser.convert.JUD2LDTSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Date;


public class TZAwareJUDContextualSerializer extends JsonSerializer<Date> implements ContextualSerializer {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider prov) throws IOException {
		if (isTzAware(gen)) {
			TZAwareJUDSerializer.INSTANCE.serialize(value, gen, prov);
		} else {
			JUD2LDTSerializer.INSTANCE.serialize(value, gen, prov);
		}
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
		if (isTzAware(property)) {
			return TZAwareJUDSerializer.INSTANCE;
		}
		return this;
	}

}
