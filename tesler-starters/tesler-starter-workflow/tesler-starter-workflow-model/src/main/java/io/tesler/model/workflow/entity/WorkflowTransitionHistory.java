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

import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * История переходов
 */
@Getter
@Setter
@Entity
@Table(name = "WF_TRANSITION_HISTORY")
public class WorkflowTransitionHistory extends BaseEntity {

	/**
	 * Активность
	 */
	@ManyToOne
	@JoinColumn(name = "WF_TASK_ID", nullable = false)
	private WorkflowTask workflowTask;

	/**
	 * Переход
	 */
	@Column
	private String transitionName;

	@Column
	private String sourceStepName;

	@Column
	private String destinationStepName;

	/**
	 * Пользователь, выполнившего переход
	 */
	@ManyToOne
	@JoinColumn(name = "TRANSITION_USER_ID", nullable = false)
	private User transitionUser;

	/**
	 * Пользователь, на которого была назначена задача
	 */
	@ManyToOne
	@JoinColumn(name = "PREVIOUS_ASSIGNEE_ID", nullable = false)
	private User previousAssignee;

}
