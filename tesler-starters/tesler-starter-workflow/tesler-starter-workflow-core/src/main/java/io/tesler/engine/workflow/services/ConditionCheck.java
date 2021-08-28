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

import static com.google.common.collect.ImmutableMap.builder;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.engine.workflow.condition.ConditionChecker;
import io.tesler.engine.workflow.condition.UnsupportedConditionChecker;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ConditionCheck {

	private final ConditionChecker<? extends WorkflowableTask, ? extends WorkflowCondition> defaultConditionChecker;

	private final Map<LOV, ConditionChecker<? extends WorkflowableTask, ? extends WorkflowCondition>> conditionCheckers;

	ConditionCheck(
			final UnsupportedConditionChecker defaultConditionChecker,
			final List<ConditionChecker<? extends WorkflowableTask, ? extends WorkflowCondition>> conditionCheckers) {
		this.defaultConditionChecker = defaultConditionChecker;

		final Builder<LOV, ConditionChecker<? extends WorkflowableTask, ? extends WorkflowCondition>> builder = builder();
		for (final ConditionChecker<? extends WorkflowableTask, ? extends WorkflowCondition> conditionChecker : conditionCheckers) {
			if (conditionChecker.getType() != null) {
				builder.put(conditionChecker.getType(), conditionChecker);
			}
		}
		this.conditionCheckers = builder.build();
	}

	boolean isAvailable(final WorkflowableTask task, final Collection<? extends WorkflowCondition> conditions,
			final WorkflowTransition transition) {
		for (final WorkflowCondition condition : conditions) {
			final ConditionChecker conditionChecker = conditionCheckers.getOrDefault(
					condition.getCondCd(),
					defaultConditionChecker
			);
			if (!conditionChecker.check(task, condition, transition)) {
				log.debug("Условие {} id: {} не выполнено", condition.getCondCd().getKey(), condition.getId());
				return false;
			}
			log.debug("Условие {} id: {} выполнено", condition.getCondCd().getKey(), condition.getId());
		}
		return true;
	}

}
