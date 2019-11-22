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

package io.tesler.engine.workflow.services;

import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.NOT_RECEIVED;

import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.WaitingRemoteSystem;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowableTask;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Extension
public class WaitingRemoteSystemServiceImpl implements WaitingRemoteSystemService {

	@Autowired
	private JpaDao jpaDao;

	public void wait(final WorkflowableTask task, final WorkflowPostFunction postFunction, final String correlationId) {
		final WaitingRemoteSystem waiting = new WaitingRemoteSystem();
		waiting.setCorrelationId(correlationId);
		waiting.setWorkflowTask(task.getWorkflowTask());
		waiting.setPostFunction(postFunction);
		waiting.setPostFunctionCd(postFunction.getActionCd());
		waiting.setStatusCd(NOT_RECEIVED);
		jpaDao.save(waiting);
	}

	public void wait(final WaitingRemoteSystem srcWaiting, final String newCorrelationId) {
		final WaitingRemoteSystem waiting = new WaitingRemoteSystem();
		waiting.setParentWaiting(getRootWaiting(srcWaiting));
		waiting.setCorrelationId(newCorrelationId);
		waiting.setWorkflowTask(srcWaiting.getWorkflowTask());
		waiting.setPostFunction(srcWaiting.getPostFunction());
		waiting.setPostFunctionCd(srcWaiting.getPostFunctionCd());
		waiting.setStatusCd(NOT_RECEIVED);
		jpaDao.save(waiting);
	}

	public WaitingRemoteSystem getRootWaiting(final WaitingRemoteSystem waiting) {
		WaitingRemoteSystem tmp = waiting;
		while (tmp.getParentWaiting() != null) {
			tmp = tmp.getParentWaiting();
		}
		return tmp;
	}

}
