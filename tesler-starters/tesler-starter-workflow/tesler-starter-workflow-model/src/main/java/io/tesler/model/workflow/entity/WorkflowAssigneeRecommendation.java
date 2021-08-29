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
import io.tesler.model.core.entity.Department;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Правило отбора рекомендованных исполнителей
 */
@Getter
@Setter
@Entity
@Table(name = "WF_ASSIGNEE_RECOMMEND")
public class WorkflowAssigneeRecommendation extends BaseEntity {

	/**
	 * Группа условий
	 */
	@ManyToOne
	@JoinColumn(name = "COND_GROUP_ID", nullable = false)
	private WorkflowStepConditionGroup conditionGroup;

	/**
	 * Правило отбора
	 */
	@Column(name = "COND_ASSIGNEE_CD")
	private LOV condAssigneeCd;

	/**
	 * Департамент
	 */
	@ManyToOne
	@JoinColumn(name = "DEPT_ID")
	private Department department;

	/**
	 * Уровень подразделения
	 */
	@Column(name = "DIVISION_LEVEL_CD")
	private LOV divisionLevelCd;

	/**
	 * Роль
	 */
	@Column(name = "INTERNAL_ROLE_CD")
	private LOV internalRoleCd;

	/**
	 * Описание
	 */
	@Column(name = "DESCRIPTION")
	private String description;

}
