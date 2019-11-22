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

import io.tesler.api.util.jackson.ser.contextual.I18NAwareStringContextualSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class I18NModule extends SimpleModule {

	public I18NModule() {
		addSerializer(String.class, new I18NAwareStringContextualSerializer());
	}

}
