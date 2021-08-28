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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.dto.PreInvokeEvent;
import io.tesler.engine.workflow.preinvoke.AlwaysPreInvoke;
import io.tesler.model.workflow.entity.WorkflowTransitionValidation;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class PreInvoker {

	private static final String PRE_INVOKE_KEY_SEPARATOR = "_";

	private final PreInvokeConditionChecker<? extends WorkflowableTask> defaultConditionChecker;

	private final Map<LOV, PreInvokeConditionChecker<? extends WorkflowableTask>> preInvokeConditionCheckers;

	PreInvoker(
			final AlwaysPreInvoke defaultConditionChecker,
			final List<PreInvokeConditionChecker<? extends WorkflowableTask>> conditionCheckers) {
		this.defaultConditionChecker = defaultConditionChecker;

		final Builder<LOV, PreInvokeConditionChecker<? extends WorkflowableTask>> builder = ImmutableMap.builder();
		for (final PreInvokeConditionChecker<? extends WorkflowableTask> conditionChecker : conditionCheckers) {
			if (conditionChecker.getType() != null) {
				builder.put(conditionChecker.getType(), conditionChecker);
			}
		}
		this.preInvokeConditionCheckers = builder.build();
	}

	PreInvokeEvent invoke(final WorkflowableTask task, final WorkflowTransitionValidation transitionValidation,
			final Collection<String> confirmedPreInvokeKeys) {
		final String preInvokeKey = getPreInvokeKey(transitionValidation);
		log.debug("Проверка подтверждений перехода '{}'", preInvokeKey);
		if (isNull(confirmedPreInvokeKeys) || !confirmedPreInvokeKeys.contains(preInvokeKey)) {
			log.debug("Подтверждение перехода '{}' не получено", preInvokeKey);
			final LOV preInvokeTypeCd = transitionValidation.getPreInvokeTypeCd();
			if (nonNull(preInvokeTypeCd) && isNotBlank(preInvokeTypeCd.getKey())) {
				final PreInvokeConditionChecker conditionChecker = preInvokeConditionCheckers.getOrDefault(
						transitionValidation.getPreInvokeCondCd(), defaultConditionChecker
				);
				if (conditionChecker.check(task, transitionValidation)) {
					log.debug(
							"Условие {} id: {} для вызова диалогового окна выполнено",
							transitionValidation.getPreInvokeCondCd(),
							transitionValidation.getId()
					);
					return PreInvokeEvent.of(preInvokeKey, preInvokeTypeCd.getKey(), transitionValidation.getPreInvokeMessage());
				} else {
					log.debug(
							"Условие {} id: {} для вызова диалогового окна не выполнено",
							transitionValidation.getPreInvokeCondCd(),
							transitionValidation.getId()
					);
				}
			} else {
				log.debug("Подтверждение перехода '{}' получено", preInvokeKey);
			}
		}
		return null;
	}

	private String getPreInvokeKey(WorkflowTransitionValidation wfValidation) {
		return wfValidation.getValidCd().getKey().toLowerCase() + PRE_INVOKE_KEY_SEPARATOR + wfValidation.getId();
	}

}
