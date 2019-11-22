/*-
 * #%L
 * IO Tesler - Workflow Impl
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

package io.tesler.engine.workflow.notification.impl;

import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.CoreDictionaries.NotificationRecipient;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.util.session.SessionService;
import io.tesler.engine.notification.IWorkflowVersionEventGenerator;
import io.tesler.model.core.api.notifications.INotificationEventBuilder;
import io.tesler.model.core.api.notifications.IRecipientResolver;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.listeners.hbn.change.notifications.AbstractEventGenerator;
import io.tesler.model.workflow.entity.WorkflowVersion;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Extension
public class WorkflowVersionEventGenerator extends AbstractEventGenerator<WorkflowVersion> implements
		IWorkflowVersionEventGenerator {

	@Autowired
	private SessionService sessionService;

	private Map<LOV, IRecipientResolver<WorkflowVersion>> recipientResolvers = new ImmutableMap.Builder<LOV, IRecipientResolver<WorkflowVersion>>()
			.put(NotificationRecipient.CURRENT_USER, this::getCurrentUser)
			.build();

	@Override
	public Class<? extends WorkflowVersion> getType() {
		return WorkflowVersion.class;
	}

	@Override
	public void process(IChangeVector vector, LOV event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canProcess(IChangeVector vector, LOV event) {
		return false;
	}

	@Override
	public List<User> getCurrentUser(WorkflowVersion WorkflowVersion, LOV event) {
		return Collections.singletonList(sessionService.getSessionUser());
	}

	@Override
	public INotificationEventBuilder builder(WorkflowVersion WorkflowVersion, LOV event) {
		return new DefaultBuilder(WorkflowVersion, event);
	}

	@Override
	public Map<LOV, IRecipientResolver<WorkflowVersion>> getRecipientResolvers() {
		return recipientResolvers;
	}

}
