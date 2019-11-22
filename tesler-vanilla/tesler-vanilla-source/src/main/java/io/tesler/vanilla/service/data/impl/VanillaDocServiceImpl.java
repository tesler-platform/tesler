/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.data.impl;

import static io.tesler.vanilla.VanillaServiceAssociation.bcChildExample;
import static io.tesler.vanilla.VanillaServiceAssociation.bcExample;
import static io.tesler.vanilla.VanillaServiceAssociation.bcPagination;
import static io.tesler.vanilla.VanillaServiceAssociation.bcPreAction;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.MessageType;
import io.tesler.core.dto.multivalue.MultivalueField;
import io.tesler.core.dto.multivalue.MultivalueFieldSingleValue;
import io.tesler.core.dto.multivalue.MultivalueOptionType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.dto.rowmeta.PreAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.ActionIcon;
import io.tesler.core.service.action.ActionScope;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.action.PreActionEvent;
import io.tesler.model.core.entity.FileEntity;
import io.tesler.vanilla.dto.VanillaDocDTO;
import io.tesler.vanilla.dto.VanillaDocDTO_;
import io.tesler.vanilla.entity.VanillaFileEntity;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord_;
import io.tesler.vanilla.entity.VanillaTask_;
import io.tesler.vanilla.service.action.VanillaPreActionCondition;
import io.tesler.vanilla.service.action.VanillaPreActionConditionHolder;
import io.tesler.vanilla.service.data.VanillaDocService;
import io.tesler.vanilla.service.meta.VanillaDocFieldMetaBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

@Service
public class VanillaDocServiceImpl extends VersionAwareResponseService<VanillaDocDTO, VanillaTask> implements
		VanillaDocService {

	@Autowired
	private VanillaPreActionCondition condition;

	public VanillaDocServiceImpl() {
		super(VanillaDocDTO.class, VanillaTask.class, null, VanillaDocFieldMetaBuilder.class);
		preActionConditionHolderDataResponse = VanillaPreActionConditionHolder.class;
	}

	@Override
	public ResultPage<VanillaDocDTO> getList(BusinessComponent bc) {
		QueryParameters params = bc.getParameters();
		if (bcPagination.isBc(bc)) {
			return entitiesToDtos(bc, baseDAO
					.getList(VanillaTask.class, VanillaDocDTO.class, (root, cq, cb) -> cb.and(), params));
		}
		ResultPage<VanillaDocDTO> resultPage = entitiesToDtos(bc, baseDAO
				.getList(VanillaTask.class, VanillaDocDTO.class, (root, cq, cb) -> cb.and(), params));
		resultPage.getResult().forEach(task -> task.setCreatedDate(params.getDateTo()));
		return resultPage;
	}

	/**
	 * Возвращает поле множественного выбора для документации
	 *
	 * @param dto - VanillaDocDTO - ДТО данного сервиса
	 * @return пример поля множественного выбора
	 */
	//TODO сейчас делает по 2 запроса на строку, переписать при изменении модели на документации
	private MultivalueField getMultivalue(VanillaDocDTO dto) {
		List<VanillaTaskRelatedRecord> relatedRecords = baseDAO.getList(
				VanillaTaskRelatedRecord.class,
				Specifications.where(
						(root, cq, cb) -> cb.equal(
								root.get(VanillaTaskRelatedRecord_.parTaskId),
								NumberUtils.createLong(dto.getId())
						)
				)
		);
		if (!relatedRecords.isEmpty()) {
			List<Long> relatedIds = relatedRecords.stream().map(VanillaTaskRelatedRecord::getChildId)
					.collect(Collectors.toList());
			List<VanillaTask> vanillaTasks = baseDAO.getList(
					VanillaTask.class,
					Specifications.where(
							(root, cq, cb) -> root.get(VanillaTask_.id).in(relatedIds)
					)
			);
			return vanillaTasks.stream().collect(MultivalueField.toMultivalueField(
					vanillaTask -> vanillaTask.getId().toString(),
					VanillaTask::getName,
					ImmutableMap.of(
							MultivalueOptionType.HINT, vanillaTask -> vanillaTask.getId().toString(),
							MultivalueOptionType.DRILL_DOWN_TYPE, vanillaTask -> DrillDownType.INNER.getValue(),
							MultivalueOptionType.DRILL_DOWN_LINK, vanillaTask -> "screen/doc"
									+ "/view/docAssocListPopup/bcExample/"
									+ dto.getId()
									+ "/bcExampleRelatedRecords/"
									+ vanillaTask.getId().toString()
					)
			));
		}
		return new MultivalueField();
	}

	/**
	 * Проставляет значение Multivalue поля на Entity
	 *
	 * @param task - Entity
	 * @param multivalue - Multivalue - поле
	 */
	private void setMultivalue(VanillaTask task, MultivalueField multivalue) {
		if (multivalue == null) {
			return;
		}
		List<VanillaTaskRelatedRecord> relatedRecords = baseDAO.getList(
				VanillaTaskRelatedRecord.class,
				Specifications.where(
						(root, cq, cb) -> cb.equal(
								root.get(VanillaTaskRelatedRecord_.parTaskId),
								task.getId()
						)
				)
		);
		List<Long> multivalueIds = multivalue.getValues().stream().map(MultivalueFieldSingleValue::getId)
				.map(NumberUtils::createLong).collect(Collectors.toList());
		relatedRecords.forEach(relatedRecord -> {
			if (multivalueIds.contains(relatedRecord.getChildId())) {
				multivalueIds.remove(relatedRecord.getChildId());
			} else {
				baseDAO.delete(relatedRecord);
			}
		});
		multivalueIds.forEach(multivalueId -> {
			VanillaTaskRelatedRecord relRec = new VanillaTaskRelatedRecord();
			relRec.setChildId(multivalueId);
			relRec.setParTaskId(task.getId());
			relRec.setChildType("Задача");
			baseDAO.save(relRec);
		});
	}


	@Override
	protected ActionResultDTO<VanillaDocDTO> doUpdateEntity(VanillaTask task, VanillaDocDTO data,
			BusinessComponent bc) {
		if (data.isFieldChanged(VanillaDocDTO_.testFileId)) {
			Long testFileId = NumberUtils.createLong(data.getTestFileId());
			if (testFileId != null) {
				FileEntity fileEntity = baseDAO.findById(FileEntity.class, testFileId);
				if (fileEntity != null) {
					task.setFileEntity(fileEntity);
				}
			} else {
				task.setFileEntity(null);
			}
		}

		if (data.isFieldChanged(VanillaDocDTO_.testSourceFileId)) {
			Long testFileId = NumberUtils.createLong(data.getTestSourceFileId());
			if (testFileId != null) {
				VanillaFileEntity fileEntity = baseDAO.findById(VanillaFileEntity.class, testFileId);
				task.setVanillaFileEntity(fileEntity);
			} else {
				task.setVanillaFileEntity(null);
			}
		}

		if (data.isFieldChanged(VanillaDocDTO_.maskedPhone)) {
			task.setPhone(data.getMaskedPhone());
		}

		if (data.isFieldChanged(VanillaDocDTO_.testMultivalue)) {
			setMultivalue(task, data.getTestMultivalue());
		}

		if (data.isFieldChanged(VanillaDocDTO_.maskedPostalCode)) {
			task.setPostalCode(data.getMaskedPostalCode());
		}
		VanillaDocDTO updatedDto = entityToDto(bc, task);

		if (data.isFieldChanged(VanillaDocDTO_.forceTaskStatus)) {
			updatedDto.setForceTaskStatus(DictionaryType.TASK_STATUS.lookupName(data.getForceTaskStatus())
					.getKey());
			updatedDto.setForceName(data.getForceTaskStatus());
		}
		if (data.isFieldChanged(VanillaDocDTO_.errorType)) {
			throw new BusinessException()
					.addPopup("Пример бизнес ошибки");
		}
		if (data.isFieldChanged(VanillaDocDTO_.errorCategory)) {
			throw new NullPointerException();
		}
		updatedDto.setEphemeral(data.getEphemeral());

		return new ActionResultDTO<>(updatedDto);

	}

	@Override
	public Actions<VanillaDocDTO> getActions() {
		return Actions.<VanillaDocDTO>builder()
				.create().add()
				.save()
				.withPreAction(bc -> isBcPreAction(bc) ? PreAction.confirm("Сохранить изменения?") : null)
				.withPreActionEvents(bc -> isBcPreAction(bc) ?
						Arrays.asList(
								PreActionEvent.confirm("pre-save-1", condition,
										"Вы изменили поле testDate, вы уверены, что хотите сохранить запись?"
								),
								PreActionEvent.info("pre-save-2", condition,
										"Выполняется сохранение после изменения..."
								)
						) : null)
				.add()
				.addGroup("messages", "сообщения", 1, Actions.<VanillaDocDTO>builder()
						.action("show-error", "Error MSG").available(this::notNullAndPreAction)
						.invoker(this::actionShowError).add()
						.action("show-warning", "Warning MSG").available(this::notNullAndPreAction)
						.invoker(this::actionShowWarning)
						.add()
						.action("show-info", "Info MCG").available(this::notNullAndPreAction)
						.invoker(this::actionShowInfo).add()
						.action("business-exception-with-refresh", "Business Exception with Refresh BC")
						.available(this::notNullAndPreAction)
						.invoker(this::actionThrowBusinessExceptionWithPostActionRefresh).add()
						.action("business-exception-pa", "Business Exception with Post Action")
						.available(this::notNullAndPreAction).invoker(this::actionThrowBusinessExceptionWithPostAction)
						.add()
						.action("business-exception", "Business Exception").available(this::notNullAndPreAction)
						.invoker(this::actionThrowBusinessException).add()
						.build())
				.addGroup("drillDowns", "дриллдауны", 1, Actions.<VanillaDocDTO>builder()
						.action("open-url", "Открыть Yandex").available(this::notNullAndPreAction)
						.invoker(this::actionOpenUrl)
						.withPreAction(PreAction.confirm()).add()
						.action("open-url-new", "Открыть Yandex в новой вкладке").available(this::notNullAndPreAction)
						.invoker(this::actionOpenUrlNew).withPreAction(PreAction.info()).add()
						.build()
				)
				.addGroup("updates-bc", "обновления БК", 1, Actions.<VanillaDocDTO>builder()
						.action("refresh-bc", "Обновить текущую БК").scope(ActionScope.RECORD)
						.available(bc1 -> !isBcPreAction(bc1))
						.invoker(this::actionRefreshBC).add()
						.action("delayed-refresh-bc", "Обновить текущую БК через 10 сек.").scope(ActionScope.RECORD)
						.available(bc1 -> !isBcPreAction(bc1))
						.invoker(this::actionDelayedRefreshBC).add()
						.build()
				)
				.action("bars", "Открыть пик-лист").withIcon(ActionIcon.BARS, false)
				.available(this::notNullAndPreAction).invoker(this::openPickList)
				.add()
				.delete().withPreAction(PreAction.error()).add()
				.action("pre-invoke-confirm", "Подтверждение").available(this::isBcPreAction)
				.withPreAction(PreAction.confirm("Обновить?")).invoker(this::actionRefreshBC).add()
				.action("pre-invoke-info", "Предупреждение").available(this::isBcPreAction)
				.withPreAction(PreAction.info("Выполняется обновление")).invoker(this::actionRefreshBC).add()
				.action("pre-invoke-error", "Ошибка").available(this::isBcPreAction)
				.withPreAction(PreAction.error("Невозможно выполнить")).add()
				.action("pre-invoke-custom", "Подтверждение по условию").available(this::isBcPreAction)
				.withPreActionEvents(
						PreActionEvent.confirm("dataChanged", condition,
								"Вы изменили поле Дата, продолжить выполнение действия?"
						),
						PreActionEvent.info("save_2", condition, "Выполняется сохранение после изменения..."),
						PreActionEvent.info("save_3", condition, "Выполняется сохранение после изменения 2...")
				)
				.invoker(this::actionRefreshBC)
				.add()
				.build();
	}

	private boolean isBcPreAction(BusinessComponent bc) {
		return bcPreAction.isBc(bc);
	}

	private boolean notNullAndPreAction(BusinessComponent bc) {
		return bc.getId() != null && !isBcPreAction(bc);
	}

	private ActionResultDTO<VanillaDocDTO> actionRefreshBC(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.refreshBc(bc));
	}

	private ActionResultDTO<VanillaDocDTO> actionDelayedRefreshBC(final BusinessComponent bc,
			final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.delayedRefreshBC(bc, 10));
	}

	private ActionResultDTO<VanillaDocDTO> actionOpenUrl(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.drillDown(DrillDownType.EXTERNAL, "https://ya.ru/"));
	}

	@Override
	protected VanillaDocDTO entityToDto(BusinessComponent bc, VanillaTask entity) {
		VanillaDocDTO dto = super.entityToDto(bc, entity);
		dto.setTestMultivalue(getMultivalue(dto));
		int multivalueCount = dto.getTestMultivalue().getValues().size();
		dto.setTestMultivalueCount("Всего задач: " + multivalueCount);
		return dto;
	}

	private ActionResultDTO<VanillaDocDTO> actionOpenUrlNew(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.drillDown(DrillDownType.EXTERNAL_NEW, "https://ya.ru/"));
	}

	private ActionResultDTO<VanillaDocDTO> actionShowError(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.showMessage(MessageType.ERROR, "Сообщение об ошибке для пользователя"));
	}

	private ActionResultDTO<VanillaDocDTO> actionShowWarning(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.showMessage(MessageType.WARNING, "Предупреждение для пользователя"));
	}

	private ActionResultDTO<VanillaDocDTO> actionShowInfo(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.showMessage(MessageType.INFO, "Информация для пользователя"));
	}

	private ActionResultDTO<VanillaDocDTO> actionThrowBusinessException(final BusinessComponent bc,
			final VanillaDocDTO data) {
		throw new BusinessException()
				.addPopup("Бизнес ошибка");
	}

	private ActionResultDTO<VanillaDocDTO> actionThrowBusinessExceptionWithPostAction(final BusinessComponent bc,
			final VanillaDocDTO data) {
		throw new BusinessException()
				.addPopup("Бизнес ошибка с дополнительным сообщением")
				.addPostAction(PostAction.showMessage(MessageType.INFO, "Дополнительное сообщение"));
	}

	private ActionResultDTO<VanillaDocDTO> actionThrowBusinessExceptionWithPostActionRefresh(final BusinessComponent bc,
			final VanillaDocDTO data) {
		throw new BusinessException()
				.addPopup("Бизнес ошибка с обновлением БК")
				.addPostAction(PostAction.refreshBc(bcExample));
	}

	private ActionResultDTO<VanillaDocDTO> openPickList(final BusinessComponent bc, final VanillaDocDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.openPickList(bcChildExample.name()));
	}

	@Override
	public ActionResultDTO<VanillaDocDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected CreateResult<VanillaDocDTO> doCreateEntity(final VanillaTask entity, final BusinessComponent bc) {
		return new CreateResult<>(entityToDto(bc, entity))
				.setAction(PostAction.drillDown(DrillDownType.INNER, "screen/doc/view/errors"));
	}

	@Override
	protected VanillaTask create(BusinessComponent bc) {
		return baseDAO.findById(VanillaTask.class, 1L);
	}

}
