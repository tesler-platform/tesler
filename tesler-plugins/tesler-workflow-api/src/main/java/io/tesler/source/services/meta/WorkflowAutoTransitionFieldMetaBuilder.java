/*-
 * #%L
 * IO Tesler - Workflow API
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

package io.tesler.source.services.meta;

import static io.tesler.source.dto.WorkflowPostFunctionTriggerDto_.requestPostFunctionType;
import static org.apache.commons.lang3.StringUtils.joinWith;

import io.tesler.WorkflowServiceAssociation;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.Project;
import io.tesler.model.workflow.entity.Workflow;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.source.dto.WorkflowPostFunctionTriggerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowAutoTransitionFieldMetaBuilder extends FieldMetaBuilder<WorkflowPostFunctionTriggerDto> {

	private final JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(final RowDependentFieldsMeta<WorkflowPostFunctionTriggerDto> fields,
			InnerBcDescription bcDescription, final Long rowId, final Long parRowId) {
		final WorkflowPostFunctionTrigger trigger = jpaDao.findById(WorkflowPostFunctionTrigger.class, rowId);
		final WorkflowPostFunction postFunction = trigger.getRequestPostFunction();
		final WorkflowTransitionConditionGroup conditionGroup = postFunction.getConditionGroup();
		final WorkflowTransition transition = conditionGroup.getTransition();
		final WorkflowStep step = transition.getSourceStep();
		final WorkflowVersion workflowVersion = step.getWorkflowVersion();
		final Workflow workflow = workflowVersion.getWorkflow();
		final Project project = workflow.getProject();
		fields.setDrilldown(
				requestPostFunctionType,
				DrillDownType.INNER,
				joinWith(
						"/",
						"screen/admin/view/wftransitionfunc",
						WorkflowServiceAssociation.wfProject,
						project.getId(),
						WorkflowServiceAssociation.wf,
						workflow.getId(),
						WorkflowServiceAssociation.wfVersion,
						workflowVersion.getId(),
						WorkflowServiceAssociation.wfStep,
						step.getId(),
						WorkflowServiceAssociation.wfTransition,
						transition.getId(),
						WorkflowServiceAssociation.wfPostFuncGroup,
						conditionGroup.getId(),
						WorkflowServiceAssociation.wfTransitionFunc,
						postFunction.getId(),
						WorkflowServiceAssociation.wfPostFuncTrigger,
						trigger.getId()
				)
		);
	}

	@Override
	public void buildIndependentMeta(final FieldsMeta<WorkflowPostFunctionTriggerDto> fields,
			InnerBcDescription bcDescription, final Long parRowId) {
	}

}
