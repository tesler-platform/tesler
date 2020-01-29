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

package io.tesler.model.core.listeners.hbn.change.notifications;

import io.tesler.api.data.dao.databaselistener.IChangeListener;
import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.notification.EventSettings;
import io.tesler.api.notification.INotificationTemplate;
import io.tesler.api.notification.NotificationEvent;
import io.tesler.api.notification.NotificationSettingsProvider;
import io.tesler.api.notification.NotificationTemplateSupport;
import io.tesler.api.notification.Recipient;
import io.tesler.api.service.AsyncService;
import io.tesler.api.service.ObjectLoader;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.api.notifications.EventRecipientInterceptor;
import io.tesler.model.core.api.notifications.INotificationEventBuilder;
import io.tesler.model.core.api.notifications.IRecipientResolver;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;


@Slf4j
public abstract class AbstractEventGenerator<E extends BaseEntity> implements IChangeListener<E> {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	protected ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private CurrentUserAware<User> currentUserAware;

	@Autowired
	private NotificationTemplateSupport templateSupport;

	@Autowired
	private NotificationSettingsProvider notificationSettingsProvider;

	@Autowired
	private List<ObjectLoader> loaders;

	@Autowired
	private TransactionService txService;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private Optional<List<EventRecipientInterceptor>> deliveryInterceptors;

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	protected void publish(NotificationEvent event) {
		applicationEventPublisher.publishEvent(event);
	}

	private boolean isSendAsync() {
		return deliveryInterceptors.map(
				list -> list.stream().anyMatch(EventRecipientInterceptor::isSendAsync)
		).orElse(false);
	}

	private boolean isCurrentUserAction() {
		return deliveryInterceptors.map(
				list -> list.stream().anyMatch(EventRecipientInterceptor::isCurrentUserAction)
		).orElse(false);
	}

	protected final NotificationEvent createEvent(LOV eventName) {
		return new NotificationEvent(this).setEvent(eventName);
	}

	protected Map<LOV, IRecipientResolver<E>> getRecipientResolvers() {
		return Collections.emptyMap();
	}

	private List<User> getRecipients(E entity, LOV event, LOV recipientRole) {
		IRecipientResolver<E> provider = getRecipientResolvers().get(recipientRole);
		if (provider == null) {
			return Collections.emptyList();
		}
		return provider.getRecipients(entity, event);
	}

	protected User getPerformer() {
		return getPerformer(null);
	}

	protected User getPerformer(E entity) {
		if (entity == null) {
			return currentUserAware.getCurrentUser();
		}
		Long author = entity.getLastUpdBy();
		if (author != null) {
			return jpaDao.findById(User.class, author);
		}
		author = entity.getCreatedBy();
		if (author != null) {
			return jpaDao.findById(User.class, author);
		}
		return currentUserAware.getCurrentUser();
	}

	public class DefaultBuilder implements INotificationEventBuilder {

		protected final E entity;

		protected final LOV event;

		protected Map<String, Object> model = new HashMap<>();

		protected List<User> excludeUsers = new ArrayList<>();

		private LOV mimeType;

		private Map<User, Set<LOV>> recipients = new HashMap<>();

		private boolean defaultRecipients = true;

		public DefaultBuilder(E entity, LOV event) {
			this.entity = ensureLoaded(entity);
			this.event = event;
		}

		@Override
		public INotificationEventBuilder setMimeType(LOV mimeType) {
			this.mimeType = mimeType;
			return this;
		}

		@Override
		public INotificationEventBuilder addDefaultRecipients(boolean value) {
			defaultRecipients = value;
			return null;
		}

		public INotificationEventBuilder addRecipients(LOV recipientRole, Collection<User> users) {
			users.stream().filter(Objects::nonNull).forEach(user ->
					addRecipient(recipientRole, user)
			);
			return this;
		}

		public INotificationEventBuilder addRecipient(LOV recipientRole) {
			addRecipients(recipientRole, getRecipients(entity, event, recipientRole));
			return this;
		}

		public INotificationEventBuilder addRecipient(LOV recipientRole, User user) {
			recipients.computeIfAbsent(ensureLoaded(user), k -> new HashSet<>()).add(recipientRole);
			return this;
		}

		public INotificationEventBuilder addModel(String name, Object value) {
			if (value != null) {
				model.put(name, ensureLoaded(value));
			}
			return this;
		}

		public INotificationEventBuilder addModel(Map<String, Object> model) {
			model.forEach(this::addModel);
			return this;
		}

		@Override
		public INotificationEventBuilder setPerformer(User user) {
			addModel("performer", user);
			return this;
		}

		@Override
		public INotificationEventBuilder excludeUser(User user) {
			if (user != null) {
				excludeUsers.add(user);
			}
			return this;
		}

		@Override
		public INotificationEventBuilder excludePerformer() {
			return excludeUser(getPerformer());
		}

		@Override
		public void publish() {
			// по-умолчанию делаем асинхронную доставку
			// только если действие произошло из UI,
			// чтобы не забивать очередь
			publish(isSendAsync());
		}

		@Override
		public final void publish(boolean async) {
			// считаем заранее что у нас за действие
			boolean isCurrentUserAction = isCurrentUserAction();
			Invoker<Void, RuntimeException> invoker = Invoker.of(() -> doPublish(isCurrentUserAction));
			if (async) {
				asyncService.<Void, RuntimeException>invokeAsync(() -> txService.invokeInTx(invoker));
			} else {
				invoker.invoke();
			}
		}

		private Map<String, Object> getModelFor(User recipient, Set<LOV> recipientRoles) {
			return ImmutableMap.<String, Object>builder()
					.put("entity", entity)
					.put("recipient", recipient)
					.put("recipient_roles", recipientRoles)
					.putAll(model).build();
		}

		private void doPublish(boolean isCurrentUserAction) {
			if (entity == null) {
				return;
			}

			EventSettings settings = notificationSettingsProvider.getGlobalSettings(event);

			if (settings == null) {
				return;
			}

			INotificationTemplate template = templateSupport.getTemplate(event);
			if (template == null && templateSupport.isTemplateRequired(model)) {
				return;
			}

			if (defaultRecipients) {
				for (Recipient recipient : settings.getRecipients()) {
					addRecipients(
							recipient.getRole(),
							getRecipients(entity, event, recipient.getRole())
					);
				}
			}

			if (recipients.isEmpty()) {
				return;
			}

			recipients.forEach((recipient, roles) -> {
				if (recipient != null && roles != null && !roles.isEmpty()) {
					NotificationEvent event = buildNotificationEvent(settings, template, recipient, roles, isCurrentUserAction);
					if (event != null) {
						applicationEventPublisher.publishEvent(event);
					}
				}
			});
		}

		private NotificationEvent buildNotificationEvent(
				EventSettings globalSettings,
				INotificationTemplate template,
				User user,
				Set<LOV> roles,
				boolean userLess) {
			int deliveryType = getDeliveryType(globalSettings, user, roles, userLess);
			if (deliveryType <= 0) {
				return null;
			}

			Map<String, Object> data = getModelFor(user, roles);
			NotificationEvent notificationEvent = createEvent(event);
			notificationEvent.setDeliveryType(deliveryType);
			notificationEvent.setRecipientId(user.getId());
			notificationEvent.setMimeType(getMimeType(template));
			return templateSupport.processTemplate(template, data, notificationEvent);
		}

		private LOV getMimeType(INotificationTemplate template) {
			if (mimeType != null) {
				return mimeType;
			} else if (template != null) {
				return template.getMimeType();
			}
			return CoreDictionaries.MimeType.TEXT;
		}

		private int getDeliveryType(
				EventSettings globalSettings,
				User user,
				Set<LOV> recipientRoles,
				boolean isCurrentUserAction) {

			deliveryInterceptors.ifPresent(list -> list.forEach(
					interceptor -> interceptor.modifyRoles(globalSettings, recipientRoles, user)
			));

			// рассылать некому
			if (recipientRoles.isEmpty()) {
				return 0;
			}

			EventSettings settings = notificationSettingsProvider.getUserSettings(event, user.getId());
			if (settings == null) {
				settings = globalSettings;
			}

			// определяем следует ли пользователю посылать
			// уведомление по следующим критериям:
			// - пользователь хочет получать уведомления
			//   по собственным изменениям
			// - в UI нажат invoke или вообще не из UI
			// - получатель - не текущий пользователь
			boolean send = !isCurrentUserAction && !excludeUsers.contains(user);
			send |= settings.isNotifyMyself();
			send |= !user.equals(currentUserAware.getCurrentUser());

			if (send) {
				return settings.getDeliveryType();
			}

			return 0;
		}

		private <T> T ensureLoaded(T object) {
			return loaders.stream()
					.filter(loader -> loader.accept(object))
					.findFirst()
					.map(loader -> (T) loader.ensureLoaded(object))
					.orElse(object);
		}

	}


}
