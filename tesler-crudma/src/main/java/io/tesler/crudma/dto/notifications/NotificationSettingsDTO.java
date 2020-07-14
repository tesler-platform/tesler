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
import static io.tesler.api.data.dictionary.DictionaryType.NOTIFICATION_SETTINGS_TYPE;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.LovValueProvider;
import io.tesler.model.core.entity.notifications.NotificationSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class NotificationSettingsDTO extends DataResponseDTO {

	@Lov(DATABASE_EVENT)
	@SearchParameter(provider = LovValueProvider.class)
	private String eventName;

	@Lov(NOTIFICATION_SETTINGS_TYPE)
	private String settingsType;

	private Long userId;

	private Boolean push;

	private Boolean smtp;

	private Boolean neva;

	public NotificationSettingsDTO(NotificationSettings entity) {
		this.id = entity.getId().toString();
		this.eventName = DATABASE_EVENT.lookupValue(entity.getEventName());
		this.settingsType = NOTIFICATION_SETTINGS_TYPE.lookupValue(entity.getSettingsType());
		this.userId = entity.getUserId();
	}

}
