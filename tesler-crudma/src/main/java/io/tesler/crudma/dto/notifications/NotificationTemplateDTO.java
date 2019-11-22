/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.dto.notifications;

import static io.tesler.api.data.dictionary.DictionaryType.DATABASE_EVENT;
import static io.tesler.api.data.dictionary.DictionaryType.MIME_TYPE;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.model.core.entity.notifications.NotificationTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class NotificationTemplateDTO extends DataResponseDTO {

	@Lov(DATABASE_EVENT)
	private String eventName;

	@Lov(MIME_TYPE)
	private String mimeType;

	private String subject;

	private String message;

	private String uiSubject;

	private String uiMessage;

	private String url;

	private String previewSubject;

	private String previewMessage;

	private String previewUISubject;

	private String previewUIMessage;

	private String previewUrl;

	private Long taskId;

	private String taskName;

	private Long cntrpId;

	private String cntrpName;

	public NotificationTemplateDTO(NotificationTemplate entity) {
		this.id = entity.getId().toString();
		this.eventName = DATABASE_EVENT.lookupValue(entity.getEventName());
		this.mimeType = MIME_TYPE.lookupValue(entity.getMimeType());
		this.subject = entity.getSubject();
		this.message = entity.getMessage();
		this.uiSubject = entity.getUiSubject();
		this.uiMessage = entity.getUiMessage();
		this.url = entity.getUrl();
	}

}
