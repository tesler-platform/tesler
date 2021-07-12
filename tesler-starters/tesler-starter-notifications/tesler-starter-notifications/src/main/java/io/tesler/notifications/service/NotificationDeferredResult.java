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

package io.tesler.notifications.service;

import io.tesler.core.dto.ResponseDTO;
import java.time.ZoneId;
import java.util.Collections;
import lombok.Getter;
import org.springframework.web.context.request.async.DeferredResult;

@Getter
public class NotificationDeferredResult extends DeferredResult<ResponseDTO> {

	private static final Long TIME_OUT_MS = 20000L;

	private final Long recipientId;

	private final Long latestNotificationId;

	private final boolean unread;

	private final ZoneId zoneId;

	public NotificationDeferredResult(Long recipientId, Long latestNotificationId, ZoneId zoneId, boolean unread) {
		super(latestNotificationId > 0 ? TIME_OUT_MS : null, Collections.emptyList());
		this.recipientId = recipientId;
		this.latestNotificationId = latestNotificationId;
		this.zoneId = zoneId;
		this.unread = unread;
	}

}

