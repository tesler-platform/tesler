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

package io.tesler.engine.workflow.dao;

import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.NOT_RECEIVED;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_AND_ERROR;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_PARTIALLY;
import static io.tesler.api.data.dictionary.CoreDictionaries.RemoteSystemResponse.RECEIVED_PARTIALLY_AND_ERROR;
import static io.tesler.api.data.dictionary.DictionaryType.TASK_TYPE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.CoreDictionaries.TaskStatus;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.engine.workflow.cache.WorkflowCache;
import io.tesler.engine.workflow.services.WorkflowDao;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.dao.util.JpaUtils;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.core.entity.Project;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.PendingTransition;
import io.tesler.model.workflow.entity.WaitingRemoteSystem;
import io.tesler.model.workflow.entity.WaitingRemoteSystem_;
import io.tesler.model.workflow.entity.Workflow;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation;
import io.tesler.model.workflow.entity.WorkflowAssigneeRecommendation_;
import io.tesler.model.workflow.entity.WorkflowCondition;
import io.tesler.model.workflow.entity.WorkflowCondition_;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger;
import io.tesler.model.workflow.entity.WorkflowPostFunctionTrigger_;
import io.tesler.model.workflow.entity.WorkflowPostFunction_;
import io.tesler.model.workflow.entity.WorkflowStep;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup;
import io.tesler.model.workflow.entity.WorkflowStepConditionGroup_;
import io.tesler.model.workflow.entity.WorkflowStepField;
import io.tesler.model.workflow.entity.WorkflowStep_;
import io.tesler.model.workflow.entity.WorkflowTask;
import io.tesler.model.workflow.entity.WorkflowTaskChildBcAvailability;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionConditionGroup;
import io.tesler.model.workflow.entity.WorkflowTransitionHistory;
import io.tesler.model.workflow.entity.WorkflowTransitionHistory_;
import io.tesler.model.workflow.entity.WorkflowTransitionValidation;
import io.tesler.model.workflow.entity.WorkflowTransitionValidation_;
import io.tesler.model.workflow.entity.WorkflowTransition_;
import io.tesler.model.workflow.entity.WorkflowVersion;
import io.tesler.model.workflow.entity.WorkflowVersion_;
import io.tesler.model.workflow.entity.Workflow_;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Extension
public class WorkflowDaoImpl implements WorkflowDao {

	@PersistenceContext(unitName = "teslerEntityManagerFactory")
	private EntityManager entityManager;

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private WorkflowCache workflowCache;

	@Override
	public WorkflowStep getCurrentStep(final WorkflowableTask task) {
		final WorkflowTask workflowTask = task.getWorkflowTask();
		if (workflowTask != null) {
			return workflowTask.getWorkflowStep();
		}
		return getInitialStep(task.getProject(), task.getTaskType());
	}

	@Override
	public WorkflowStep getHiddenStep(final WorkflowVersion version) {
		return jpaDao.getFirstResultOrNull(WorkflowStep.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowStep_.linkedStatusCd), TaskStatus.HIDDEN),
				cb.equal(root.get(WorkflowStep_.workflowVersion), version)
		));
	}

	@Override
	public WorkflowStep getInitialStep(final Project project, final LOV taskType) {
		final Workflow workflow = jpaDao.getSingleResultOrNull(Workflow.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(Workflow_.project), project),
				cb.equal(root.get(Workflow_.taskTypeCd), taskType)
		));
		return workflow == null || workflow.getActiveVersion() == null ? null : workflow.getActiveVersion().getFirstStep();
	}

	@Override
	public boolean isInitialStep(final WorkflowStep workflowStep) {
		return workflowStep == null || Objects.equals(workflowCache.getFirstWorkflowStep(workflowStep), workflowStep);
	}

	@Override
	public WorkflowStep getStepByUuid(final WorkflowVersion version, final String uuid) {
		return jpaDao.getSingleResultOrNull(WorkflowStep.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowStep_.workflowVersion), version),
				cb.equal(root.get(WorkflowStep_.uuid), uuid)
		));
	}

	@Override
	public List<WorkflowTransition> getTransitionsByUuid(final Workflow workflow, final String uuid) {
		return jpaDao.getList(WorkflowTransition.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion)
						.get(WorkflowVersion_.workflow), workflow),
				cb.equal(root.get(WorkflowTransition_.uuid), uuid)
		));
	}

	@Override
	public WorkflowTransition getTransitionByUuid(final WorkflowVersion version, final String uuid) {
		return jpaDao.getSingleResultOrNull(WorkflowTransition.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion), version),
				cb.equal(root.get(WorkflowTransition_.uuid), uuid)
		));
	}

	@Override
	public WorkflowTransition getLastWorkflowTransitionByUuid(final String uuid) {
		return jpaDao.getSingleResultOrNull(WorkflowTransition.class, (root, query, cb) -> {
			final Subquery<Double> subquery = query.subquery(Double.class);
			final Root<WorkflowTransition> subqueryRoot = subquery.from(WorkflowTransition.class);
			subquery.select(cb.max(subqueryRoot.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion)
					.get(WorkflowVersion_.version)));
			subquery.where(
					cb.and(
							cb.equal(subqueryRoot.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion)
									.get(WorkflowVersion_.draft), Boolean.FALSE),
							cb.equal(subqueryRoot.get(WorkflowTransition_.uuid), uuid)
					)
			);
			return cb.and(
					cb.equal(root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion)
							.get(WorkflowVersion_.version), subquery),
					cb.equal(root.get(WorkflowTransition_.uuid), uuid)
			);
		});
	}

	@Override
	public WorkflowTransition getActiveWorkflowTransitionByUuid(String uuid) {
		return jpaDao.getList(WorkflowTransition.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowTransition_.uuid), uuid),
				cb.equal(
						root.get(WorkflowTransition_.sourceStep)
								.get(WorkflowStep_.workflowVersion)
								.get(WorkflowVersion_.id),
						root.get(WorkflowTransition_.sourceStep)
								.get(WorkflowStep_.workflowVersion)
								.get(WorkflowVersion_.workflow)
								.get(Workflow_.activeVersion)
								.get(WorkflowVersion_.id)
				)
		)).stream().findFirst().orElse(null);
	}

	@Override
	public WorkflowTransition getTransition(final LOV taskType, final LOV sourceStepTaskStatus,
			final LOV destinationStepTaskStatus) {
		return jpaDao.getFirstResultOrNull(WorkflowTransition.class, (root, query, cb) -> cb.and(
				cb.equal(
						root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.linkedStatusCd),
						sourceStepTaskStatus
				),
				cb.equal(
						root.get(WorkflowTransition_.destinationStep).get(WorkflowStep_.linkedStatusCd),
						destinationStepTaskStatus
				),
				cb.equal(root.get(WorkflowTransition_.sourceStep).get(WorkflowStep_.workflowVersion)
						.get(WorkflowVersion_.workflow).get(Workflow_.taskTypeCd), taskType),
				cb.equal(root.get(WorkflowTransition_.destinationStep).get(WorkflowStep_.workflowVersion)
						.get(WorkflowVersion_.workflow).get(Workflow_.taskTypeCd), taskType)
		));
	}

	@Override
	public WorkflowTransitionHistory getLastTransitionHistoryByDestinationStep(final WorkflowTask task,
			final WorkflowStep destinationStep) {
		return jpaDao.getFirstResultOrNull(WorkflowTransitionHistory.class, (root, query, cb) -> {
			query.orderBy(cb.desc(root.get(BaseEntity_.createdDate)));
			return cb.and(
					cb.equal(root.get(WorkflowTransitionHistory_.workflowTask), task),
					cb.equal(
							root.get(WorkflowTransitionHistory_.transition).get(WorkflowTransition_.destinationStep),
							destinationStep
					)
			);
		});
	}

	@Override
	public WorkflowTransitionHistory getLastTransitionHistory(final WorkflowTask task) {
		return jpaDao.getFirstResultOrNull(WorkflowTransitionHistory.class, (root, query, cb) -> {
			query.orderBy(cb.desc(root.get(BaseEntity_.createdDate)));
			return cb.equal(root.get(WorkflowTransitionHistory_.workflowTask), task);
		});
	}

	@Override
	public void createDefaultPostFunctions(final WorkflowTransitionConditionGroup transitionConditionGroup) {
		final WorkflowPostFunction postFunction = new WorkflowPostFunction();
		postFunction.setConditionGroup(transitionConditionGroup);
		postFunction.setSeq(1L);
		postFunction.setActionCd(CoreDictionaries.WfPostFunction.SET_STEP_TERM);
		postFunction.setStepTerm(0L);
		jpaDao.save(postFunction);
	}

	@Override
	public void deletePostFunction(final WorkflowPostFunction postFunction) {
		deleteRelations(postFunction);
		jpaDao.delete(postFunction);
	}

	@Override
	public void deleteTransitionConditionGroup(final WorkflowTransitionConditionGroup transitionConditionGroup) {
		deleteRelations(transitionConditionGroup);
		jpaDao.delete(transitionConditionGroup);
	}

	@Override
	public WorkflowVersion getWorkflowVersion(final WorkflowableTask task) {
		final WorkflowStep currentStep = getCurrentStep(task);
		return currentStep == null ? null : currentStep.getWorkflowVersion();
	}

	@Override
	public List<LOV> getTaskTypesNotInWf(final Project project) {
		return JpaUtils.<String>selectNativeQuery(
				entityManager,
				"select d.key from dictionary_item d where d.type = ? and d.key not in (select w.task_type_cd from wf w where w.project_id = ?)",
				TASK_TYPE.getName(),
				project
		).stream().map(TASK_TYPE::lookupName).collect(Collectors.toList());
	}

	@Override
	public BigDecimal getMaxVersion(final Workflow workflow) {
		final WorkflowVersion version = jpaDao.getFirstResultOrNull(WorkflowVersion.class, (root, query, cb) -> {
			query.orderBy(cb.desc(root.get(WorkflowVersion_.version)));
			return cb.equal(root.get(WorkflowVersion_.workflow), workflow);
		});
		return version == null || version.getVersion() == null ? BigDecimal.ZERO : BigDecimal.valueOf(version.getVersion());
	}

	@Override
	public BigDecimal getNextVersion(final Workflow workflow, final boolean majorVersion) {
		final BigDecimal bigDecimal = getMaxVersion(workflow);
		return majorVersion
				? bigDecimal.setScale(0, RoundingMode.DOWN).add(BigDecimal.ONE)
				: bigDecimal.add(BigDecimal.valueOf(0.001));
	}

	public PendingTransition createPendingTransition(final WorkflowTransition transition, final User sessionUser,
			final LOV sessionUserRole) {
		final PendingTransition pendingTransition = new PendingTransition();
		pendingTransition.setTransition(transition);
		pendingTransition.setUser(sessionUser);
		pendingTransition.setUserRole(sessionUserRole);
		jpaDao.save(pendingTransition);
		return pendingTransition;
	}

	public WorkflowTask createWorkflowTask(final WorkflowStep step) {
		if (step == null) {
			return null;
		}
		final WorkflowTask workflowTask = new WorkflowTask();
		workflowTask.setWorkflowStep(step);
		jpaDao.save(workflowTask);
		return workflowTask;
	}

	public List<WorkflowStepConditionGroup> getStepConditionGroups(final WorkflowStep step) {
		final List<WorkflowStepConditionGroup> result = jpaDao.getList(
				WorkflowStepConditionGroup.class,
				(root, query, cb) -> cb.equal(root.get(WorkflowStepConditionGroup_.step), step)
		);
		result.sort(comparing(WorkflowStepConditionGroup::getSeq, nullsFirst(naturalOrder())));
		return result;
	}

	public List<WorkflowAssigneeRecommendation> getAssigneeRecommendations(final WorkflowStepConditionGroup condGroup) {
		return jpaDao.getList(WorkflowAssigneeRecommendation.class, (root, query, cb) -> cb.equal(
				root.get(WorkflowAssigneeRecommendation_.conditionGroup), condGroup
		));
	}

	public List<WorkflowTransitionConditionGroup> getTransitionConditionGroups(final WorkflowTransition transition,
			final LOV condGroupCd) {
		return workflowCache.getTransitionConditionGroups(transition, condGroupCd);
	}

	public <V extends WorkflowTransitionValidation> List<V> getValidations(
			final Class<V> transitionValidationClass,
			final WorkflowTransitionConditionGroup conditionGroup) {
		final List<V> result = jpaDao.getList(transitionValidationClass, (root, query, cb) -> cb.equal(
				root.get(WorkflowTransitionValidation_.conditionGroup), conditionGroup
		));
		result.sort(comparing(WorkflowTransitionValidation::getSeq, nullsFirst(naturalOrder())));
		return result;
	}

	public <P extends WorkflowPostFunction> List<P> getPostFunctions(
			final Class<P> postFunctionClass,
			final WorkflowTransitionConditionGroup conditionGroup) {
		final List<P> result = jpaDao.getList(postFunctionClass, (root, query, cb) -> cb.equal(
				root.get(WorkflowPostFunction_.conditionGroup), conditionGroup
		));
		result.sort(comparing(WorkflowPostFunction::getSeq, nullsLast(naturalOrder())));
		return result;
	}

	public WorkflowTransition getTransition(final Long transitionId) {
		return jpaDao.getSingleResultOrNull(
				WorkflowTransition.class,
				(root, query, cb) -> cb.equal(root.get(BaseEntity_.id), transitionId)
		);
	}

	public List<WorkflowStepField> getStepFields(final WorkflowStep sourceStep) {
		if (sourceStep == null) {
			return Collections.emptyList();
		}
		return workflowCache.getStepFields(sourceStep);
	}

	public List<WorkflowTransition> getTransitions(final WorkflowStep sourceStep) {
		if (sourceStep == null) {
			return Collections.emptyList();
		}
		return workflowCache.getTransitions(sourceStep);
	}

	public List<WorkflowTaskChildBcAvailability> getWorkflowTaskChildBcAvailabilities(final WorkflowStep workflowStep) {
		if (workflowStep == null) {
			return Collections.emptyList();
		}
		return workflowCache.getWorkflowTaskChildBcAvailabilities(workflowStep);
	}

	public WorkflowTransition getTransitionBetweenSteps(final WorkflowStep sourceStep,
			final WorkflowStep destinationStep) {
		return jpaDao.getSingleResultOrNull(WorkflowTransition.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowTransition_.sourceStep), sourceStep),
				cb.equal(root.get(WorkflowTransition_.destinationStep), destinationStep)
		));
	}

	public WorkflowTransitionHistory saveTransitionHistory(final WorkflowableTask task,
			final WorkflowTransition transition, final User currentUser, final User previousAssignee) {
		final WorkflowTransitionHistory history = new WorkflowTransitionHistory();
		history.setWorkflowTask(task.getWorkflowTask());
		history.setTransition(transition);
		history.setTransitionUser(currentUser);
		history.setPreviousAssignee(previousAssignee);
		jpaDao.save(history);
		return history;
	}

	public WaitingRemoteSystem getWaitingRemoteSystem(final String correlationId) {
		return jpaDao.getSingleResultOrNull(WaitingRemoteSystem.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WaitingRemoteSystem_.correlationId), correlationId),
				root.get(WaitingRemoteSystem_.statusCd).in(
						NOT_RECEIVED,
						RECEIVED_PARTIALLY,
						RECEIVED_AND_ERROR,
						RECEIVED_PARTIALLY_AND_ERROR
				)
		));
	}

	public WorkflowPostFunctionTrigger getPostFunctionTrigger(final WorkflowPostFunction requestPostFunction,
			final WorkflowStep responseWaitStep) {
		return jpaDao.getSingleResultOrNull(WorkflowPostFunctionTrigger.class, (root, query, cb) -> cb.and(
				cb.equal(root.get(WorkflowPostFunctionTrigger_.requestPostFunction), requestPostFunction),
				cb.equal(root.get(WorkflowPostFunctionTrigger_.responseWaitStep), responseWaitStep)
		));
	}

	public <C extends WorkflowCondition> List<C> getConditions(
			final Class<C> conditionClass,
			final WorkflowTransitionConditionGroup conditionGroup) {
		return workflowCache.getTransitionConditions(conditionClass, conditionGroup);
	}

	public <C extends WorkflowCondition> List<C> getConditions(
			final Class<C> conditionClass,
			final WorkflowStepConditionGroup conditionGroup) {
		return workflowCache.getStepConditions(conditionClass, conditionGroup);
	}

	public <C extends WorkflowCondition> List<C> getConditions(
			final Class<C> conditionClass,
			final WorkflowStepField stepField) {
		return workflowCache.getFieldConditions(conditionClass, stepField);
	}

	public <C extends WorkflowCondition> List<C> getConditions(
			final Class<C> conditionClass,
			final WorkflowTaskChildBcAvailability childBcAvailability) {
		return workflowCache.getAvailabilityConditions(conditionClass, childBcAvailability);
	}

	private void deleteRelations(final WorkflowPostFunction postFunction) {
		int count = jpaDao.delete(WorkflowPostFunctionTrigger.class, (root, cq, cb) -> cb.equal(
				root.get(WorkflowPostFunctionTrigger_.requestPostFunction), postFunction
		));
		if (count > 0) {
			jpaDao.flush();
		}
	}

	private void deleteRelations(final WorkflowTransitionConditionGroup transitionConditionGroup) {
		int count = jpaDao.delete(WorkflowCondition.class, (root, cq, cb) -> cb.equal(
				root.get(WorkflowCondition_.transitionConditionGroup), transitionConditionGroup
		));
		count += jpaDao.delete(WorkflowPostFunction.class, (root, cq, cb) -> cb.equal(
				root.get(WorkflowPostFunction_.conditionGroup), transitionConditionGroup
		));
		if (count > 0) {
			jpaDao.flush();
		}
	}

}
