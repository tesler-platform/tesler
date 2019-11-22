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

package io.tesler.crudma.meta.notifications;

import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.cntrpId;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.cntrpName;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.eventName;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.message;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.mimeType;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.subject;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.taskId;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.taskName;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.uiMessage;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.uiSubject;
import static io.tesler.crudma.dto.notifications.NotificationTemplateDTO_.url;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.crudma.dto.notifications.NotificationTemplateDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateFieldMetaBuilder extends FieldMetaBuilder<NotificationTemplateDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<NotificationTemplateDTO> fields,
			InnerBcDescription bcDescription, Long rowId, Long parRowId) {
		fields.setEnabled(
				eventName,
				mimeType,
				subject,
				message,
				uiSubject,
				uiMessage,
				url,
				taskId,
				taskName,
				cntrpId,
				cntrpName
		);
		fields.setRequired(eventName, mimeType, subject, message, uiSubject, uiMessage);
		fields.setDictionaryTypeWithAllValues(eventName, DictionaryType.DATABASE_EVENT);
		fields.setDictionaryTypeWithAllValues(mimeType, DictionaryType.MIME_TYPE);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<NotificationTemplateDTO> fields, InnerBcDescription bcDescription,
			Long parRowId) {
		fields.enableFilter(eventName, mimeType);
		fields.setEphemeral(taskId, taskName, cntrpId, cntrpName);
		fields.setAllFilterValuesByLovType(eventName, DictionaryType.DATABASE_EVENT);
		fields.setAllFilterValuesByLovType(mimeType, DictionaryType.MIME_TYPE);
	}

}
