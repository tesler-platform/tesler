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

package io.tesler.notifications.controller;

import static io.tesler.core.config.properties.APIProperties.TESLER_API_PATH_SPEL;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.util.ResponseBuilder;
import io.tesler.core.util.session.SessionService;
import java.util.List;

import io.tesler.notifications.dao.NotificationDAO;
import io.tesler.notifications.dto.MarkNotificationDTO;
import io.tesler.notifications.service.INotificationPollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(TESLER_API_PATH_SPEL + "/notification")
public class NotificationController {

	@Autowired
	private NotificationDAO notificationDAO;

	@Autowired
	private List<INotificationPollingService> pollingServices;

	@Autowired
	private SessionService sessionService;

	@GetMapping(value = "/get-notifications")
	public ResponseDTO getNotifications(
			@RequestParam(defaultValue = "false") boolean unread,
			@RequestParam(required = false) Long offset,
			PageSpecification page) {
		return ResponseBuilder.build(ResultPage.of(
				notificationDAO.getNotifications(
						sessionService.getSessionUser().getId(),
						unread,
						offset,
						page
				),
				INotificationPollingService::entityToDto
		));
	}

	@GetMapping(value = "/count-notifications")
	public ResponseDTO countNotifications(
			@RequestParam(defaultValue = "false") boolean unread,
			@RequestParam(required = false) Long offset) {
		return ResponseBuilder.build(notificationDAO.countNotifications(
				sessionService.getSessionUser().getId(),
				unread,
				offset
		));
	}

	@GetMapping(value = "/check-new-notification")
	public DeferredResult<ResponseDTO> checkNewNotifications(@RequestParam Long latestNotificationId,
			@RequestParam(defaultValue = "false") boolean unread) {
		DeferredResult<ResponseDTO> result = null;
		for (INotificationPollingService service : pollingServices) {
			result = service.addTaskInQueue(
					sessionService.getSessionUser().getId(), latestNotificationId, unread
			);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	@PostMapping(value = "/mark-notification-as-read")
	public MarkNotificationDTO markNotificationsAsRead(@RequestBody MarkNotificationDTO markNotificationDTO) {
		notificationDAO.markNotificationsAsRead(markNotificationDTO.getNotificationIds(), markNotificationDTO.getMark(),
				sessionService.getSessionUser().getId()
		);
		return markNotificationDTO;
	}

	@DeleteMapping(value = "/delete-notification")
	public List<Long> deleteNotifications(@RequestParam List<Long> notificationId) {
		notificationDAO.deleteNotifications(notificationId, sessionService.getSessionUser().getId());
		return notificationId;
	}

	@PostMapping(value = "/save-notification")
	public void saveNotification(@RequestParam("url") String url, @RequestParam("message") String message) {
		notificationDAO.saveNotification(url, message, sessionService.getSessionUser().getId());
	}

}

