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

package io.tesler.core.config.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

	public static final String TESLER_CACHE_RESOLVER = "teslerCacheResolver";

	public static final String NOTIFICATION_SETTINGS = "notificationSettings";

	public static final String WORKFLOW_CACHE = "workflow";

	public static final String USER_CACHE = "userCache";

	public static final String REQUEST_CACHE = "requestCache";

	public static final String LINKED_DICTIONARY_RULES = "linkedDictionaryRules";

	public static final String SPECIFICATION_CACHE = "specifications";

	public static final String UI_CACHE = "widgetcache";

}
