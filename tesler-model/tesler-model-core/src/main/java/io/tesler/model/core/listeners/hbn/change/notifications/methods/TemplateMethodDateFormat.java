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

import freemarker.template.TemplateModelException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TemplateMethodDateFormat extends TemplateMethod {

	@Override
	public String getName() {
		return "dateFormat";
	}

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.isEmpty()) {
			throw new TemplateModelException("empty arguments");
		}
		if (arguments.size() != 2) {
			throw new TemplateModelException("syntax error, expected 2 arguments, got: " + arguments);
		}
		Temporal date = unwrap(arguments.get(0), Temporal.class);
		if (date == null) {
			return null;
		}
		String format = unwrap(arguments.get(1), String.class);
		return DateTimeFormatter.ofPattern(format).format(date);
	}

}
