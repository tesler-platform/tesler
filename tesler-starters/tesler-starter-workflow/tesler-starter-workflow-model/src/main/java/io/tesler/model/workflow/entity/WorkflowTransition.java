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
import io.tesler.model.core.hbn.ExtSequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * Переход
 */
@Getter
@Setter
@Entity
@Table(name = "WF_TRANSITION")
@ExtSequenceGenerator(
		parameters = {
				@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "META_SEQ"),
				@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "1"),
				@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "100"),
				@Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = OptimizerFactory.POOL_LO)
		}
)
public class WorkflowTransition extends BaseEntity {

	/**
	 * Исходный шаг
	 */
	@ManyToOne
	@JoinColumn(name = "WF_STEP_ID", nullable = false)
	private WorkflowStep sourceStep;

	/**
	 * Шаг назначения
	 */
	@ManyToOne
	@JoinColumn(name = "WF_DEST_STEP_ID", nullable = false)
	private WorkflowStep destinationStep;

	/**
	 * Название перехода
	 */
	private String name;

	/**
	 * Button text
	 */
	private String text;

	/**
	 * Order
	 */
	private Long seq;

	/**
	 * Icon Code
	 */
	private String iconCode;

	/**
	 * Идентификатор группы кнопок
	 */
	@ManyToOne
	@JoinColumn(name = "WF_TRANSITION_GROUP_ID")
	private WorkflowTransitionGroup workflowTransitionGroup;

	/**
	 * Проверка заполнения обязательных полей
	 */
	private Boolean checkRequiredFields;

	/**
	 * Фоновое выполнение
	 */
	@Column(name = "BACKGROUND_EXECUTION")
	private Boolean backgroundExecution;

}
