/*-
 * #%L
 * IO Tesler - Dictionary Links Implementation
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

package io.tesler.source.service.data.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.api.util.i18n.LocalizationFormatter.uiMessage;

import io.tesler.core.bc.InnerBcTypeAware;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.ResponseService;
import io.tesler.core.service.action.ActionAvailableChecker;
import io.tesler.core.service.action.Actions;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService_;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule_;
import io.tesler.source.dto.CustomizableResponseServiceDto;
import io.tesler.source.dto.CustomizableResponseServiceDto_;
import io.tesler.source.engine.LinkedDictionaryServiceImpl.LinkedDictionaryCache;
import io.tesler.source.service.data.CustomizableResponseSrvsService;
import io.tesler.source.service.meta.CustomizableResponseServiceFieldMetaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomizableRespSrvsServiceImpl extends
		VersionAwareResponseService<CustomizableResponseServiceDto, CustomizableResponseService> implements
		CustomizableResponseSrvsService {

	@Autowired
	private LinkedDictionaryCache linkedDictionaryCache;

	@Autowired
	private BcRegistry bcRegistry;

	@Autowired
	private InnerBcTypeAware innerBcTypeAware;

	public CustomizableRespSrvsServiceImpl() {
		super(
				CustomizableResponseServiceDto.class,
				CustomizableResponseService.class,
				null,
				CustomizableResponseServiceFieldMetaBuilder.class
		);
	}

	@Override
	public ActionResultDTO<CustomizableResponseServiceDto> deleteEntity(BusinessComponent bc) {
		if (bc.getIdAsLong() != null) {
			Long rulesCount = baseDAO.getCount(DictionaryLnkRule.class, (root, cq, cb) ->
					cb.equal(
							root.get(DictionaryLnkRule_.service).get(CustomizableResponseService_.id),
							bc.getIdAsLong()
					)
			);
			if (rulesCount > 0) {
				throw new BusinessException().addPopup(errorMessage("error.cant_delete_service_rules_exist"));
			}
		}
		return super.deleteEntity(bc);
	}

	@Override
	public Actions<CustomizableResponseServiceDto> getActions() {
		return Actions.<CustomizableResponseServiceDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.action("clearCache", uiMessage("action.clearCache"))
				.available(ActionAvailableChecker.ALWAYS_TRUE)
				.invoker((bc, data) -> {
					linkedDictionaryCache.evictRules();
					return new ActionResultDTO<>();
				}).add()
				.build();
	}

	@Override
	protected CreateResult<CustomizableResponseServiceDto> doCreateEntity(final CustomizableResponseService entity,
			final BusinessComponent bc) {
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<CustomizableResponseServiceDto> doUpdateEntity(CustomizableResponseService entity,
			CustomizableResponseServiceDto data, BusinessComponent bc) {
		if (data.isFieldChanged(CustomizableResponseServiceDto_.serviceName)) {
			if (!entity.getRules().isEmpty()) {
				throw new BusinessException().addPopup(errorMessage("error.cant_modify_service_rules_exist"));
			}
			entity.setServiceName(data.getServiceName());

			InnerBcDescription bcDescription = bcRegistry.select(InnerBcDescription.class)
					.distinct()
					.filter(innerBc -> innerBc != null && innerBc.getServiceClass().getSimpleName()
							.equals(data.getServiceName()))
					.findFirst().orElse(null);

			if (bcDescription != null && ResponseService.class.isAssignableFrom(bcDescription.getServiceClass())) {
				entity.setDtoClass(innerBcTypeAware.getTypeOfDto(bcDescription).getName());
			}
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

}
