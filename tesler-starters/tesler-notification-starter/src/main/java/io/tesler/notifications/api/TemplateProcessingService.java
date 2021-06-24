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

package io.tesler.notifications.api;

import java.util.Map;
import javax.persistence.metamodel.Attribute;
import lombok.SneakyThrows;


public interface TemplateProcessingService {

	<E extends INotificationTemplate> Map<String, String> processTemplate(
			E entity,
			Map<String, Object> model,
			Attribute<?, String>... attributes
	);

	@SneakyThrows
	String processTemplate(String templateName, Map<String, Object> model);

	@SneakyThrows
	String processTempTemplate(String templateString, Map<String, Object> model);

}
