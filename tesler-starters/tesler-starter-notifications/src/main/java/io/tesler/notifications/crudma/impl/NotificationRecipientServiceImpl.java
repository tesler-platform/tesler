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

package io.tesler.notifications.crudma.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.tesler.notifications.crudma.api.NotificationRecipientService;
import io.tesler.notifications.crudma.api.NotificationSettingsService;
import io.tesler.notifications.crudma.config.NotificationServiceAssociation;
import io.tesler.notifications.crudma.dto.NotificationRecipientDTO;
import io.tesler.notifications.crudma.dto.NotificationRecipientDTO_;
import io.tesler.notifications.crudma.meta.NotificationRecipientFieldMetaBuilder;
import io.tesler.notifications.model.entity.NotificationRecipient;
import io.tesler.notifications.model.entity.NotificationRecipient_;
import io.tesler.notifications.model.entity.NotificationSettings;
import io.tesler.notifications.model.entity.NotificationSettings_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationRecipientServiceImpl extends
		VersionAwareResponseService<NotificationRecipientDTO, NotificationRecipient> implements
		NotificationRecipientService {

	@Autowired
	private NotificationSettingsService notificationSettingsService;

	@Autowired
	private DictionaryCache dictionaryCache;

	public NotificationRecipientServiceImpl() {
		super(
				NotificationRecipientDTO.class,
				NotificationRecipient.class,
				NotificationRecipient_.notificationSettings,
				NotificationRecipientFieldMetaBuilder.class
		);
	}

	@Override
	protected ResultPage<NotificationRecipientDTO> entitiesToDtos(BusinessComponent bc,
			ResultPage<NotificationRecipient> entities) {
		if (NotificationServiceAssociation.notificationRecipients.isBc(bc)) {
			return super.entitiesToDtos(bc, entities);
		}
		// Оптимизация, чтобы на весь лист получить исключения
		Set<LOV> excluded = getExclusions(bc);
		return ResultPage.of(entities, entity -> entityToDto(bc, entity, excluded));
	}

	private NotificationRecipientDTO entityToDto(BusinessComponent bc, NotificationRecipient entity, Set<LOV> excluded) {
		NotificationRecipientDTO dto = super.entityToDto(bc, entity);
		dto.setEnabled(!excluded.contains(entity.getRecipientType()));
		return dto;
	}

	private Set<LOV> getExclusions(BusinessComponent bc) {
		NotificationSettings settings = baseDAO.findById(NotificationSettings.class, bc.getParentIdAsLong());
		NotificationSettings userSettings = notificationSettingsService.getUserSettings(settings.getEventName());
		Set<LOV> exclusions = new HashSet<>();
		if (userSettings != null) {
			baseDAO.getList(
					NotificationRecipient.class,
					(root, cq, cb) -> cb.equal(root.get(NotificationRecipient_.notificationSettings), userSettings)
			).stream().map(NotificationRecipient::getRecipientType)
					.filter(Objects::nonNull)
					.forEach(exclusions::add);
		}
		return exclusions;
	}

	@Override
	protected NotificationRecipientDTO entityToDto(BusinessComponent bc, NotificationRecipient entity) {
		return entityToDto(bc, entity, getExclusions(bc));
	}

	@Override
	public Actions<NotificationRecipientDTO> getActions() {
		return Actions.<NotificationRecipientDTO>builder()
				.create().available(this::isActionCreateAvailable).add()
				.save().add()
				.delete().available(NotificationServiceAssociation.notificationRecipients::isBc).add()
				.build();
	}

	private boolean isActionCreateAvailable(BusinessComponent bc) {
		if (NotificationServiceAssociation.notificationRecipients.isNotBc(bc)) {
			return false;
		}

		// глобальные настройки - проверяем по справочнику
		NotificationSettings settings = baseDAO.findById(NotificationSettings.class, bc.getParentIdAsLong());

		// не указано событие
		if (settings.getEventName() == null) {
			return false;
		}

		return settings.getNotificationRecipients().size() < dictionaryCache
				.getAll(DictionaryType.NOTIFICATION_RECIPIENT_TYPE).size();
	}

	@Override
	protected CreateResult<NotificationRecipientDTO> doCreateEntity(final NotificationRecipient entity,
			final BusinessComponent bc) {
		if (NotificationServiceAssociation.notificationRecipients.isNotBc(bc)) {
			throw new UnsupportedOperationException();
		}
		entity.setNotificationSettings(baseDAO.findById(NotificationSettings.class, bc.getParentIdAsLong()));
		NotificationRecipientDTO result = entityToDto(
				bc,
				baseDAO.findById(NotificationRecipient.class, baseDAO.save(entity))
		);
		return new CreateResult<>(result);
	}

	@Override
	public ActionResultDTO<NotificationRecipientDTO> deleteEntity(BusinessComponent bc) {
		if (NotificationServiceAssociation.notificationRecipients.isNotBc(bc)) {
			throw new UnsupportedOperationException();
		}
		notificationSettingsService.evictCache(isExist(bc.getIdAsLong()).getNotificationSettings());
		return super.deleteEntity(bc);
	}

	@Override
	protected ActionResultDTO<NotificationRecipientDTO> doUpdateEntity(NotificationRecipient recipient,
			NotificationRecipientDTO data, BusinessComponent bc) {
		if (NotificationServiceAssociation.notificationRecipients.isBc(bc)) {
			if (data.isFieldChanged(NotificationRecipientDTO_.recipientType)) {
				LOV recipientType = DictionaryType.NOTIFICATION_RECIPIENT_TYPE.lookupName(data.getRecipientType());
				if (!checkDuplicates(bc, recipient, recipientType)) {
					throw new BusinessException().addPopup(errorMessage("error.notification_recipient_already_exists"));
				}
				recipient.setRecipientType(recipientType);
			}
			if (data.isFieldChanged(NotificationRecipientDTO_.sameDeptOnly)) {
				recipient.setSameDeptOnly(data.isSameDeptOnly());
			}
			notificationSettingsService.evictCache(recipient.getNotificationSettings());
		} else {
			if (data.getEnabled() != null && data.isFieldChanged(NotificationRecipientDTO_.enabled)) {
				NotificationSettings settings = notificationSettingsService
						.getOrCreateUserCopy(recipient.getNotificationSettings());
				if (data.getEnabled()) {
					include(settings, recipient.getRecipientType());
				} else {
					exclude(settings, recipient.getRecipientType());
				}
				notificationSettingsService.evictCache(settings);
			}
		}
		return new ActionResultDTO<>(entityToDto(bc, recipient));
	}

	private boolean checkDuplicates(BusinessComponent bc, NotificationRecipient entity, LOV recipientType) {
		return !baseDAO.exists(
				NotificationRecipient.class,
				(root, cq, cb) -> cb.and(
						cb.equal(root.get(NotificationRecipient_.recipientType), recipientType),
						getParentSpecification(bc).toPredicate(root, cq, cb),
						cb.notEqual(root, entity)
				)
		);
	}

	private void include(NotificationSettings settings, LOV recipientType) {
		baseDAO.delete(NotificationRecipient.class, (root, cq, cb) -> cb.and(
				cb.equal(
						root.get(NotificationRecipient_.notificationSettings).get(NotificationSettings_.id),
						settings.getId()
				),
				cb.equal(
						root.get(NotificationRecipient_.recipientType),
						recipientType
				)
		));
	}

	private NotificationRecipient exclude(NotificationSettings settings, LOV recipientType) {
		NotificationRecipient exclude = new NotificationRecipient();
		exclude.setRecipientType(recipientType);
		exclude.setNotificationSettings(settings);
		exclude = baseDAO.findById(NotificationRecipient.class, baseDAO.save(exclude));
		return exclude;
	}

}
