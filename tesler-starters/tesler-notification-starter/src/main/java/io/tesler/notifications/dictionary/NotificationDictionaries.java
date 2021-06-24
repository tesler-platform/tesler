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

package io.tesler.notifications.dictionary;

import io.tesler.api.data.dictionary.LOV;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

public class NotificationDictionaries {

	@UtilityClass
	public static final class NotificationDeliveryType {

		public static final LOV PUSH = new LOV("push");

		public static final LOV SMTP = new LOV("smtp");

	}

	@UtilityClass
	public static final class NotificationSettingsType {

		public static final LOV GLOBAL = new LOV("GLOBAL");

		public static final LOV PERSONAL = new LOV("PERSONAL");

		public static final Map<LOV, Integer> ORDER = new HashMap<>();

		static {
			ORDER.put(PERSONAL, 1);
			ORDER.put(GLOBAL, 2);
		}

	}

	@UtilityClass
	public static final class NotificationRecipient {

		public static final LOV MENTIONED_USER = new LOV("MENTIONED_USER");

		public static final LOV COMMENT_AUTHOR = new LOV("COMMENT_AUTHOR");

		public static final LOV CURRENT_USER = new LOV("CURRENT_USER");

	}

	@UtilityClass
	public static final class SystemPref {
		public static final LOV FEATURE_NOTIFICATIONS = new LOV("FEATURE_NOTIFICATIONS");
	}

}
