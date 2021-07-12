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

package io.tesler.notifications.model.hbn.change;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.notifications.api.INotificationTemplate;
import io.tesler.notifications.api.NotificationEvent;
import io.tesler.notifications.api.NotificationTemplateSupport;
import io.tesler.notifications.api.TemplateProcessingService;
import io.tesler.notifications.model.entity.NotificationTemplate;
import io.tesler.notifications.model.entity.NotificationTemplate_;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.metamodel.Attribute;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationTemplateSupportImpl implements NotificationTemplateSupport {

	private final Attribute<NotificationTemplate, String> SUBJECT = NotificationTemplate_.subject;

	private final Attribute<NotificationTemplate, String> MESSAGE = NotificationTemplate_.message;

	private final Attribute<NotificationTemplate, String> UI_SUBJECT = NotificationTemplate_.uiSubject;

	private final Attribute<NotificationTemplate, String> UI_MESSAGE = NotificationTemplate_.uiMessage;

	private final Attribute<NotificationTemplate, String> URL = NotificationTemplate_.url;

	private final TemplateProcessingService templateService;

	private final JpaDao jpaDao;

	@Override
	public INotificationTemplate getTemplate(LOV event) {
		return jpaDao.getFirstResultOrNull(
				NotificationTemplate.class,
				(root, query, cb) -> cb.equal(
						root.get(NotificationTemplate_.eventName), event
				)
		);
	}


	@Override
	public boolean isTemplateRequired(Map<String, Object> model) {
		for (Attribute attribute : getTemplateAttributes()) {
			if (model.get(attribute.getName()) == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NotificationEvent processTemplate(INotificationTemplate notificationTemplate, Map<String, Object> model,
			NotificationEvent event) {
		try {
			Map<String, String> result = new HashMap<>();
			List<Attribute<NotificationTemplate, String>> attributes = getTemplateAttributes();
			attributes.removeIf(attribute -> {
				Object value = model.get(attribute.getName());
				if (value != null) {
					result.put(attribute.getName(), value.toString());
					return true;
				}
				return false;
			});

			attributes.forEach(attribute ->
					result.putAll(templateService.processTemplate(notificationTemplate, model, attribute))
			);

			event.setUrl(result.get(URL.getName()));
			event.setSubject(result.get(SUBJECT.getName()));
			event.setMessage(result.get(MESSAGE.getName()));
			event.setUiSubject(result.get(UI_SUBJECT.getName()));
			event.setUiMessage(result.get(UI_MESSAGE.getName()));
			return event;
		} catch (Error e) {
			log.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}


	private List<Attribute<NotificationTemplate, String>> getTemplateAttributes() {
		return new ArrayList<>(Arrays.asList(SUBJECT, MESSAGE, UI_SUBJECT, UI_MESSAGE, URL));
	}

}
