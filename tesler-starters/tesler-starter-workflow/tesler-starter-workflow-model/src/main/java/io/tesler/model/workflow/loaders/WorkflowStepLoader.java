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

import io.tesler.model.core.loaders.AbstractObjectLoader;
import io.tesler.model.workflow.entity.WorkflowStep;
import org.springframework.stereotype.Component;


@Component
public class WorkflowStepLoader extends AbstractObjectLoader<WorkflowStep> {

	@Override
	protected Class<? extends WorkflowStep> getType() {
		return WorkflowStep.class;
	}

	@Override
	public WorkflowStep ensureLoaded(WorkflowStep object) {
		return load(object);
	}

}
