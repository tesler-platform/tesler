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

package io.tesler.core.crudma;

import static com.google.common.collect.Sets.immutableEnumSet;
import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.core.crudma.CrudmaActionType.INVOKE;
import static io.tesler.core.crudma.CrudmaActionType.PREVIEW;
import static io.tesler.core.crudma.CrudmaActionType.UPDATE;
import static io.tesler.core.dto.DrillDownType.INNER;
import static io.tesler.core.dto.rowmeta.PostAction.BasePostActionType.DRILL_DOWN;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.DataResponseDTO_;
import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.api.data.dto.rowmeta.PreviewResult;
import io.tesler.api.security.obligations.IObligationSet;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.controller.BCFactory;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.CrudmaActionHolder.CrudmaAction;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.MessageType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionType;
import io.tesler.core.dto.rowmeta.ActionsDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.dto.rowmeta.PostAction.BasePostActionField;
import io.tesler.core.security.PolicyEnforcer;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.core.service.action.ActionAvailableChecker;
import io.tesler.core.service.action.ActionDescriptionBuilder;
import io.tesler.core.util.session.BcState;
import io.tesler.core.util.session.BcState.State;
import io.tesler.core.util.session.CreationState;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrudmaGateway {

	private final CrudmaFactory crudmaFactory;

	private final BCFactory bcFactory;

	private final ResponseFactory respFactory;

	private final BcRegistry bcRegistry;

	private final TransactionService txService;

	private final BcState bcState;

	private final ApplicationEventPublisher eventPublisher;

	private final PolicyEnforcer policyEnforcer;

	public DataResponseDTO get(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		DataResponseDTO result = invoke(crudmaAction, () -> getCrudmaService(bc).get(bc), readOnly);
		if (result != null && bcState.isNew(bc)) {
			result.setVstamp(-1L);
		}
		return result;
	}

	public ResultPage<? extends DataResponseDTO> getAll(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		return invoke(crudmaAction, () -> getCrudmaService(bc).getAll(bc), readOnly);
	}

	public MetaDTO create(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		final InterimResult result = invoke(crudmaAction, () -> {
			final Crudma crudmaService = getCrudmaService(bc);
			final CreateResult createResult = crudmaService.create(bc);
			if (readOnly) {
				// мы откатываем транзакцию, помечаем DTO специальным флагом
				createResult.getRecord().setVstamp(-1L);
			}
			final MetaDTO metaNew = crudmaService.getMetaNew(bc, createResult);
			return new InterimResult(
					getBcForState(bc.withId(createResult.getRecord().getId()), createResult.getPostActions()),
					createResult.getRecord(),
					metaNew,
					createResult.getCreationState()
			);
		}, InterimResult::getDto, readOnly);
		if (readOnly) {
			bcState.set(result.getBc(), null, result.getCreationState());
			addActionCancel(bc, result.getMeta().getRow().getActions());
		}
		return result.getMeta();
	}

	public MetaDTO preview(CrudmaAction crudmaAction, Map<String, Object> data) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		final InterimResult result = invoke(crudmaAction, () -> {
			final Crudma crudmaService = getCrudmaService(bc);
			final PreviewResult previewResult = crudmaService.preview(bc, data);
			if (readOnly) {
				// мы откатываем транзакцию, поэтому ставим старую версию
				previewResult.getResponseDto().setVstamp(previewResult.getRequestDto().getVstamp());
			}
			final MetaDTO metaNew = crudmaService.getOnFieldUpdateMeta(bc, previewResult.getResponseDto());
			return new InterimResult(bc, previewResult.getRequestDto(), metaNew, bcState.getCreationState(bc));
		}, InterimResult::getMeta, readOnly);
		if (readOnly) {
			bcState.set(result.getBc(), result.getDto(), result.getCreationState());
			if (bcState.isNew(bc)) {
				addActionCancel(bc, result.getMeta().getRow().getActions());
			}
		}
		return result.getMeta();
	}

	public ActionResultDTO update(CrudmaAction crudmaAction, Map<String, Object> data) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		return invoke(crudmaAction, () -> getCrudmaService(bc).update(bc, data), readOnly);
	}

	public ActionResultDTO delete(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		if (bc.getId() == null) {
			return new ActionResultDTO().setAction(
					PostAction.showMessage(MessageType.WARNING, errorMessage("warn.no_record_to_delete"))
			);
		}
		// нужно для аудита
		DataResponseDTO[] data = new DataResponseDTO[1];
		return invoke(crudmaAction, () -> {
					Crudma crudma = getCrudmaService(bc);
					data[0] = crudma.get(bc);
					return crudma.delete(bc);
				},
				resultDTO -> data[0],
				readOnly
		);
	}

	public ActionResultDTO invokeAction(CrudmaAction crudmaAction, Map<String, Object> data) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		String actionName = crudmaAction.getName();
		if (Objects.equals(ActionType.CANCEL_CREATE.getType(), actionName) && bcState.isNew(bc)) {
			bcState.clear();
			BcDescription description = bc.getDescription();
			if (description instanceof InnerBcDescription) {
				return getResponseService(bc).onCancel(bc);
			}
			return new ActionResultDTO().setAction(PostAction.postDelete());
		}
		return invoke(crudmaAction, () -> getCrudmaService(bc).invokeAction(bc, actionName, data), readOnly);
	}

	public AssociateResultDTO associate(CrudmaAction crudmaAction, List<AssociateDTO> data) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		return invoke(crudmaAction, () -> getCrudmaService(bc).associate(bc, data), readOnly);
	}

	public MetaDTO getMeta(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		final MetaDTO meta = invoke(crudmaAction, () -> getCrudmaService(bc).getMeta(bc), readOnly);
		if (bcState.isNew(bc)) {
			addActionCancel(bc, meta.getRow().getActions());
			meta.getRow().getFields().get(DataResponseDTO_.vstamp.getName()).setCurrentValue(-1L);
		}
		return meta;
	}

	public MetaDTO getMetaEmpty(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		return invoke(crudmaAction, () -> getCrudmaService(bc).getMetaEmpty(bc), readOnly);
	}

	public long count(CrudmaAction crudmaAction) {
		BusinessComponent bc = crudmaAction.getBc();
		boolean readOnly = isReadOnly(crudmaAction);
		return invoke(crudmaAction, () -> getCrudmaService(bc).count(bc), readOnly);
	}

	private <T> T invoke(CrudmaAction crudmaAction, Invoker<T, RuntimeException> invoker, boolean readOnly) {
		return invoke(crudmaAction, invoker, t -> t, readOnly);
	}

	private <T, V> T invoke(
			CrudmaAction crudmaAction,
			Invoker<T, RuntimeException> invoker,
			Function<T, V> resultExtractor,
			boolean readOnly) {
		T result = null;
		Exception exception = null;
		try {
			log.debug(crudmaAction.getDescription());
			result = doInvoke(crudmaAction, invoker, readOnly);
			return result;
		} catch (Exception ex) {
			exception = ex;
			throw ex;
		} finally {
			eventPublisher.publishEvent(
					new CrudmaEvent<>(
							this,
							crudmaAction,
							result == null ? null : resultExtractor.apply(result), exception
					)
			);
		}
	}

	private <T> T doInvoke(CrudmaAction crudmaAction, Invoker<T, RuntimeException> invoker, boolean readOnly) {
		BusinessComponent bc = crudmaAction.getBc();
		CrudmaActionType action = crudmaAction.getActionType();
		final Invoker<T, RuntimeException> targetInvoker = () -> {
			restoreBcState(bc, action);
			// проверяем, что действие можно выполнить и
			// набор обязательств, которые нужно соблюсти
			IObligationSet obligationSet = policyEnforcer.check(crudmaAction);
			// делаем набор обязательств доступным отовсюду
			crudmaAction.setObligationSet(obligationSet);
			final T invokeResult = invoker.invoke();
			if (action != null && needClearBcState(readOnly, action)) {
				bcState.clear();
			}
			// модифицируем результат выполнения действия
			return policyEnforcer.transform(invokeResult, crudmaAction, obligationSet);
		};
		if (readOnly) {
			return txService.invokeInNewRollbackOnlyTx(targetInvoker);
		}
		return txService.invokeInNewTx(targetInvoker);
	}

	/**
	 * Определяет нужно ли выполнять действие в read-only транзакции
	 *
	 * @param action действие
	 * @return нужно ли использовать read-only транзакцию
	 */
	private boolean isReadOnly(CrudmaAction action) {
		CrudmaActionType actionType = action.getActionType();
		BusinessComponent bc = action.getBc();
		BcDescription description = bc.getDescription();
		// todo: implement for extreme bc
		boolean readOnly = false;
		if (description instanceof InnerBcDescription) {
			readOnly = actionType != null && actionType.isReadOnly();
			ResponseService<?, ?> responseService = getResponseService(bc);
			if (CrudmaActionType.CREATE == actionType) {
				readOnly &= responseService.isDeferredCreationSupported(bc);
			}
		}
		return readOnly;
	}

	/**
	 * Определяет нужно ли очищать состояние в сесии
	 *
	 * @param readOnly использовалась ли read-only транзакция
	 * @param action действие
	 * @return нужно ли очищать состояние в сесии
	 */
	private boolean needClearBcState(boolean readOnly, CrudmaActionType action) {
		// todo: здесь должно быть написано что-то более сложное
		return !readOnly || action == CrudmaActionType.PREVIEW;
	}

	private void restoreBcState(final BusinessComponent currentBc, final CrudmaActionType action) {
		for (final BusinessComponent bc : Arrays.asList(getParentBcForRestore(currentBc), currentBc)) {
			if (bc == null) {
				continue;
			}
			final State state = bcState.getState(bc);
			if (state == null) {
				continue;
			}
			if (!(bc.getDescription() instanceof InnerBcDescription)) {
				continue;
			}
			final ResponseService<?, ?> responseService = getResponseService(bc);
			if (state.getCreationState() != null) {
				responseService.createEntity(bc, state.getCreationState());
			}
			// эти действия сами вызывают update
			if (state.getDto() != null && !immutableEnumSet(UPDATE, PREVIEW, INVOKE).contains(action)) {
				responseService.updateEntity(bc, state.getDto());
			}
		}
	}

	private ResponseService<?, ?> getResponseService(BusinessComponent bc) {
		return respFactory.getService(bc.getDescription());
	}

	private BusinessComponent getParentBcForRestore(final BusinessComponent currentBc) {
		if (currentBc.getHierarchy() == null || currentBc.getHierarchy().getParent() == null) {
			return null;
		}
		return bcFactory.getBusinessComponent(
				currentBc.getHierarchy().getParent(),
				QueryParameters.onlyDatesQueryParameters(
						currentBc.getParameters()
				)
		);
	}

	private void addActionCancel(BusinessComponent bc, final ActionsDTO actions) {
		boolean hasCancelAction = false;
		for (ActionDTO action : actions) {
			if (ActionType.DELETE.isTypeOf(action)) {
				action.setAvailable(false);
			}
			if (ActionType.CANCEL_CREATE.isTypeOf(action)) {
				action.setAvailable(true);
				hasCancelAction = true;
			}
		}

		if (hasCancelAction) {
			return;
		}

		actions.addMethod(
				0,
				new ActionDescriptionBuilder<>()
						.action(ActionType.CANCEL_CREATE)
						.available(ActionAvailableChecker.ALWAYS_TRUE)
						.withoutAutoSaveBefore()
						.build(null),
				bc
		);
	}

	private BusinessComponent getBcForState(final BusinessComponent bc, final List<PostAction> postActions) {
		for (final PostAction postAction : postActions) {
			if (DRILL_DOWN.equals(postAction.getAttribute(BasePostActionField.TYPE)) && INNER.getValue()
					.equals(postAction.getAttribute(BasePostActionField.DRILL_DOWN_TYPE))) {
				final String[] url = postAction.getAttribute(BasePostActionField.URL).split("/");
				if (Objects.equals(bc.getId(), url[url.length - 1])) {
					return new BusinessComponent(
							bc.getId(),
							bc.getParentId(),
							bcRegistry.getBcDescription(url[url.length - 2])
					);
				}
			}
		}
		return bc;
	}

	private Crudma getCrudmaService(final BusinessComponent bc) {
		return crudmaFactory.get(bc.getDescription());
	}

	@Getter
	@RequiredArgsConstructor
	private static class InterimResult implements MetaContainer<MetaDTO> {

		private final BusinessComponent bc;

		private final DataResponseDTO dto;

		private final MetaDTO meta;

		private final CreationState creationState;

		@Override
		public void transformMeta(Function<MetaDTO, MetaDTO> function) {
			function.apply(meta);
		}

	}

}
