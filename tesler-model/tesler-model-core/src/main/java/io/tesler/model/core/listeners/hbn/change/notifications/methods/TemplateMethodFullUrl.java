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

import io.tesler.api.data.dictionary.CoreDictionaries.SystemPref;
import io.tesler.api.system.SystemSettings;
import freemarker.template.TemplateModelException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateMethodFullUrl extends TemplateMethod {

	@Autowired
	private SystemSettings systemSettings;

	@Override
	public String getName() {
		return "fullUrl";
	}

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.isEmpty()) {
			throw new TemplateModelException("empty arguments");
		}
		if (arguments.size() != 1) {
			throw new TemplateModelException("syntax error, expected 1 argument, got: " + arguments);
		}
		String url = unwrap(arguments.get(0), String.class);
		String systemUrl = systemSettings.getValue(SystemPref.SYSTEM_URL);
		if (StringUtils.isBlank(systemUrl)) {
			return url;
		}
		if (StringUtils.isBlank(url)) {
			return systemUrl;
		}
		if (!systemUrl.endsWith("/")) {
			systemUrl += "/";
		}
		if (!systemUrl.endsWith("#/")) {
			systemUrl += "#/";
		}
		return systemUrl + url;
	}

}
