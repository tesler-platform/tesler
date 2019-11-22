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

package io.tesler.engine.workflow;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowTransitionValidation;
import io.tesler.model.workflow.entity.WorkflowableTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkflowSettings<E extends WorkflowableTask> {

	private final Class<E> entityClass;

	private final Class<? extends DataResponseDTO> dtoClass;

	private final Class<? extends WorkflowPostFunction> postFunctionExtensionClass;

	private final Class<? extends WorkflowTransitionValidation> transitionValidationExtensionClass;

	private final Class<? extends WorkflowCondition> conditionExtensionClass;

	public static <E extends WorkflowableTask> Builder<E> builder(
			final Class<E> entityClass,
			final Class<? extends DataResponseDTO> dtoClass) {
		return new Builder<>(entityClass, dtoClass);
	}

	public static class Builder<E extends WorkflowableTask> {

		private final Class<E> entityClass;

		private final Class<? extends DataResponseDTO> dtoClass;

		private Class<? extends WorkflowPostFunction> postFunctionExtensionClass = WorkflowPostFunction.class;

		private Class<? extends WorkflowTransitionValidation> transitionValidationExtensionClass = WorkflowTransitionValidation.class;

		private Class<? extends WorkflowCondition> conditionExtensionClass = WorkflowCondition.class;

		public Builder(
				final Class<E> entityClass,
				final Class<? extends DataResponseDTO> dtoClass) {
			this.entityClass = entityClass;
			this.dtoClass = dtoClass;
		}

		public Builder<E> postFunctionExtensionClass(
				Class<? extends WorkflowPostFunction> postFunctionExtensionClass) {
			this.postFunctionExtensionClass = postFunctionExtensionClass;
			return this;
		}

		public Builder<E> transitionValidationExtensionClass(
				Class<? extends WorkflowTransitionValidation> transitionValidationExtensionClass) {
			this.transitionValidationExtensionClass = transitionValidationExtensionClass;
			return this;
		}

		public Builder<E> conditionExtensionClass(
				Class<? extends WorkflowCondition> conditionExtensionClass) {
			this.conditionExtensionClass = conditionExtensionClass;
			return this;
		}

		public WorkflowSettings<E> build() {
			return new WorkflowSettings<>(
					entityClass,
					dtoClass,
					postFunctionExtensionClass,
					transitionValidationExtensionClass,
					conditionExtensionClass
			);
		}

	}

}
