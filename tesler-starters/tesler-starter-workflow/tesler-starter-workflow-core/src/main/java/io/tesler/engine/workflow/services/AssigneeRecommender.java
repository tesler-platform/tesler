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

package io.tesler.engine.workflow.services;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.engine.workflow.dao.WorkflowDaoImpl;
import io.tesler.engine.workflow.recommendation.AssigneeRecommendation;
import io.tesler.engine.workflow.recommendation.UnsupportedRecommendation;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
final class AssigneeRecommender {

	private final WorkflowSettings<?> workflowSettings;

	private final WorkflowDaoImpl workflowDao;

	private final ConditionCheck conditionCheck;

	private final AssigneeRecommendation defaultRecommendation;

	private final Map<LOV, AssigneeRecommendation> recommendations;

	AssigneeRecommender(
			final WorkflowSettings<?> workflowSettings,
			final WorkflowDaoImpl workflowDao,
			final ConditionCheck conditionCheck,
			final UnsupportedRecommendation defaultRecommendation,
			final List<AssigneeRecommendation> assigneeRecommendations) {
		this.workflowSettings = workflowSettings;
		this.workflowDao = workflowDao;
		this.conditionCheck = conditionCheck;
		this.defaultRecommendation = defaultRecommendation;

		final Builder<LOV, AssigneeRecommendation> builder = ImmutableMap.builder();
		for (final AssigneeRecommendation assigneeRecommendation : assigneeRecommendations) {
			if (assigneeRecommendation.getType() != null) {
				builder.put(assigneeRecommendation.getType(), assigneeRecommendation);
			}
		}
		this.recommendations = builder.build();
	}

	Specification<User> recommend(final WorkflowableTask task) {
		return new AssigneeRecommendationSpecification(
				workflowDao.getStepConditionGroups(workflowDao.getCurrentStep(task)).stream()
						.filter(conditionGroup -> conditionCheck.isAvailable(
								task, workflowDao.getConditions(workflowSettings.getConditionExtensionClass(), conditionGroup), null)
						)
						.map(workflowDao::getAssigneeRecommendations)
						.map(this::recommendationsToSpecifications)
						.filter(CollectionUtils::isNotEmpty)
						.collect(Collectors.toList())
		);
	}

	private List<Specification<User>> recommendationsToSpecifications(
			final List<WorkflowAssigneeRecommendation> assigneeRecommendations) {
		return assigneeRecommendations.stream()
				.map(
						assigneeRecommendation -> recommendations.getOrDefault(
								assigneeRecommendation.getCondAssigneeCd(),
								defaultRecommendation
						).getSpecification(assigneeRecommendation)
				)
				.collect(Collectors.toList());
	}

	@RequiredArgsConstructor
	private static class AssigneeRecommendationSpecification implements Specification<User> {

		private final List<List<Specification<User>>> specificationGroups;

		@Override
		public Predicate toPredicate(final Root<User> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
			return cb.or(
					specificationGroups.stream()
							.map(specifications -> cb.and(
									specifications.stream()
											.map(userSpecification -> userSpecification.toPredicate(root, query, cb))
											.toArray(Predicate[]::new)
									)
							)
							.toArray(Predicate[]::new)
			);
		}

	}

}
