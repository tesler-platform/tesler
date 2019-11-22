/*-
 * #%L
 * IO Tesler - Workflow Impl
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

package io.tesler.engine.workflow.recommendation;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public final class UnsupportedRecommendation implements AssigneeRecommendation {

	@Override
	public LOV getType() {
		return null;
	}

	@Override
	public Specification<User> getSpecification(final WorkflowAssigneeRecommendation assigneeRecommendation) {
		throw new UnsupportedOperationException(String.format(
				"Правило отбора рекомендованных исполнителей '%s' не реализовано",
				assigneeRecommendation.getCondAssigneeCd().getKey()
		));
	}

}
