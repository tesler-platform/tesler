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


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;


public class LocalizationFormatter {

	private static final Character LEFT_BRACKET = '{';

	private static final Character RIGHT_BRACKET = '}';

	private static final LocalizationFormatter INSTANCE = new LocalizationFormatter();

	@Getter
	private final ResourceBundleMessageSource bundles;

	@Getter
	private final MessageSourceAccessor messages;

	public LocalizationFormatter() {
		ResourceBundleMessageSource core = new ResourceBundleMessageSource();
		core.setDefaultEncoding("UTF-8");
		core.setBasename("ui.core.messages");
		bundles = new ResourceBundleMessageSource();
		bundles.setDefaultEncoding("UTF-8");
		bundles.setBasename("ui.messages");
		bundles.setParentMessageSource(core);
		messages = new MessageSourceAccessor(bundles);
	}

	public static LocalizationFormatter getInstance() {
		return INSTANCE;
	}

	public static String uiMessage(String code) {
		return getInstance().getMessages().getMessage(code);
	}

	public static String uiMessage(String code, Object... args) {
		return getInstance().getMessages().getMessage(code, args);
	}

	public static String i18n(String string) {
		return getInstance().format(string);
	}

	public String format(String string) {
		if (string == null) {
			return string;
		}
		return doFormat(string);
	}

	protected String doFormat(String string) {
		char[] chars = string.toCharArray();
		StringBuilder buffer = new StringBuilder();
		int start = 0;
		Parser parser = new Parser();
		for (int i = 0; i < chars.length; ++i) {
			if (parser.accept(chars[i])) {
				buffer.append(string, start, i - 1);
				start = i - 1;
			}

			if (parser.brackets != 0) {
				continue;
			}

			if (parser.state != ParserState.IN_BRACKET) {
				continue;
			}

			String key = string.substring(start + 2, i - 1);
			String value = null;
			if (StringUtils.isNotBlank(key)) {
				value = getMessage(key);
			}

			if (value == null) {
				buffer.append(LEFT_BRACKET).append(LEFT_BRACKET);
				buffer.append(key);
				buffer.append(RIGHT_BRACKET).append(RIGHT_BRACKET);
			} else {
				buffer.append(value);
			}

			start = i + 1;
			parser.reset();
		}

		if (start != chars.length) {
			buffer.append(string, start, chars.length);
		}

		return buffer.toString();
	}

	protected String getMessage(String key) {
		return messages.getMessage(key, new Object[0], (String) null);
	}


	enum ParserState {
		NORMAL,
		SEEN_BRACKET,
		IN_BRACKET
	}

	private static class Parser {

		private ParserState state = ParserState.NORMAL;

		private int brackets = 0;

		void reset() {
			state = ParserState.NORMAL;
			brackets = 0;
		}

		boolean accept(char c) {
			if (c == LEFT_BRACKET) {
				return acceptLeftBracket();
			} else if (c == RIGHT_BRACKET) {
				return acceptRightBracket();
			} else {
				return acceptOther();
			}
		}

		boolean acceptLeftBracket() {
			switch (state) {
				case NORMAL:
					state = ParserState.SEEN_BRACKET;
					brackets++;
					return false;
				case IN_BRACKET:
					reset();
					return false;
				case SEEN_BRACKET:
					state = ParserState.IN_BRACKET;
					brackets++;
					return true;
				default:
					throw new IllegalStateException();
			}
		}

		boolean acceptRightBracket() {
			switch (state) {
				case NORMAL:
				case SEEN_BRACKET:
					reset();
					return false;
				case IN_BRACKET:
					brackets--;
					return false;
				default:
					throw new IllegalStateException();
			}
		}

		boolean acceptOther() {
			switch (state) {
				case NORMAL:
					return false;
				case SEEN_BRACKET:
					reset();
					return false;
				case IN_BRACKET:
					if (brackets == 1) {
						reset();
					}
					return false;
				default:
					throw new IllegalStateException();
			}
		}

	}


}
