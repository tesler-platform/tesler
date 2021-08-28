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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Настройки атрибутов активности для шага модели переходов
 */
@Getter
@Setter
@Entity
@Table(name = "WF_STEP_FIELD")
public class WorkflowStepField extends BaseEntity {

	/**
	 * Шаг
	 */
	@ManyToOne
	@JoinColumn(name = "WF_STEP_ID", nullable = false)
	private WorkflowStep step;

	@ManyToOne
	@JoinColumn(name = "TASK_FIELD_ID")
	private TaskField taskField;

}
