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

import io.tesler.model.core.listeners.hbn.change.notifications.TemplateProcessingServiceExt;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Slf4j
public abstract class TemplateMethod implements TemplateMethodModelEx {

	@Lazy
	@Autowired
	private TemplateProcessingServiceExt templateProcessingServiceExt;

	public abstract String getName();

	protected <T> T unwrap(Object model, Class<T> cls) throws TemplateModelException {
		if (model == null) {
			return null;
		}
		if (model instanceof TemplateModel) {
			return unwrap((TemplateModel) model, cls);
		}
		throw new IllegalArgumentException();
	}

	@SuppressWarnings("unchecked")
	private <T> T unwrap(TemplateModel model, Class<T> cls) throws TemplateModelException {
		Object result = templateProcessingServiceExt.getBeansWrapper().tryUnwrapTo(model, cls);
		if (result == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
			return null;
		}
		return (T) result;
	}

}
