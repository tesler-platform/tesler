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

package io.tesler.crudma.impl.notifications;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dictionary.CoreDictionaries.NotificationSettingsType;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.notification.CacheableNotificationSettingsProvider;
import io.tesler.core.service.notification.IDeliveryService;
import io.tesler.core.service.notification.impl.DeliveryServiceRegistry;
import io.tesler.core.util.session.SessionService;
import io.tesler.crudma.api.notifications.NotificationSettingsService;
import io.tesler.crudma.config.CoreServiceAssociation;
import io.tesler.crudma.dto.notifications.NotificationSettingsDTO;
import io.tesler.crudma.dto.notifications.NotificationTemplateDTO_;
import io.tesler.crudma.meta.notifications.NotificationSettingsFieldMetaBuilder;
import io.tesler.model.core.entity.notifications.NotificationRecipient;
import io.tesler.model.core.entity.notifications.NotificationRecipient_;
import io.tesler.model.core.entity.notifications.NotificationSettings;
import io.tesler.model.core.entity.notifications.NotificationSettings_;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class NotificationSettingsServiceImpl extends
		VersionAwareResponseService<NotificationSettingsDTO, NotificationSettings> implements NotificationSettingsService {

	@Autowired
	private DeliveryServiceRegistry serviceRegistry;

	@Autowired
	private DictionaryCache dictionaryCache;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private TransactionService txService;

	@Autowired
	private CacheableNotificationSettingsProvider notificationSettingsCache;

	public NotificationSettingsServiceImpl() {
		super(NotificationSettingsDTO.class, NotificationSettings.class, null, NotificationSettingsFieldMetaBuilder.class);
	}


	@Override
	protected ResultPage<NotificationSettingsDTO> entitiesToDtos(BusinessComponent bc,
			ResultPage<NotificationSettings> entities) {
		if (CoreServiceAssociation.notificationGlobalSettings.isBc(bc)) {
			return ResultPage.of(entities, entity -> entityToDto(bc, entity, (NotificationSettings) null));
		}
		Map<LOV, NotificationSettings> userSettings = baseDAO
				.getList(NotificationSettings.class, (root, query, cb) -> cb.and(
						cb.equal(root.get(NotificationSettings_.settingsType), NotificationSettingsType.PERSONAL),
						cb.equal(root.get(NotificationSettings_.userId), sessionService.getSessionUser().getId())
				)).stream().collect(Collectors.toMap(
						NotificationSettings::getEventName,
						Function.identity()
				));
		return ResultPage.of(entities, entity -> entityToDto(bc, entity, userSettings.get(entity.getEventName())));
	}

	protected NotificationSettingsDTO entityToDto(BusinessComponent bc, NotificationSettings entity,
			NotificationSettings userSettings) {
		if (userSettings == null) {
			userSettings = entity;
		}
		NotificationSettingsDTO result = super.entityToDto(bc, entity);
		for (IDeliveryService service : serviceRegistry.getServiceList()) {
			String deliveryType = service.getDeliveryType();
			int serviceId = service.getServiceId();
			boolean enabled = (userSettings.getDeliveryType() & serviceId) == serviceId;
			setDeliveryType(result, deliveryType, enabled);
		}
		return result;
	}

	@Override
	protected NotificationSettingsDTO entityToDto(BusinessComponent bc, NotificationSettings entity) {
		NotificationSettings userSettings = null;
		if (CoreServiceAssociation.notificationGlobalSettings.isNotBc(bc)) {
			userSettings = getUserSettings(entity.getEventName());
		}
		return entityToDto(bc, entity, userSettings);
	}


	@Override
	protected Specification<NotificationSettings> getParentSpecification(BusinessComponent bc) {
		// глобальные настройки
		if (CoreServiceAssociation.notificationGlobalSettings.isBc(bc)) {
			return (root, query, cb) -> cb.equal(
					root.get(NotificationSettings_.settingsType), NotificationSettingsType.GLOBAL
			);
		}
		// пользовательские настройки - выбираем глобальные
		return (root, query, cb) -> cb.and(
				cb.isNotNull(root.get(NotificationSettings_.eventName)),
				cb.equal(root.get(NotificationSettings_.settingsType), NotificationSettingsType.GLOBAL)
		);
	}

	@Override
	protected CreateResult<NotificationSettingsDTO> doCreateEntity(final NotificationSettings entity,
			final BusinessComponent bc) {
		if (CoreServiceAssociation.notificationGlobalSettings.isNotBc(bc)) {
			throw new UnsupportedOperationException();
		}
		entity.setSettingsType(NotificationSettingsType.GLOBAL);
		entity.setDeliveryType(getDefaultDeliveryType());
		return new CreateResult<>(entityToDto(bc, baseDAO.findById(NotificationSettings.class, baseDAO.save(entity))));
	}

	@Override
	protected ActionResultDTO<NotificationSettingsDTO> doUpdateEntity(final NotificationSettings entity,
			NotificationSettingsDTO dto, BusinessComponent bc) {
		// пытаемся менять глобальные настройки из пользовательских - нужно создать копию
		NotificationSettings mutable = entity;
		if (CoreServiceAssociation.notificationGlobalSettings.isNotBc(bc)) {
			mutable = getOrCreateUserCopy(mutable);
		}
		for (IDeliveryService service : serviceRegistry.getServiceList()) {
			Boolean value = getDeliveryTypeValue(dto, service.getDeliveryType());
			if (value == null) {
				continue;
			}
			int deliveryType = mutable.getDeliveryType();
			if (value) {
				deliveryType = deliveryType | service.getServiceId();
			} else {
				deliveryType = deliveryType & (Integer.MAX_VALUE ^ service.getServiceId());
			}
			mutable.setDeliveryType(deliveryType);
		}

		if (dto.isFieldChanged(NotificationTemplateDTO_.eventName)) {
			LOV eventName = DictionaryType.DATABASE_EVENT.lookupName(dto.getEventName());
			if (!checkDuplicates(mutable, eventName)) {
				throw new BusinessException().addPopup(errorMessage("error.notification_setting_already_exists"));
			}
			mutable.setEventName(eventName);
		}
		evictCache(mutable);
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<NotificationSettingsDTO> getActions() {
		return Actions.<NotificationSettingsDTO>builder()
				.create().available(this::isActionCreateAvailable).add()
				.save().add()
				.delete().available(CoreServiceAssociation.notificationGlobalSettings::isBc).add()
				.action("reset", "Сбросить")
				.available(this::isActionResetAvailable).invoker(this::resetSettings).add()
				.action("reset-all", "Сбросить все")
				.available(CoreServiceAssociation.notificationGlobalSettings::isNotBc).invoker(this::resetAllSettings).add()
				.build();
	}

	private boolean isActionResetAvailable(BusinessComponent bc) {
		Long id = bc.getIdAsLong();
		if (id == null || CoreServiceAssociation.notificationGlobalSettings.isBc(bc)) {
			return false;
		}
		NotificationSettings entity = baseDAO.findById(NotificationSettings.class, id);
		return getUserSettings(entity.getEventName()) != null;
	}

	private ActionResultDTO<NotificationSettingsDTO> resetAllSettings(final BusinessComponent bc,
			final NotificationSettingsDTO data) {
		if (CoreServiceAssociation.notificationGlobalSettings.isBc(bc)) {
			throw new UnsupportedOperationException();
		}
		baseDAO.getStream(NotificationSettings.class, (root, cq, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.settingsType), NotificationSettingsType.PERSONAL),
				cb.equal(root.get(NotificationSettings_.userId), sessionService.getSessionUser().getId())
		)).forEach(
				this::delete
		);
		return new ActionResultDTO<NotificationSettingsDTO>().setAction(PostAction.refreshBc(bc));
	}

	private ActionResultDTO<NotificationSettingsDTO> resetSettings(final BusinessComponent bc,
			final NotificationSettingsDTO data) {
		if (CoreServiceAssociation.notificationGlobalSettings.isBc(bc)) {
			throw new UnsupportedOperationException();
		}
		Long id = bc.getIdAsLong();
		NotificationSettings entity = baseDAO.findById(NotificationSettings.class, id);
		entity = getUserSettings(entity.getEventName());
		if (entity != null) {
			delete(entity);
		}
		return new ActionResultDTO<NotificationSettingsDTO>().setAction(PostAction.refreshBc(bc));
	}

	private boolean isActionCreateAvailable(BusinessComponent bc) {
		// создавать можно только глобальные
		if (CoreServiceAssociation.notificationGlobalSettings.isNotBc(bc)) {
			return false;
		}
		// и только если остались значения в справочнике
		return baseDAO.getCount(NotificationSettings.class, getParentSpecification(null)) < dictionaryCache
				.getAll(DictionaryType.DATABASE_EVENT).size();
	}

	private boolean checkDuplicates(NotificationSettings entity, LOV eventName) {
		return !baseDAO.exists(NotificationSettings.class, (root, cq, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.eventName), eventName),
				cb.notEqual(root, entity),
				getParentSpecification(null).toPredicate(
						root, cq, cb
				)
		));
	}

	@Override
	public ActionResultDTO<NotificationSettingsDTO> deleteEntity(BusinessComponent bc) {
		NotificationSettings settings = isExist(bc.getIdAsLong());
		delete(settings);
		return new ActionResultDTO<>();
	}

	private void delete(NotificationSettings entity) {
		deleteRecipients(entity);
		baseDAO.delete(entity);
		evictCache(entity);
	}

	private void deleteRecipients(NotificationSettings settings) {
		baseDAO.delete(
				NotificationRecipient.class,
				(root, query, cb) -> cb.equal(
						root.get(NotificationRecipient_.notificationSettings).get(NotificationSettings_.id),
						settings.getId()
				)
		);
	}

	private int getDefaultDeliveryType() {
		int result = 0;
		for (IDeliveryService service : serviceRegistry.getServiceList()) {
			int serviceId = service.getServiceId();
			if (service.isActive()) {
				result = (result | serviceId);
			}
		}
		return result;
	}

	@SneakyThrows
	private Boolean getDeliveryTypeValue(NotificationSettingsDTO dto, String deliveryType) {
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(dto.getClass(), deliveryType);
		if (descriptor == null) {
			return null;
		}
		Method method = BeanUtils.getPropertyDescriptor(dto.getClass(), deliveryType).getReadMethod();
		return (Boolean) method.invoke(dto);
	}

	@SneakyThrows
	private void setDeliveryType(NotificationSettingsDTO dto, String deliveryType, boolean enabled) {
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(dto.getClass(), deliveryType);
		if (descriptor != null) {
			Method method = BeanUtils.getPropertyDescriptor(dto.getClass(), deliveryType).getWriteMethod();
			method.invoke(dto, enabled);
		}
	}

	public void evictCache(NotificationSettings settings) {
		if (NotificationSettingsType.GLOBAL.equals(settings.getSettingsType())) {
			txService.invokeAfterCompletion(Invoker.of(
					() -> notificationSettingsCache.evict(settings.getEventName())
			));
		} else if (NotificationSettingsType.PERSONAL.equals(settings.getSettingsType())) {
			txService.invokeAfterCompletion(Invoker.of(
					() -> notificationSettingsCache.evict(settings.getEventName(), settings.getUserId())
			));
		}
	}

	@Override
	public NotificationSettings getOrCreateUserCopy(NotificationSettings entity) {
		NotificationSettings copy = getUserSettings(entity.getEventName());
		if (copy != null) {
			return copy;
		}
		copy = new NotificationSettings();
		copy.setSettingsType(NotificationSettingsType.PERSONAL);
		copy.setUserId(sessionService.getSessionUser().getId());
		copy.setDeliveryType(entity.getDeliveryType());
		copy.setEventName(entity.getEventName());
		return baseDAO.findById(NotificationSettings.class, baseDAO.save(copy));
	}

	@Override
	public NotificationSettings getGlobalSettings(LOV event) {
		return baseDAO.getFirstResultOrNull(NotificationSettings.class, (root, cq, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.eventName), event),
				cb.equal(root.get(NotificationSettings_.settingsType), NotificationSettingsType.GLOBAL)
		));
	}

	@Override
	public NotificationSettings getUserSettings(LOV event) {
		return baseDAO.getFirstResultOrNull(NotificationSettings.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(NotificationSettings_.settingsType), NotificationSettingsType.PERSONAL),
				cb.equal(root.get(NotificationSettings_.userId), sessionService.getSessionUser().getId()),
				cb.equal(root.get(NotificationSettings_.eventName), event)
		));
	}

}
