/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.api.util.i18n.LocalizationFormatter.uiMessage;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.SchedulerService;
import io.tesler.core.service.action.Actions;
import io.tesler.crudma.api.ScheduledJobService;
import io.tesler.crudma.dto.ScheduledJobDTO;
import io.tesler.crudma.dto.ScheduledJobDTO_;
import io.tesler.crudma.meta.ScheduledJobFieldMetaBuilder;
import io.tesler.model.core.entity.ScheduledJob;
import io.tesler.model.core.entity.ScheduledJob_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class ScheduledJobServiceImpl extends VersionAwareResponseService<ScheduledJobDTO, ScheduledJob> implements
		ScheduledJobService {

	@Autowired
	private SchedulerService schedulerService;

	public ScheduledJobServiceImpl() {
		super(ScheduledJobDTO.class, ScheduledJob.class, null, ScheduledJobFieldMetaBuilder.class);
	}

	@Override
	protected Specification<ScheduledJob> getParentSpecification(BusinessComponent bc) {
		return (root, cq, cb) -> cb.and(
				super.getParentSpecification(bc).toPredicate(root, cq, cb),
				// не показываем совсем уж системные задачи
				cb.equal(root.get(ScheduledJob_.system), false)
		);
	}

	@Override
	public Actions<ScheduledJobDTO> getActions() {
		return Actions.<ScheduledJobDTO>builder()
				.create().add()
				.save().add()
				.delete().add()
				.action("launchNow", uiMessage("action.launch"))
				.available(this::isServiceDefined).invoker(this::launchNow).add()
				.build();
	}

	@Override
	protected CreateResult<ScheduledJobDTO> doCreateEntity(final ScheduledJob entity, final BusinessComponent bc) {
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	private boolean isServiceDefined(BusinessComponent bc) {
		if (bc.getId() == null) {
			return false;
		}
		return baseDAO.findById(ScheduledJob.class, bc.getIdAsLong()).getService() != null;
	}

	private ActionResultDTO<ScheduledJobDTO> launchNow(final BusinessComponent bc, final ScheduledJobDTO data) {
		ScheduledJob job = baseDAO.findById(ScheduledJob.class, bc.getIdAsLong());
		schedulerService.launchNow(job);
		return new ActionResultDTO<>(entityToDto(bc, job));
	}

	@Override
	protected ActionResultDTO<ScheduledJobDTO> doUpdateEntity(ScheduledJob job, ScheduledJobDTO data,
			BusinessComponent bc) {
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(ScheduledJobDTO_.serviceName)) {
				onServiceChanged(job, validateServiceName(data.getServiceName()));
			}
			if (data.isFieldChanged(ScheduledJobDTO_.cronExpression)) {
				job.setCronExpression(validateCronExpression(data.getCronExpression()));
			}
			if (data.isFieldChanged(ScheduledJobDTO_.active)) {
				job.setActive(data.isActive());
			}
		}
		schedulerService.scheduleJob(job);
		return new ActionResultDTO<>(entityToDto(bc, job)).setAction(PostAction.refreshBc(bc));
	}

	protected void onServiceChanged(ScheduledJob job, LOV serviceName) {
		job.setService(serviceName);
		job.getParams().forEach(param -> baseDAO.delete(param));
	}

	private LOV validateServiceName(String title) {
		LOV service = DictionaryType.SCHEDULED_SERVICES.lookupName(title);
		if (service != null) {
			return service;
		}
		throw new BusinessException().addPopup(errorMessage("error.empty_service_name"));
	}

	private String validateCronExpression(String cronExpression) {
		try {
			return schedulerService.validateCronExpression(cronExpression);
		} catch (IllegalArgumentException ex) {
			throw new BusinessException().addPopup(errorMessage("error.wrong_cron_expression"));
		}
	}

	@Override
	public ActionResultDTO<ScheduledJobDTO> deleteEntity(BusinessComponent bc) {
		ScheduledJob job = baseDAO.findById(ScheduledJob.class, bc.getIdAsLong());
		schedulerService.removeJob(job);
		job.getParams().forEach(param -> baseDAO.delete(param));
		return super.deleteEntity(bc);
	}

}
