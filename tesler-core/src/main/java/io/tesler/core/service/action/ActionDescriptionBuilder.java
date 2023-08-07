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

import static io.tesler.core.service.action.ActionAvailableChecker.and;
import static io.tesler.core.service.action.TeslerActionIconSpecifier.WITHOUT_ICON;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionType;
import io.tesler.core.dto.rowmeta.PreAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActionDescriptionBuilder<T extends DataResponseDTO, D extends BcDescription> {

	private String key;

	private String text;

	private ActionAvailableChecker<D> baseActionAvailableChecker;

	private ActionAvailableChecker<D> actionAvailableChecker;

	private ActionInvoker<T, D> actionInvoker;

	private PreActionSpecifier preActionSpecifier;

	private PreActionEventSpecifier preActionEventSpecifier;

	private DataValidator<T> dataValidator;

	private ActionsBuilder<T, D> actionsBuilder;

	private ActionIconSpecifier iconCode = WITHOUT_ICON;

	private boolean showOnlyIcon = false;

	private ActionScope actionScope = ActionScope.RECORD;

	private Map<String, String> customParameter;

	private boolean autoSaveBefore = true;

	private static <T extends DataResponseDTO, D extends BcDescription> ActionInvoker<T, D> withoutUpdate(ActionInvoker<T, D> wrapped) {

		return new ActionInvoker<T, D>() {
			@Override
			public ActionResultDTO<T> invoke(BusinessComponent<D> bc, T data) {
				return wrapped.invoke(bc, data);
			}

			@Override
			public boolean isUpdateRequired() {
				return false;
			}

		};

	}

	private static <T extends DataResponseDTO, D extends BcDescription> ActionInvoker<T, D> withUpdate(ActionInvoker<T, D> wrapped) {

		return new ActionInvoker<T, D>() {
			@Override
			public ActionResultDTO<T> invoke(BusinessComponent<D> bc, T data) {
				return wrapped.invoke(bc, data);
			}

			@Override
			public boolean isUpdateRequired() {
				return true;
			}

		};

	}

	public ActionDescriptionBuilder<T, D> action(String key, String text) {
		this.key = key;
		this.text = text;
		return this;
	}

	public ActionDescriptionBuilder<T, D> action(ActionType actionType) {
		if (nonNull(actionType)) {
			this.key = actionType.getType();
			this.text = actionType.getText().get();
			this.iconCode = actionType.getIcon();
			this.showOnlyIcon = true;
			this.baseActionAvailableChecker = (ActionAvailableChecker<D>) actionType.getBaseAvailableChecker();
		}
		return this;
	}

	public ActionDescriptionBuilder<T, D> available(ActionAvailableChecker<D> actionAvailableChecker) {
		this.actionAvailableChecker = actionAvailableChecker;
		return this;
	}

	public ActionDescriptionBuilder<T, D> invoker(ActionInvoker<T, D> actionInvoker) {
		this.actionInvoker = actionInvoker;
		return this;
	}

	public ActionDescriptionBuilder<T, D> text(String text) {
		this.text = text;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withPreAction(PreActionSpecifier preActionSpecifier) {
		this.preActionSpecifier = preActionSpecifier;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withPreAction(PreAction preAction) {
		this.preActionSpecifier = bc -> preAction;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withIcon(ActionIconSpecifier icon, boolean showOnlyIcon) {
		this.iconCode = icon;
		this.showOnlyIcon = showOnlyIcon;
		return this;
	}

	public ActionDescriptionBuilder<T, D> scope(ActionScope actionScope) {
		this.actionScope = actionScope;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withCustomParameter(Map<String, String> parametersTuple) {
		this.customParameter = parametersTuple;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withAutoSaveBefore() {
		this.autoSaveBefore = true;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withoutAutoSaveBefore() {
		this.autoSaveBefore = false;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withoutIcon() {
		this.iconCode = WITHOUT_ICON;
		this.showOnlyIcon = false;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withPreActionEvents(PreActionEventSpecifier preActionEventSpecifier) {
		this.preActionEventSpecifier = preActionEventSpecifier;
		return this;
	}

	public ActionDescriptionBuilder<T, D> withPreActionEvents(PreActionEvent... preActionEvents) {
		this.preActionEventSpecifier = bc -> nonNull(preActionEvents) ? Arrays.asList(preActionEvents) : null;
		return this;
	}

	public ActionDescriptionBuilder<T, D> validator(DataValidator<T> dataValidator) {
		this.dataValidator = dataValidator;
		return this;
	}

	ActionDescriptionBuilder<T, D> withBuilder(ActionsBuilder<T, D> actionsBuilder) {
		this.actionsBuilder = actionsBuilder;
		return this;
	}

	public ActionsBuilder<T, D> add(Boolean updateRequired) {
		ActionDescription<T, D> actionDescription = this.build(updateRequired);
		this.actionsBuilder.addAction(actionDescription);
		return this.actionsBuilder;
	}

	public ActionsBuilder<T, D> add() {
		return add(null);
	}

	public ActionDescription<T, D> build(Boolean updateRequired) {
		ActionInvoker<T, D> invoker;
		if (actionInvoker == null) {
			invoker = ActionInvoker.UNSUPPORTED_OPERATION;
		} else if (updateRequired == null) {
			invoker = actionInvoker;
		} else {
			invoker = updateRequired ? withUpdate(actionInvoker) : withoutUpdate(actionInvoker);
		}
		return new ActionDescription<>(
				key,
				text,
				customParameter,
				and(
						defaultIfNull(baseActionAvailableChecker, ActionAvailableChecker.ALWAYS_TRUE),
						defaultIfNull(actionAvailableChecker, ActionAvailableChecker.ALWAYS_TRUE)
				),
				invoker,
				defaultIfNull(preActionSpecifier, PreActionSpecifierType.WITHOUT_PREACTION),
				defaultIfNull(preActionEventSpecifier, bc -> null),
				defaultIfNull(dataValidator, (bc, data, entityDto) -> Collections.emptyList()),
				iconCode.getActionIconCode(),
				showOnlyIcon,
				actionScope,
				autoSaveBefore
		);
	}

}
