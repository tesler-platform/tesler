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

package io.tesler.notifications.crudma.config;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.notifications.crudma.api.NotificationRecipientService;
import io.tesler.notifications.crudma.api.NotificationSettingsService;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public enum NotificationServiceAssociation implements EnumBcIdentifier {

	// @formatter:off



	notificationGlobalSettings(NotificationSettingsService.class),
		notificationRecipients(notificationGlobalSettings, NotificationRecipientService.class),

	notificationUserSettings(NotificationSettingsService.class),
		notificationExcludeRecipients(notificationUserSettings, NotificationRecipientService.class),

	;
	// @formatter:on

	public static final Holder<NotificationServiceAssociation> Holder = new Holder<>(NotificationServiceAssociation.class);

	private final BcDescription bcDescription;

	NotificationServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	NotificationServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	NotificationServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	NotificationServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	NotificationServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	NotificationServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Component
	public static class CoreBcSupplier extends AbstractEnumBcSupplier<NotificationServiceAssociation> {

		public CoreBcSupplier() {
			super(NotificationServiceAssociation.Holder);
		}

	}

}