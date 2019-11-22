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

package io.tesler.crudma.config;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.crudma.api.BcPropertiesService;
import io.tesler.crudma.api.BcService;
import io.tesler.crudma.api.DeptService;
import io.tesler.crudma.api.FilterGroupService;
import io.tesler.crudma.api.ScreenService;
import io.tesler.crudma.api.WidgetService;
import io.tesler.crudma.api.notifications.NotificationRecipientService;
import io.tesler.crudma.api.notifications.NotificationSettingsService;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Getter
public enum CoreServiceAssociation implements EnumBcIdentifier {

	// @formatter:off

	department(DeptService.class),

	filterGroup(FilterGroupService.class),

	bcProperties(BcPropertiesService.class),

	notificationGlobalSettings(NotificationSettingsService.class),
		notificationRecipients(notificationGlobalSettings, NotificationRecipientService.class),

	notificationUserSettings(NotificationSettingsService.class),
		notificationExcludeRecipients(notificationUserSettings, NotificationRecipientService.class),

	// ui
	screen(ScreenService.class),
	widget(WidgetService.class),
	bc(BcService.class),

	;
	// @formatter:on

	public static final Holder<CoreServiceAssociation> Holder = new Holder<>(CoreServiceAssociation.class);

	private final BcDescription bcDescription;

	CoreServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	CoreServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	CoreServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	CoreServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	CoreServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	CoreServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Component
	public static class CoreBcSupplier extends AbstractEnumBcSupplier<CoreServiceAssociation> {

		public CoreBcSupplier() {
			super(CoreServiceAssociation.Holder);
		}

	}

}
