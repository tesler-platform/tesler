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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

/**
 * Шаг модели переходов
 */
@Getter
@Setter
@Entity
@Table(name = "WF_STEP")
public class WorkflowStep extends BaseEntity {

	/**
	 * Версия модели переходов
	 */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "WF_VERSION_ID", nullable = false)
	private WorkflowVersion workflowVersion;

	/**
	 * Статус соответствующий шагу
	 */
	private LOV linkedStatusCd;

	@Formula("LINKED_STATUS_CD")
	private String linkedStatusKey;

	/**
	 * Название шага
	 */
	private String name;

	/**
	 * Переход при истечении срока выполнения шага
	 */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "OVERDUE_TRANSITION_ID")
	private WorkflowTransition overdueTransition;

	@OneToMany(fetch = LAZY, mappedBy = "step")
	private List<WorkflowStepField> stepFields;

	@OneToMany(fetch = LAZY, mappedBy = "workflowStep")
	private List<WorkflowTaskChildBcAvailability> childBcAvailabilities;
}
