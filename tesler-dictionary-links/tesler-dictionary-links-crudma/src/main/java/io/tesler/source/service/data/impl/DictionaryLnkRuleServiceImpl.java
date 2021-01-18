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

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.DTOUtils;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.action.Actions;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond_;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue_;
import io.tesler.source.dto.DictionaryLnkRuleDto;
import io.tesler.source.dto.DictionaryLnkRuleDto_;
import io.tesler.source.service.data.DictionaryLnkRuleService;
import io.tesler.source.service.meta.DictionaryLnkRuleFieldMetaBuilder;
import io.tesler.source.service.specification.DictionaryLnkRuleLinkSpecifications;
import org.reflections.ReflectionUtils;
import org.springframework.stereotype.Service;

@Service
public class DictionaryLnkRuleServiceImpl extends
		VersionAwareResponseService<DictionaryLnkRuleDto, DictionaryLnkRule> implements DictionaryLnkRuleService {

	public DictionaryLnkRuleServiceImpl() {
		super(DictionaryLnkRuleDto.class, DictionaryLnkRule.class, null, DictionaryLnkRuleFieldMetaBuilder.class);
		this.linkSpecificationHolder = DictionaryLnkRuleLinkSpecifications.class;
	}

	@Override
	protected CreateResult<DictionaryLnkRuleDto> doCreateEntity(final DictionaryLnkRule entity,
			final BusinessComponent bc) {
		entity.setService(baseDAO.findById(CustomizableResponseService.class, bc.getParentIdAsLong()));
		Long id = baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, baseDAO.findById(DictionaryLnkRule.class, id)));
	}

	@Override
	protected ActionResultDTO<DictionaryLnkRuleDto> doUpdateEntity(DictionaryLnkRule entity, DictionaryLnkRuleDto data,
			BusinessComponent bc) {
		boolean needChildDeletion = false;
		boolean isSqlService = "SqlCrudmaService".equals(entity.getService().getServiceName());
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(DictionaryLnkRuleDto_.name)) {
				entity.setName(data.getName());
			}
			if (data.isFieldChanged(DictionaryLnkRuleDto_.filterableField)) {
				entity.setFilterableField(data.getFilterableField());
			}
			if (data.isFieldChanged(DictionaryLnkRuleDto_.allValues)) {
				entity.setAllValues(data.getAllValues());
				if (entity.isAllValues()) {
					needChildDeletion = true;
				}
			}
			if (data.isFieldChanged(DictionaryLnkRuleDto_.field)) {
				if (!entity.getValues().isEmpty()) {
					throw new BusinessException()
							.addPopup(errorMessage("error.cant_modify_rule_field_values_exist"));
				}
				entity.setField(data.getField());
				Class<?> dtoClass = ReflectionUtils.forName(entity.getService().getDtoClass());
				if (!isSqlService) {
					entity.setType(DTOUtils.getDictionaryType(dtoClass, data.getField()));
				}
			}
			if (data.isFieldChanged(DictionaryLnkRuleDto_.defaultRuleFlg)) {
				entity.setDefaultRuleFlg(data.getDefaultRuleFlg());
				if (entity.isDefaultRuleFlg()) {
					needChildDeletion = true;
				}
			}
			if (data.isFieldChanged(DictionaryLnkRuleDto_.type) && isSqlService) {
				entity.setType(data.getType());
			}

			if (needChildDeletion) {
				baseDAO.getList(DictionaryLnkRuleCond.class, (root, cq, cb) ->
						cb.equal(root.get(DictionaryLnkRuleCond_.dictionaryLnkRule), entity)
				).forEach(baseDAO::delete);
			}
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public ActionResultDTO<DictionaryLnkRuleDto> deleteEntity(BusinessComponent bc) {
		DictionaryLnkRule entity = baseDAO.findById(DictionaryLnkRule.class, bc.getIdAsLong());
		baseDAO.getList(DictionaryLnkRuleValue.class, (root, cq, cb) ->
				cb.equal(root.get(DictionaryLnkRuleValue_.dictionaryLnkRule), entity)
		).forEach(baseDAO::delete);
		baseDAO.getList(DictionaryLnkRuleCond.class, (root, cq, cb) ->
				cb.equal(root.get(DictionaryLnkRuleCond_.dictionaryLnkRule), entity)
		).forEach(baseDAO::delete);
		baseDAO.delete(entity);
		return new ActionResultDTO<>();
	}

	@Override
	public Actions<DictionaryLnkRuleDto> getActions() {
		return Actions.<DictionaryLnkRuleDto>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
