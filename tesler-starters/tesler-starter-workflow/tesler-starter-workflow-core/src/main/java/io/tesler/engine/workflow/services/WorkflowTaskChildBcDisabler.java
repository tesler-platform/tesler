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

import io.tesler.core.bc.InnerBcTypeAware;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.ActionType;
import io.tesler.core.service.rowmeta.BcDisabler;
import io.tesler.engine.workflow.dao.WorkflowableTaskDao;
import io.tesler.model.workflow.entity.WorkflowableTask;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.pf4j.Extension;
import org.springframework.stereotype.Service;

@Extension
@Service
public class WorkflowTaskChildBcDisabler extends BcDisabler {

	private final WorkflowEngine workflowEngine;

	private final BcRegistry bcRegistry;

	private final WorkflowableTaskDao<?> workflowableTaskDao;

	private final Map<String, String> supportedBc;

	public WorkflowTaskChildBcDisabler(
			final WorkflowEngine workflowEngine, final BcRegistry bcRegistry,
			final WorkflowableTaskDao<?> workflowableTaskDao,
			final InnerBcTypeAware innerBcTypeAware) {
		this.workflowEngine = workflowEngine;
		this.bcRegistry = bcRegistry;
		this.workflowableTaskDao = workflowableTaskDao;
		final Set<String> taskBcNames = bcRegistry.select(InnerBcDescription.class)
				.filter(bcDescription -> WorkflowableTask.class.isAssignableFrom(
						innerBcTypeAware.getTypeOfEntity(bcDescription)
				))
				.map(BcDescription::getName)
				.collect(Collectors.toSet());

		supportedBc = new HashMap<>();
		bcRegistry.select(BcDescription.class).forEach(bcDescription -> {
			final String parentTaskBc = getParentTaskBc(taskBcNames, bcDescription);
			if (parentTaskBc != null) {
				supportedBc.put(bcDescription.getName(), parentTaskBc);
			}
		});
	}

	@Override
	public Collection<BcIdentifier> getSupportedBc() {
		return supportedBc.keySet().stream()
				.map(bcRegistry::getBcDescription)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isBcDisabled(final BusinessComponent bc) {
		final BcDescription bcDescription = bc.getDescription();
		if (!(bcDescription instanceof InnerBcDescription)) {
			return false;
		}
		final String taskIdString = bc.getHierarchy().getId(supportedBc.get(bc.getName()));
		final Long taskId = NumberUtils.createLong(Objects.equals(taskIdString, "null") ? null : taskIdString);
		if (taskId == null) {
			return false;
		}
		return workflowEngine.isChildBcDisabled(bcDescription, workflowableTaskDao.getTask(taskId));
	}

	@Override
	protected boolean isActionDisabled(final String actionName) {
		return Sets.newHashSet(
				ActionType.CREATE.getType(),
				ActionType.SAVE.getType(),
				ActionType.DELETE.getType(),
				ActionType.ASSOCIATE.getType()
		).contains(actionName);
	}

	private String getParentTaskBc(final Set<String> taskBcNames, final BcDescription bcDescription) {
		String parentName = bcDescription.getParentName();
		while (parentName != null) {
			if (taskBcNames.contains(parentName)) {
				break;
			}
			parentName = bcRegistry.getBcDescription(parentName).getParentName();
		}
		return parentName;
	}

}
