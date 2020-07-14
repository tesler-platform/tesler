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

package io.tesler.core.dto.data.notifications;

import static io.tesler.api.data.dictionary.DictionaryType.MIME_TYPE;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.TZAware;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.model.core.entity.notifications.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO extends DataResponseDTO {

	@SearchParameter(name = "uiSubject")
	private String subject;

	@SearchParameter(name = "uiMessage")
	private String message;

	@SearchParameter(provider = BooleanValueProvider.class)
	private boolean read;

	private String url;

	@Lov(MIME_TYPE)
	private String mimeType;

	private Long recipientId;

	@TZAware
	private LocalDateTime createdDate;

	public NotificationDTO(Notification entity) {
		this.id = entity.getId().toString();
		this.subject = entity.getUiSubject();
		this.message = entity.getUiMessage();
		this.recipientId = entity.getRecipientId();
		this.url = entity.getUrl();
		this.createdDate = entity.getCreatedDate();
		this.read = entity.isRead();
		if (entity.getMimeType() != null) {
			this.mimeType = entity.getMimeType().getKey();
		}
	}

}
