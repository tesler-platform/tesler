/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.listeners.hbn.change.notifications.methods;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.LOV;
import freemarker.template.TemplateModelException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TemplateMethodLov extends TemplateMethod {

	private final DictionaryCache dictionaryCache;

	@Override
	public String getName() {
		return "lov";
	}

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.isEmpty()) {
			throw new TemplateModelException("empty arguments");
		}
		if (arguments.size() != 2) {
			throw new TemplateModelException("syntax error, expected 2 arguments, got: " + arguments);
		}
		LOV lov = unwrap(arguments.get(0), LOV.class);
		String dictionaryName = unwrap(arguments.get(1), String.class);
		return dictionaryCache.lookupValue(lov, dictionaryName);
	}

}
