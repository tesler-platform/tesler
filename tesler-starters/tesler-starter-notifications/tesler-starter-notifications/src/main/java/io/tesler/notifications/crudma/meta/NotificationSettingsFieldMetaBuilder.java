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
import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import java.util.ArrayList;
import java.util.List;

import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.notifications.crudma.config.NotificationServiceAssociation;
import io.tesler.notifications.crudma.dto.NotificationSettingsDTO;
import io.tesler.notifications.crudma.dto.NotificationSettingsDTO_;
import io.tesler.notifications.model.entity.NotificationSettings;
import io.tesler.notifications.service.IDeliveryService;
import io.tesler.notifications.service.impl.DeliveryServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationSettingsFieldMetaBuilder extends InnerFieldMetaBuilder<NotificationSettingsDTO> {

	@Autowired
	private DeliveryServiceRegistry registry;

	@Autowired
	private JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<NotificationSettingsDTO> fields,
			InnerBcDescription bcDescription, Long id, Long parentId) {
		fields.setDictionaryTypeWithAllValues(NotificationSettingsDTO_.eventName, DictionaryType.DATABASE_EVENT);
		fields.setRequired(NotificationSettingsDTO_.eventName);
		getEnabledFields().forEach(fields::setEnabled);
		getDisabledFields().forEach(fields::setDisabled);
		if (NotificationServiceAssociation.notificationGlobalSettings.isBc(bcDescription)) {
			if (id == null) {
				fields.setEnabled(NotificationSettingsDTO_.eventName);
			} else {
				NotificationSettings settings = jpaDao.findById(NotificationSettings.class, id);
				if (settings.getNotificationRecipients().isEmpty()) {
					fields.setEnabled(NotificationSettingsDTO_.eventName);
				}
			}
		}
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<NotificationSettingsDTO> fields, InnerBcDescription bcDescription,
			Long parentId) {
		fields.enableFilter(NotificationSettingsDTO_.eventName);
		fields.setAllFilterValuesByLovType(NotificationSettingsDTO_.eventName, DictionaryType.DATABASE_EVENT);
		getDisabledFields().forEach(fields::setHidden);
	}

	private List<DtoField> getEnabledFields() {
		List<DtoField> result = new ArrayList<>();
		for (IDeliveryService service : registry.getServiceList()) {
			if (service.isActive()) {
				result.add(new DtoField(service.getDeliveryType()));
			}
		}
		return result;
	}

	private List<DtoField> getDisabledFields() {
		List<DtoField> result = new ArrayList<>();
		for (IDeliveryService service : registry.getServiceList()) {
			if (!service.isActive()) {
				result.add(new DtoField(service.getDeliveryType()));
			}
		}
		return result;
	}

}
