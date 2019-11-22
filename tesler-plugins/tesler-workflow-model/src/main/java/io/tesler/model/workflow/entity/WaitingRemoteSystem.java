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

package io.tesler.model.workflow.entity;

import static javax.persistence.FetchType.LAZY;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Ожидание ответа от смежных систем
 */
@Getter
@Setter
@Entity
@Table(name = "WAITING_REMOTE_SYSTEM", uniqueConstraints = @UniqueConstraint(columnNames = {"CORRELATION_ID",
		"REQ_POST_FUN_CD"}))
public class WaitingRemoteSystem extends BaseEntity {

	/**
	 * Ожидание ответа с начальным идентификатором
	 */
	@ManyToOne
	@JoinColumn(name = "PARENT_WAITING_ID")
	private WaitingRemoteSystem parentWaiting;

	/**
	 * Уникальный идентификатор
	 */
	@Column(name = "CORRELATION_ID", nullable = false)
	private String correlationId;

	/**
	 * Активность ожидающая ответ
	 */
	@ManyToOne
	@JoinColumn(name = "WF_TASK_ID", nullable = false)
	private WorkflowTask workflowTask;

	/**
	 * Пост-функция совершившая вызов смежной системы
	 */
	@ManyToOne
	@JoinColumn(name = "REQ_POST_FUN_ID", nullable = false)
	private WorkflowPostFunction postFunction;

	/**
	 * Тип пост-функции совершившей вызов смежной системы
	 */
	@Column(name = "REQ_POST_FUN_CD", nullable = false)
	private LOV postFunctionCd;

	/**
	 * Статус
	 */
	@Column(name = "STATUS_CD")
	private LOV statusCd;

	/**
	 * Шаг модели переходов в котором получен ответ
	 */
	@ManyToOne
	@JoinColumn(name = "RESP_STEP_ID")
	private WorkflowStep responseStep;

	/**
	 * Переход совершенный после получения ответа
	 */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "RESP_TRANSITION_HIST_ID")
	private WorkflowTransitionHistory transitionHistory;

	/**
	 * Ожидание ответов с дополнительными идентификаторами
	 */
	@OneToMany(fetch = LAZY, mappedBy = "parentWaiting")
	private List<WaitingRemoteSystem> childWaiting;

}


