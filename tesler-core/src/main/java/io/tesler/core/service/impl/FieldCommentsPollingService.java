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

package io.tesler.core.service.impl;

import static io.tesler.api.data.dictionary.CoreDictionaries.SystemPref.FEATURE_COMMENTS;

import io.tesler.api.system.ISystemSettingChangeEventListener;
import io.tesler.api.system.SystemSettingChangedEvent;
import io.tesler.api.system.SystemSettings;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.core.dto.data.FieldCommentDTO;
import io.tesler.core.service.FieldCommentService;
import io.tesler.core.service.FieldCommentsDeferredResult;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component("FieldCommentsPollingService")
public class FieldCommentsPollingService implements ISystemSettingChangeEventListener {

	private final Queue<FieldCommentsDeferredResult> responseBodyQueue = new ConcurrentLinkedQueue<>();

	private boolean enabled;

	@Autowired
	@Lazy
	private FieldCommentService fieldCommentService;

	@Autowired
	private SystemSettings systemSettings;

	@PostConstruct
	protected void init() {
		this.enabled = systemSettings.getBooleanValue(FEATURE_COMMENTS);
	}

	@Override
	public void onApplicationEvent(SystemSettingChangedEvent event) {
		if (FEATURE_COMMENTS.equals(event.getSetting())) {
			this.enabled = systemSettings.getBooleanValue(FEATURE_COMMENTS);
		}
	}

	public void addToQueue(FieldCommentsDeferredResult deferredResult) {
		responseBodyQueue.add(deferredResult);
	}

	@Scheduled(fixedRate = 1000)
	public void executePollTaskInQueue() {
		PrivilegeUtil.runPrivileged(() -> {
			doExecutePollTaskInQueue();
			return null;
		});
	}

	private void doExecutePollTaskInQueue() {
		if (!enabled) {
			return;
		}
		if (!responseBodyQueue.isEmpty()) {
			for (FieldCommentsDeferredResult result : responseBodyQueue) {
				List<FieldCommentDTO> commentsList = fieldCommentService.getList(
						result.getBcNames(),
						result.getTimestamp()
				);
				if (!commentsList.isEmpty()) {
					result.setResult(commentsList);
				}
				if (result.isSetOrExpired()) {
					responseBodyQueue.remove(result);
				}
			}
		}
	}

}
