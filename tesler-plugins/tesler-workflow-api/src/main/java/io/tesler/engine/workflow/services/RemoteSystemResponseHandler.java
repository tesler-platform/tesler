/*-
 * #%L
 * IO Tesler - Workflow API
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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.workflow.entity.WaitingRemoteSystem;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowTransition;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pf4j.ExtensionPoint;


public interface RemoteSystemResponseHandler extends ExtensionPoint {

	/**
	 * Выполняет обработку финального ответа смежной системы.
	 *
	 * @param correlationId уникальный идентификатор
	 * @param responseCode тип ответа смежной системы
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствие
	 */
	WaitingRemoteSystem handleFinalResponse(String correlationId, ResponseCode responseCode);

	/**
	 * Выполняет обработку промежуточного ответа смежной системы и регистрирует ожидание ответа по новому уникальному идентификатору
	 *
	 * @param correlationId уникальный идентификатор
	 * @param newCorrelationId новый уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствие
	 */
	WaitingRemoteSystem handlePartialResponse(String correlationId, String newCorrelationId, ResponseCode responseCode);

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @param responseCode тип ответа смежной системы
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	WaitingRemoteSystem handlePartialResponse(String correlationId, ResponseCode responseCode);

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @param newCorrelationId новый уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	WaitingRemoteSystem handlePartialResponse(String correlationId, String newCorrelationId);

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	WaitingRemoteSystem handlePartialResponse(String correlationId);

	/**
	 * Возвращает PostFunctionCd по correlationId
	 */
	LOV getPostFunctionByCorrelationId(String correlationId);

	/**
	 * Тип ответа смежной системы
	 */
	@Getter
	@RequiredArgsConstructor
	enum ResponseCode {

		/**
		 * Ответ с кодом 1
		 */
		CODE_1(WorkflowPostFunctionTrigger::getResponseCode1Transition),

		/**
		 * Ответ с кодом 2
		 */
		CODE_2(WorkflowPostFunctionTrigger::getResponseCode2Transition),

		/**
		 * Ответ с кодом 3
		 */
		CODE_3(WorkflowPostFunctionTrigger::getResponseCode3Transition),

		/**
		 * Ответ с кодом 4
		 */
		CODE_4(WorkflowPostFunctionTrigger::getResponseCode4Transition),

		/**
		 * Ответ с кодом 5
		 */
		CODE_5(WorkflowPostFunctionTrigger::getResponseCode5Transition);

		private final Function<WorkflowPostFunctionTrigger, WorkflowTransition> transitionGetter;

	}

}
