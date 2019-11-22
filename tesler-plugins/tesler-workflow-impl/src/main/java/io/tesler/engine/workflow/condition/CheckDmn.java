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

package io.tesler.engine.workflow.condition;

import static java.util.Optional.ofNullable;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.dict.WorkflowDictionaries.WfCondition;
import io.tesler.core.util.DmnEngine;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.springframework.stereotype.Service;

@Extension
@Service
@RequiredArgsConstructor
public class CheckDmn implements ConditionChecker<WorkflowableTask, WorkflowCondition>, ExtensionPoint {

	private final DmnEngine dmnEngine;

	private final WorkflowSettings<?> workflowSettings;

	@Override
	public LOV getType() {
		return WfCondition.DMN;
	}

	@Override
	public boolean check(
			final WorkflowableTask task,
			final WorkflowCondition condition,
			final WorkflowTransition transition) {
		return (boolean) ofNullable(dmnEngine.evaluate(condition.getDmn(), task, workflowSettings.getDtoClass()))
				.map(DmnDecisionTableResult::getFirstResult)
				.map(DmnDecisionRuleResult::getFirstEntry)
				.orElse(false);
	}

}
