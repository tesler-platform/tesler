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

import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_AND_ERROR;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_AND_SUCCESS;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_PARTIALLY;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_PARTIALLY_AND_ERROR;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.exception.IntegrationException;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.model.workflow.entity.WaitingRemoteSystem;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Extension
@RequiredArgsConstructor
public class RemoteSystemResponseHandlerImpl implements RemoteSystemResponseHandler {

	private final WorkflowEngine workflowEngine;

	private final WorkflowDaoImpl workflowDao;

	private final WorkflowableTaskDao<?> workflowableTaskDao;

	private final WaitingRemoteSystemService waitingRemoteSystemService;

	/**
	 * Выполняет обработку финального ответа смежной системы.
	 *
	 * @param correlationId уникальный идентификатор
	 * @param responseCode тип ответа смежной системы
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствие
	 */
	@Override
	@Transactional
	public WaitingRemoteSystem handleFinalResponse(final String correlationId, final ResponseCode responseCode) {
		final WaitingRemoteSystem waiting = workflowDao.getWaitingRemoteSystem(correlationId);
		if (waiting == null) {
			log.warn(String.format(
					"Нет активностей ожидающих ответа:%n\tcorrelationId: %s%n\tresponseCode: %s",
					correlationId,
					responseCode
			));
			throw new IntegrationException(
					"Нет активностей ожидающих ответа: correlationId: " + correlationId + ", responseCode: " + responseCode);
		}
		final WorkflowTask workflowTask = waiting.getWorkflowTask();
		waiting.setResponseStep(workflowTask.getWorkflowStep());

		final WorkflowPostFunctionTrigger trigger = workflowDao
				.getPostFunctionTrigger(waiting.getPostFunction(), workflowTask.getWorkflowStep());
		if (trigger == null) {
			waiting.setStatusCd(RECEIVED_AND_ERROR);
			log.warn(String.format(
					"Для шага %s не предусмотрено ожидание ответа:%n\tcorrelationId: %s%n\tresponseCode: %s",
					workflowTask.getWorkflowStep().getName(),
					correlationId,
					responseCode
			));
		} else {
			try {
				final TransitionResult transitionResult = workflowEngine.invokeAutoTransition(
						workflowableTaskDao.getTask(workflowTask),
						responseCode.getTransitionGetter().apply(trigger)
				);
				setSuccessStatus(waiting);
				waiting.setTransitionHistory(transitionResult.getTransitionHistory());
			} catch (Exception e) {
				waiting.setStatusCd(RECEIVED_AND_ERROR);
				log.error(e.getLocalizedMessage(), e);
			}
		}
		return waiting;
	}

	private void setSuccessStatus(final WaitingRemoteSystem waiting) {
		final WaitingRemoteSystem rootWaiting = waitingRemoteSystemService.getRootWaiting(waiting);
		rootWaiting.setStatusCd(RECEIVED_AND_SUCCESS);
		for (WaitingRemoteSystem childWaiting : rootWaiting.getChildWaiting()) {
			childWaiting.setStatusCd(RECEIVED_AND_SUCCESS);
		}
	}

	/**
	 * Выполняет обработку промежуточного ответа смежной системы и регистрирует ожидание ответа по новому уникальному идентификатору
	 *
	 * @param correlationId уникальный идентификатор
	 * @param newCorrelationId новый уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствие
	 */
	@Override
	@Transactional
	public WaitingRemoteSystem handlePartialResponse(final String correlationId, final String newCorrelationId,
			final ResponseCode responseCode) {
		final WaitingRemoteSystem waiting = workflowDao.getWaitingRemoteSystem(correlationId);
		if (waiting == null) {
			log.warn(String.format(
					"Нет активностей ожидающих ответа:\n\tcorrelationId: %s\n\tnewCorrelationId: %s",
					correlationId,
					newCorrelationId
			));
			throw new IntegrationException(
					"Нет активностей ожидающих ответа: correlationId: " + correlationId + ", newCorrelationId: "
							+ newCorrelationId);
		}
		if (newCorrelationId != null) {
			waitingRemoteSystemService.wait(waiting, newCorrelationId);
		}
		final WorkflowTask workflowTask = waiting.getWorkflowTask();
		waiting.setResponseStep(workflowTask.getWorkflowStep());

		if (responseCode != null) {
			final WorkflowPostFunctionTrigger trigger = workflowDao
					.getPostFunctionTrigger(waiting.getPostFunction(), workflowTask.getWorkflowStep());
			if (trigger == null) {
				waiting.setStatusCd(RECEIVED_PARTIALLY_AND_ERROR);
				log.warn(String.format(
						"Для шага %s не предусмотрено ожидание ответа:%n\tcorrelationId: %s%n\tresponseCode: %s",
						workflowTask.getWorkflowStep().getName(),
						correlationId,
						responseCode
				));
			} else {
				try {
					final TransitionResult transitionResult = workflowEngine.invokeAutoTransition(
							workflowableTaskDao.getTask(workflowTask),
							responseCode.getTransitionGetter().apply(trigger)
					);
					waiting.setStatusCd(RECEIVED_PARTIALLY);
					waiting.setTransitionHistory(transitionResult.getTransitionHistory());
				} catch (Exception e) {
					waiting.setStatusCd(RECEIVED_PARTIALLY_AND_ERROR);
					log.error(e.getLocalizedMessage(), e);
				}
			}
		} else {
			waiting.setStatusCd(RECEIVED_PARTIALLY);
		}
		return waiting;
	}

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @param responseCode тип ответа смежной системы
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	@Override
	@Transactional
	public WaitingRemoteSystem handlePartialResponse(final String correlationId, final ResponseCode responseCode) {
		return handlePartialResponse(correlationId, null, responseCode);
	}

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @param newCorrelationId новый уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	@Override
	@Transactional
	public WaitingRemoteSystem handlePartialResponse(final String correlationId, final String newCorrelationId) {
		return handlePartialResponse(correlationId, newCorrelationId, null);
	}

	/**
	 * Выполняет обработку промежуточного ответа смежной системы
	 *
	 * @param correlationId уникальный идентификатор
	 * @return активность ожидающую ответа от смежной системы или null при её отсутствии
	 */
	@Override
	@Transactional
	public WaitingRemoteSystem handlePartialResponse(final String correlationId) {
		return handlePartialResponse(correlationId, null, null);
	}

	/**
	 * Возвращает PostFunctionCd по correlationId
	 */
	@Override
	public LOV getPostFunctionByCorrelationId(final String correlationId) {
		final WaitingRemoteSystem waiting = workflowDao.getWaitingRemoteSystem(correlationId);
		if (waiting == null) {
			log.warn(String.format("Нет активностей ожидающих ответа:\n\tcorrelationId: %s", correlationId));
			return null;
		}
		return waiting.getPostFunctionCd();
	}

}
