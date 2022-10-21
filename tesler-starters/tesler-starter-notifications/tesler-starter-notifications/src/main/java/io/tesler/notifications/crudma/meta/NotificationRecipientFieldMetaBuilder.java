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

package io.tesler.notifications.crudma.meta;



import io.tesler.api.data.dictionary.DictionaryType;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.notifications.crudma.config.NotificationServiceAssociation;
import io.tesler.notifications.crudma.dto.NotificationRecipientDTO;
import io.tesler.notifications.crudma.dto.NotificationRecipientDTO_;
import org.springframework.stereotype.Service;


@Service
public class NotificationRecipientFieldMetaBuilder extends InnerFieldMetaBuilder<NotificationRecipientDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<NotificationRecipientDTO> fields,
			InnerBcDescription bcDescription, Long id, Long parentId) {
		if (NotificationServiceAssociation.notificationRecipients.isBc(bcDescription)) {
			fields.setEnabled(NotificationRecipientDTO_.recipientType);
			fields.setEnabled(NotificationRecipientDTO_.sameDeptOnly);
			fields.setRequired(NotificationRecipientDTO_.recipientType);
		} else {
			fields.setEnabled(NotificationRecipientDTO_.enabled);
		}

		// глобальные настройки
		if (NotificationServiceAssociation.notificationRecipients.isBc(bcDescription)) {
			fields.setDictionaryTypeWithAllValues(NotificationRecipientDTO_.recipientType, DictionaryType.NOTIFICATION_RECIPIENT_TYPE);
		}
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<NotificationRecipientDTO> fields, InnerBcDescription bcDescription,
			Long parentId) {

	}

}
