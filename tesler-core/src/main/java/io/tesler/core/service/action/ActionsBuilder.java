/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.service.action;

import static io.tesler.core.service.action.ActionAvailableChecker.ALWAYS_FALSE;
import static io.tesler.core.service.action.ActionAvailableChecker.ALWAYS_TRUE;
import static java.util.Objects.nonNull;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.dto.rowmeta.ActionType;
import java.util.ArrayList;
import java.util.List;


public class ActionsBuilder<T extends DataResponseDTO, D extends BcDescription> {

	private final List<ActionDescription<T, D>> actionDefinitions = new ArrayList<>();

	private final List<ActionGroupDescription<T, D>> actionGroupDefinitions = new ArrayList<>();

	private ActionDescriptionBuilder<T, D> actionDescriptionBuilder;

	ActionsBuilder() {

	}

	public ActionsBuilder<T, D> addAction(ActionDescription<T, D> actionDescription) {
		if (nonNull(actionDescription)) {
			actionDefinitions.add(actionDescription);
		}
		return this;
	}

	public ActionDescriptionBuilder<T, D> newAction() {
		actionDescriptionBuilder = ActionDescription.<T, D>builder().withBuilder(this);
		actionDescriptionBuilder.available(ALWAYS_TRUE);
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> action(String type, String actionName) {
		actionDescriptionBuilder = newAction();
		actionDescriptionBuilder.action(type, actionName);
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> action(ActionType actionType) {
		actionDescriptionBuilder = newAction().action(actionType);
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> create() {
		actionDescriptionBuilder = action(ActionType.CREATE)
				.scope(ActionScope.BC)
				.withoutAutoSaveBefore();
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> save() {
		actionDescriptionBuilder = action(ActionType.SAVE);
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> cancelCreate() {
		// по-умолчанию недоступно, а решается в io.tesler.core.crudma.CrudmaGateway
		actionDescriptionBuilder = action(ActionType.CANCEL_CREATE).available(ALWAYS_FALSE)
				.withoutAutoSaveBefore();
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> associate() {
		actionDescriptionBuilder = action(ActionType.ASSOCIATE)
				.scope(ActionScope.BC);
		return actionDescriptionBuilder;
	}

	public ActionDescriptionBuilder<T, D> delete() {
		actionDescriptionBuilder = action(ActionType.DELETE)
				.withoutAutoSaveBefore();
		return actionDescriptionBuilder;
	}

	public ActionsBuilder<T, D> addGroup(String type, String groupName, int maxGroupVisualButtonsCount,
			Actions<T, D> groupActions) {
		actionGroupDefinitions.add(
				new ActionGroupDescription<>(type, groupName, maxGroupVisualButtonsCount, groupActions.actionDefinitions)
		);
		return this;
	}

	public ActionsBuilder<T, D> withIcon(ActionIconSpecifier icon, boolean showOnlyIcon) {
		this.actionGroupDefinitions.get(actionGroupDefinitions.size() - 1).setIconCode(icon);
		this.actionGroupDefinitions.get(actionGroupDefinitions.size() - 1).setShowOnlyIcon(showOnlyIcon);
		return this;
	}

	public ActionsBuilder<T, D> addAll(Actions<T, D> actions) {
		actionDefinitions.addAll(actions.actionDefinitions);
		actionGroupDefinitions.addAll(actions.actionGroupDefinitions);
		return this;
	}

	public ActionsBuilder<T, D> add(String key, ResponseServiceAction<T> responseServiceAction) {
		actionDefinitions.add(
				new ActionDescription<>(
						key,
						responseServiceAction.getButtonName(),
						responseServiceAction.getCustomParameters(),
						responseServiceAction::isAvailable,
						responseServiceAction::invoke,
						responseServiceAction::preActionSpecifier,
						responseServiceAction::preActionEventSpecifier,
						responseServiceAction::dataValidator,
						responseServiceAction.getIcon().getActionIconCode(),
						responseServiceAction.isIconWithText(),
						responseServiceAction.getScope(),
						responseServiceAction.isAutoSaveBefore()
				)
		);
		return this;
	}

	public Actions<T, D> build() {
		return new Actions<>(
				actionDefinitions,
				actionGroupDefinitions
		);
	}

}
