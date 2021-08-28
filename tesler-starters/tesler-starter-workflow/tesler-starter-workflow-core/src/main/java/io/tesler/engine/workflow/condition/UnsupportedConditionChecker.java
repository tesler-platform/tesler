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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import org.springframework.stereotype.Service;

/**
 * Нереализованное условие
 */
@Service
public class UnsupportedConditionChecker implements ConditionChecker<WorkflowableTask, WorkflowCondition> {

	@Override
	public LOV getType() {
		return null;
	}

	@Override
	public boolean check(final WorkflowableTask task, final WorkflowCondition condition,
			final WorkflowTransition transition) {
		if (condition.getCondCd() == null) {
			throw new UnsupportedOperationException(String.format(
					"Не задан тип условия, задача: %d, условие: %d",
					task.getId(),
					condition.getId()
			));
		}
		throw new UnsupportedOperationException(String.format(
				"Проверка условия %s не реализована, задача: %d, условие: %d",
				condition.getCondCd().getKey(),
				task.getId(),
				condition.getId()
		));
	}

}
