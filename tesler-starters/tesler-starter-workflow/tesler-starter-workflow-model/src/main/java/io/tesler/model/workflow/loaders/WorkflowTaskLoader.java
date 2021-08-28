/*-
 * #%L
 * IO Tesler - Workflow Model
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

package io.tesler.model.workflow.loaders;

import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.loaders.AbstractObjectLoader;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStep_;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowVersion_;
import io.tesler.model.workflow.entity.Workflow_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WorkflowTaskLoader extends AbstractObjectLoader<WorkflowTask> {

	@Autowired
	private WorkflowStepLoader workflowStepLoader;

	@Autowired
	private JpaDao jpaDao;

	@Override
	protected Class<? extends WorkflowTask> getType() {
		return WorkflowTask.class;
	}

	@Override
	public WorkflowTask ensureLoaded(WorkflowTask object) {
		WorkflowTask workflowTask = load(object);
		if (workflowTask != null) {
			WorkflowStep workflowStep = jpaDao.getFirstResultOrNull(WorkflowStep.class, (root, cq, cb) ->
					cb.and(
							cb.equal(root.get(WorkflowStep_.name), workflowTask.getStepName()),
							cb.equal(root.get(WorkflowStep_.workflowVersion).get(WorkflowVersion_.version), workflowTask.getVersion()),
							cb.equal(
									root.get(WorkflowStep_.workflowVersion).get(WorkflowVersion_.workflow).get(Workflow_.name),
									workflowTask.getWorkflowName()
							)
					)
			);
			workflowStepLoader.ensureLoaded(workflowStep);
		}
		return workflowTask;
	}

}
