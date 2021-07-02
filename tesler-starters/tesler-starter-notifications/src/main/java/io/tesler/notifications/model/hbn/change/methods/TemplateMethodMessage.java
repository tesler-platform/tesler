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

package io.tesler.notifications.model.hbn.change.methods;


import freemarker.template.TemplateModelException;
import io.tesler.notifications.model.hbn.change.TemplateProcessingServiceExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class TemplateMethodMessage extends TemplateMethod {

	@Lazy
	@Autowired
	private TemplateProcessingServiceExt templateProcessingServiceExt;

	@Override
	public String getName() {
		return "message";
	}

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.isEmpty()) {
			throw new TemplateModelException("empty arguments");
		}
		if (arguments.size() != 1) {
			throw new TemplateModelException("syntax error, expected 1 argument, got: " + arguments);
		}
		String key = unwrap(arguments.get(0), String.class);
		if (StringUtils.isBlank(key)) {
			return StringUtils.EMPTY;
		}
		return templateProcessingServiceExt.getBundles().getMessage(key, new Object[0], key, Locale.getDefault());
	}

}
