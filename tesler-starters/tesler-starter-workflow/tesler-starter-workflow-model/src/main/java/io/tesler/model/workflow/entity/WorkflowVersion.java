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
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Версия модели переходов
 */
@Getter
@Setter
@Entity
@Table(name = "WF_VERSION")
public class WorkflowVersion extends BaseEntity {

	/**
	 * Модель переходов
	 */
	@OneToOne
	@JoinColumn(name = "WF_ID")
	private Workflow workflow;

	/**
	 * Номер версии
	 */
	@Column(name = "VERSION")
	private Double version;

	/**
	 * Описание версии
	 */
	private String description;

	/**
	 * Черновик
	 */
	@Column(name = "DRAFT")
	private boolean draft;

	/**
	 * Начальный шаг
	 */
	@OneToOne
	@JoinColumn(name = "FIRST_WF_STEP_ID")
	private WorkflowStep firstStep;

	/**
	 * Шаг автозакрытия
	 */
	@OneToOne
	@JoinColumn(name = "AUTO_CLOSED_STEP_ID")
	private WorkflowStep autoClosedStep;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "workflowVersion")
	private List<WorkflowStep> workflowSteps;

}
