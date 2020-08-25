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

package io.tesler.api.util.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;


public class InfoMessageSource extends ResourceBundleMessageSource {

	private static final MessageSourceAccessor MESSAGE_SOURCE_ACCESSOR = new MessageSourceAccessor(new InfoMessageSource());

	private InfoMessageSource() {
		ResourceBundleMessageSource core = new ResourceBundleMessageSource();
		core.setDefaultEncoding("UTF-8");
		core.setBasename("info.core.messages");
		setUseCodeAsDefaultMessage(true);
		setDefaultEncoding("UTF-8");
		setBasename("info.messages");
		setParentMessageSource(core);
	}

	public static MessageSourceAccessor messageSourceAccessor() {
		return MESSAGE_SOURCE_ACCESSOR;
	}

	public static String infoMessage(String code) {
		return messageSourceAccessor().getMessage(code);
	}

	public static String infoMessage(String code, Object... args) {
		return messageSourceAccessor().getMessage(code, args);
	}

}
