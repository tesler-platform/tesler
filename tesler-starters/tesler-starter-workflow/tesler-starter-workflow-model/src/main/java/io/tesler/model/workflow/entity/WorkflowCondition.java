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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.hbn.ExtSequenceGenerator;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@Getter
@Setter
@Entity
@Table(name = "WF_COND")
@ExtSequenceGenerator(
		parameters = {
				@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "META_SEQ"),
				@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "1"),
				@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "100"),
				@Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = OptimizerFactory.POOL_LO)
		}
)
public class WorkflowCondition extends BaseEntity {

	/**
	 * Тип связи условия(кто parent)
	 */
	private LOV condLinkType;

	/**
	 * Настройки атрибута активности для шага модели переходов
	 */
	@ManyToOne
	@JoinColumn(name = "WF_STEP_FIELD_ID")
	private WorkflowStepField stepField;

	/**
	 * Настройки атрибута активности для блокировки дочерних виджетов
	 */
	@ManyToOne
	@JoinColumn(name = "CHILD_BC_ID")
	private WorkflowTaskChildBcAvailability wfChildBcAvailability;

	/**
	 * Группа условий
	 */
	@ManyToOne
	@JoinColumn(name = "STP_COND_GROUP_ID")
	private WorkflowStepConditionGroup stepConditionGroup;

	/**
	 * Группа условий
	 */
	@ManyToOne
	@JoinColumn(name = "TR_COND_GROUP_ID")
	private WorkflowTransitionConditionGroup transitionConditionGroup;

	/**
	 * Порядок
	 */
	private Long seq;

	/**
	 * Тип условия
	 */
	private LOV condCd;

	/**
	 * DMN проверка
	 */
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String dmn;

}
