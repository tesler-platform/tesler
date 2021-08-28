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
import io.tesler.model.core.entity.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Getter
@Setter
@MappedSuperclass
public abstract class WorkflowableTask extends BaseEntity {

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WF_TASK_ID")
	private WorkflowTask workflowTask;

	@Column(name = "AUTOMATIC_TRANSITION_NAME")
	private String automaticTransitionName;

	@Column(name = "STEP_TERM")
	private LocalDateTime stepTerm;

	@Column(name = "TEMPLATE_FLG")
	private boolean templateFlg;

	public abstract LOV getTaskType();

	public abstract User getAssignee();

	public abstract void setAssignee(User user);

	public abstract void setStartDateFact(LocalDateTime startDateFact);

	public abstract void setResolutionDate(LocalDateTime resolutionDate);

}
