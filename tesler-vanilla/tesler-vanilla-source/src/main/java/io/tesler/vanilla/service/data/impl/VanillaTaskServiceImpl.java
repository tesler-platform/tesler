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

import static io.tesler.core.service.action.ActionAvailableChecker.NOT_NULL_ID;
import static io.tesler.vanilla.VanillaServiceAssociation.bcChildExample;
import static io.tesler.vanilla.VanillaServiceAssociation.legalResidentVanilla;
import static io.tesler.vanilla.VanillaServiceAssociation.taskExecutorVanilla;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.BusinessError.Entity;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.entity.FileEntity;
import io.tesler.model.core.entity.User;
import io.tesler.vanilla.dto.VanillaTaskDTO;
import io.tesler.vanilla.dto.VanillaTaskDTO_;
import io.tesler.vanilla.entity.VanillaCounterparty;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.entity.VanillaTask_;
import io.tesler.vanilla.service.action.VanillaTaskActionDownloadFile;
import io.tesler.vanilla.service.action.VanillaTaskActionRandomName;
import io.tesler.vanilla.service.data.VanillaTaskService;
import io.tesler.vanilla.service.meta.VanillaTaskFieldMetaBuilder;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class VanillaTaskServiceImpl extends VersionAwareResponseService<VanillaTaskDTO, VanillaTask> implements
		VanillaTaskService {

	@Autowired
	private VanillaTaskActionDownloadFile actionDownloadFile;

	@Autowired
	private SessionService sessionService;

	public VanillaTaskServiceImpl() {
		super(VanillaTaskDTO.class, VanillaTask.class, VanillaTask_.superVisedOrg, VanillaTaskFieldMetaBuilder.class);
	}

	@Override
	protected Specification<VanillaTask> getParentSpecification(BusinessComponent bc) {
		if (bcChildExample.isBc(bc)) {
			return (root, query, cb) -> cb.and();
		}
		return super.getParentSpecification(bc);
	}

	@Override
	protected CreateResult<VanillaTaskDTO> doCreateEntity(final VanillaTask entity, final BusinessComponent bc) {
		entity.setSuperVisedOrg(baseDAO.findById(VanillaCounterparty.class, bc.getParentIdAsLong()));
		entity.setInitiator(sessionService.getSessionUser());
		entity.setExecutor(sessionService.getSessionUser());
		entity.setCreateDate(DateTimeUtil.now());
		entity.setPlanDate(DateTimeUtil.now().plusDays(1));
		entity.setDayType(DictionaryType.DAY_TYPE.lookupName("Календарные"));
		entity.setTaskType(DictionaryType.TASK_TYPE.lookupName("Задача"));
		return new CreateResult<>(entityToDto(bc, baseDAO.findById(VanillaTask.class, baseDAO.save(entity))));
	}

	@Override
	protected ActionResultDTO<VanillaTaskDTO> doUpdateEntity(VanillaTask task, VanillaTaskDTO data,
			BusinessComponent bc) {
		if (data.isFieldChanged(VanillaTaskDTO_.executorId)) {
			task.setExecutor(baseDAO.findById(User.class, data.getExecutorId()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.name)) {
			task.setName(data.getName());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.job)) {
			task.setJob(data.getJob());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.supervisedMonitor)) {
			task.setSupervisedMonitor(data.getSupervisedMonitor());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.result)) {
			task.setResult(data.getResult());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.reportPeriod)) {
			task.setReportPeriod(DictionaryType.REPORT_PERIOD.lookupName(data.getReportPeriod()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.taskType)) {
			task.setTaskType(DictionaryType.TASK_TYPE.lookupName(data.getTaskType()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.priority)) {
			task.setPriority(DictionaryType.TASK_PRIORITY.lookupName(data.getPriority()));
			if (task.getPriority() != null && task.getPriority().getKey() != null) {
				switch (task.getPriority().getKey()) {
					case "LOW":
						task.setPlanDate(DateTimeUtil.now().plusDays(3));
						break;
					case "MIDDLE":
						task.setPlanDate(DateTimeUtil.now().plusDays(2));
						break;
					case "HIGH":
						task.setPlanDate(DateTimeUtil.now().plusDays(1));
						break;
					default:
						break;
				}
			}
		}
		if (data.isFieldChanged(VanillaTaskDTO_.activityType)) {
			task.setActivityType(DictionaryType.ACTIVITY_TYPE.lookupName(data.getActivityType()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.taskCategory)) {
			task.setTaskCategory(DictionaryType.TASK_CATEGORY.lookupName(data.getTaskCategory()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.periodicalType)) {
			task.setPeriodicalType(DictionaryType.PERIODICAL_TYPE.lookupName(data.getPeriodicalType()));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.createDate)) {
			Optional.of(data).map(VanillaTaskDTO::getCreateDate).ifPresent(task::setCreateDate);
		}
		if (data.isFieldChanged(VanillaTaskDTO_.reportDate)) {
			Optional.of(data).map(VanillaTaskDTO::getReportDate).ifPresent(task::setReportDate);
		}
		if (data.isFieldChanged(VanillaTaskDTO_.fileId)) {
			task.setFileEntity(data.getFileId() == null ? null
					: baseDAO.findById(FileEntity.class, NumberUtils.createLong(data.getFileId())));
		}
		if (data.isFieldChanged(VanillaTaskDTO_.comboConditionTest)) {
			task.setComboConditionTest(data.getComboConditionTest());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.moneyInputTest)) {
			task.setMoneyInputTest(data.getMoneyInputTest());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.numberInputTest)) {
			task.setNumberInputTest(data.getNumberInputTest());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.decimalInputTest)) {
			task.setDecimalInputTest(data.getDecimalInputTest());
		}
		if (data.isFieldChanged(VanillaTaskDTO_.percentInputTest)) {
			task.setPercentInputTest(data.getPercentInputTest());
		}
		return new ActionResultDTO<>(entityToDto(bc, task))
				.setAction(PostAction.refreshBc(legalResidentVanilla));
	}

	@Override
	public ActionResultDTO<VanillaTaskDTO> deleteEntity(BusinessComponent bc) {
		VanillaTask task = baseDAO.findById(VanillaTask.class, bc.getIdAsLong());
		if (task == null) {
			throw new IllegalArgumentException("Задача с id = " + bc.getId() + " не найдена.");
		}
		baseDAO.delete(task);
		return new ActionResultDTO<>();
	}

	@Override
	public Actions<VanillaTaskDTO> getActions() {
		return Actions.<VanillaTaskDTO>builder()
				.create().add()
				.save().add()
				.delete().add()
				.addGroup("vanilla-crud", "vanilla-crud", 2,
						Actions.<VanillaTaskDTO>builder()
								.action("open-url", "Yandex")
								.available(NOT_NULL_ID).invoker(this::actionOpenUrl).add()
								.action("download-file", "Скачать файл")
								.available(actionDownloadFile::fileExists)
								.invoker(actionDownloadFile::downloadFile).add()
								.action("error", "Test error")
								.invoker(this::actionError).add()
								.action("openPickList", "Назначить исполнителя")
								.invoker(this::openPickList).add()
								.build()
				)
				.addGroup("vanilla-custom", "vanilla-custom", 2,
						Actions.<VanillaTaskDTO>builder()
								.action("custom-action-1", "Взять в работу")
								.available(VanillaTaskActionRandomName::isAvailable1)
								.invoker(this::actionRandomName).add()
								.action("custom-action-2", "Согласовать")
								.available(VanillaTaskActionRandomName::isAvailable2)
								.invoker(this::actionRandomName).add()
								.action("custom-action-3", "Закрыть")
								.available(VanillaTaskActionRandomName::isAvailable3)
								.invoker(this::actionRandomName).add()
								.action("custom-action-4", "Создать дочернюю задачу")
								.available(VanillaTaskActionRandomName::isAvailable4)
								.invoker(this::actionRandomName).add()
								.action("custom-action-5", "Связать")
								.available(VanillaTaskActionRandomName::isAvailable5)
								.invoker(this::actionRandomName).add()
								.action("custom-action-6", "Рассчитать")
								.invoker(this::calculate).add()
								.build()
				)
				.build();
	}

	private ActionResultDTO<VanillaTaskDTO> actionOpenUrl(final BusinessComponent bc, final VanillaTaskDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.drillDown(DrillDownType.EXTERNAL, "https://ya.ru/"));
	}

	private ActionResultDTO<VanillaTaskDTO> openPickList(final BusinessComponent bc, final VanillaTaskDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.openPickList(taskExecutorVanilla.name()));
	}

	private ActionResultDTO<VanillaTaskDTO> actionError(final BusinessComponent bc, final VanillaTaskDTO data) {
		throw new BusinessException()
				.addPopup("Тест popup-error 1")
				.addPopup("Тест popup-error 2")
				.setEntity(new Entity(bc)
						.addField("name", "Такое название уже существует")
						.addField("reportDate", "Поле %field% задано неверно")
				);
	}

	private ActionResultDTO<VanillaTaskDTO> actionRandomName(final BusinessComponent bc, final VanillaTaskDTO data) {
		data.setName(UUID.randomUUID().toString());
		data.addChangedField(VanillaTaskDTO_.name);

		return new ActionResultDTO<>(updateEntity(bc, data).getRecord())
				.setAction(PostAction.refreshBc(legalResidentVanilla));
	}

	private ActionResultDTO<VanillaTaskDTO> calculate(final BusinessComponent bc, final VanillaTaskDTO data) {
		return new ActionResultDTO<>(data)
				.setAction(PostAction.delayedRefreshBC(legalResidentVanilla, 5));
	}

}
