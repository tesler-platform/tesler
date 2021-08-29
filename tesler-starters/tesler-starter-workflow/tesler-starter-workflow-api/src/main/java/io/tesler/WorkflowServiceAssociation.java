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

package io.tesler;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.source.services.crudma.DmnHelperFieldsCrudmaService;
import io.tesler.source.services.crudma.DmnTaskFieldsCrudmaService;
import io.tesler.source.services.crudma.WorkflowDictionaryCrudmaService;
import io.tesler.source.services.data.TaskChildBcService;
import io.tesler.source.services.data.WorkflowAssigneeRecommendationService;
import io.tesler.source.services.data.WorkflowConditionService;
import io.tesler.source.services.data.WorkflowPostFunctionService;
import io.tesler.source.services.data.WorkflowService;
import io.tesler.source.services.data.WorkflowStepConditionGroupService;
import io.tesler.source.services.data.WorkflowStepFieldService;
import io.tesler.source.services.data.WorkflowStepService;
import io.tesler.source.services.data.WorkflowTaskChildBcAvailabilityService;
import io.tesler.source.services.data.WorkflowTaskFieldsService;
import io.tesler.source.services.data.WorkflowTaskMigrationService;
import io.tesler.source.services.data.WorkflowTransitionConditionGroupService;
import io.tesler.source.services.data.WorkflowTransitionGroupService;
import io.tesler.source.services.data.WorkflowTransitionService;
import io.tesler.source.services.data.WorkflowTransitionValidationService;
import io.tesler.source.services.data.WorkflowVersionService;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Component;

@Getter
public enum WorkflowServiceAssociation implements EnumBcIdentifier {

	// @formatter:off
		wf(WorkflowService.class),
			wfActiveVersion(wf, WorkflowVersionService.class),
			wfVersion(wf, WorkflowVersionService.class),
				wfStepAutoClosed(wfVersion, WorkflowStepService.class),
				wfStepPickList(wfVersion, WorkflowStepService.class),
				wfStep(wfVersion, WorkflowStepService.class),
					wfStepType(wfStep, WorkflowDictionaryCrudmaService.class),
					wfTransitionPickList(wfStep, WorkflowTransitionService.class),
					wfTransition(wfStep, WorkflowTransitionService.class),
						wfTransitionDestStep(wfTransition, WorkflowStepService.class),
						wfTransitionCondGroup(wfTransition, WorkflowTransitionConditionGroupService.class),
							wfTransitionCond(wfTransitionCondGroup, WorkflowConditionService.class),
								wfTransitionCondDmnFields(wfTransitionCond, DmnTaskFieldsCrudmaService.class),
								wfTransitionCondDmnConstants(wfTransitionCond, DmnHelperFieldsCrudmaService.class),
						wfPostFuncGroup(wfTransition, WorkflowTransitionConditionGroupService.class),
							wfPostFuncGroupCond(wfPostFuncGroup, WorkflowConditionService.class),
								wfPostFuncGroupCondDmnFields(wfPostFuncGroupCond, DmnTaskFieldsCrudmaService.class),
								wfPostFuncGroupCondDmnConstants(wfPostFuncGroupCond, DmnHelperFieldsCrudmaService.class),
							wfTransitionFunc(wfPostFuncGroup, WorkflowPostFunctionService.class),
								pfChildWorkflow(wfTransitionFunc, WorkflowService.class),
						wfTranCondGroupValid(wfTransition, WorkflowTransitionConditionGroupService.class),
							wfTranCondValid(wfTranCondGroupValid, WorkflowConditionService.class),
								wfTranCondValidDmnFields(wfTranCondValid, DmnTaskFieldsCrudmaService.class),
								wfTranCondValidDmnConstants(wfTranCondValid, DmnHelperFieldsCrudmaService.class),
							wfTransitionValid(wfTranCondGroupValid, WorkflowTransitionValidationService.class),
								wfTransitionValidDmnFields(wfTransitionValid, DmnTaskFieldsCrudmaService.class),
								wfTransitionValidDmnConstants(wfTransitionValid, DmnHelperFieldsCrudmaService.class),
								wfTransitionValidPreInvoke(wfTransitionValid, WorkflowTransitionValidationService.class),
						wfTransitionGroupPopup(wfTransition, WorkflowTransitionGroupService.class),
					wfTransitionGroup(wfStep, WorkflowTransitionGroupService.class),
					wfChildBcAvailability(wfStep, WorkflowTaskChildBcAvailabilityService.class),
						wfChildBcAvailabilityPopup(wfChildBcAvailability, TaskChildBcService.class),
						wfChildBcAvailabilityCond(wfChildBcAvailability, WorkflowConditionService.class),
							wfChildBcAvailabilityCondDmnFields(wfChildBcAvailabilityCond, DmnTaskFieldsCrudmaService.class),
							wfChildBcAvailabilityCondDmnConstants(wfChildBcAvailabilityCond, DmnHelperFieldsCrudmaService.class),
					wfStepField(wfStep, WorkflowStepFieldService.class),
						wfStepTaskFields(wfStepField, WorkflowTaskFieldsService.class),
						wfStepFieldCond(wfStepField, WorkflowConditionService.class),
							wfStepFieldCondDmnFields(wfStepFieldCond, DmnTaskFieldsCrudmaService.class),
							wfStepFieldCondDmnConstants(wfStepFieldCond, DmnHelperFieldsCrudmaService.class),
					wfStepCondGroupRecommendedAssignee(wfStep, WorkflowStepConditionGroupService.class),
						wfStepCondRecommendedAssignee(wfStepCondGroupRecommendedAssignee, WorkflowConditionService.class),
							wfStepCondRecommendedAssigneeDmnFields(wfStepCondRecommendedAssignee, DmnTaskFieldsCrudmaService.class),
							wfStepCondRecommendedAssigneeDmnConstants(wfStepCondRecommendedAssignee, DmnHelperFieldsCrudmaService.class),
				wfStepRecommendedAssignee(wfStepCondGroupRecommendedAssignee, WorkflowAssigneeRecommendationService.class),

		migrationWf(WorkflowService.class),
			migrationWfVersion(migrationWf, WorkflowVersionService.class),
				wfTemplateMigration(migrationWfVersion, WorkflowTaskMigrationService.class),
					wfTemplateMigrationCurrentStep(wfTemplateMigration, WorkflowStepService.class),
					wfTemplateMigrationNewStep(wfTemplateMigration, WorkflowStepService.class),
					wfTemplateMigrationCurrentAutomaticTransition(wfTemplateMigration, WorkflowTransitionService.class),
					wfTemplateMigrationNewAutomaticTransition(wfTemplateMigration, WorkflowTransitionService.class),
				wfTaskMigration(migrationWfVersion, WorkflowTaskMigrationService.class),
					wfTaskMigrationCurrentStep(wfTaskMigration, WorkflowStepService.class),
					wfTaskMigrationNewStep(wfTaskMigration, WorkflowStepService.class),
	;
	// @formatter:on

	public static final Holder<WorkflowServiceAssociation> Holder = new Holder<>(WorkflowServiceAssociation.class);

	private final BcDescription bcDescription;

	WorkflowServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	WorkflowServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	WorkflowServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	WorkflowServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	WorkflowServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	WorkflowServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Override
	public String getName() {
		return bcDescription.getName();
	}

	@Override
	public String getParentName() {
		return bcDescription.getParentName();
	}

	public boolean isBc(BcIdentifier other) {
		if (other == null) {
			return false;
		}
		return new EqualsBuilder()
				.append(getName(), other.getName())
				.append(getParentName(), other.getParentName())
				.isEquals();
	}

	public boolean isNotBc(BcIdentifier other) {
		return !isBc(other);
	}

	@Component
	public static class WorkflowBcSupplier extends AbstractEnumBcSupplier<WorkflowServiceAssociation> {

		public WorkflowBcSupplier() {
			super(WorkflowServiceAssociation.Holder);
		}

	}

}
