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

import static io.tesler.api.data.dictionary.CoreDictionaries.DictionaryTermType.DICTIONARY_FIELD;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.crudma.impl.sql.SqlCrudmaService;
import io.tesler.core.dto.DTOUtils;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.Department;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.source.dto.DictionaryLnkRuleCondDto;
import io.tesler.source.dto.DictionaryLnkRuleCondDto_;
import javax.persistence.metamodel.SingularAttribute;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseDictionaryLnkRuleCondServiceImpl<D extends DictionaryLnkRuleCondDto, E extends DictionaryLnkRuleCond>
		extends VersionAwareResponseService<D, E> {

	@Autowired
	private DictionaryCache dictionaryCache;

	public BaseDictionaryLnkRuleCondServiceImpl(Class<D> typeOfDTO, Class<E> typeOfEntity,
			SingularAttribute<? super E, ? extends BaseEntity> parentSpec,
			Class<? extends FieldMetaBuilder<D>> metaBuilder) {
		super(typeOfDTO, typeOfEntity, parentSpec, metaBuilder);
	}

	@Override
	protected CreateResult<D> doCreateEntity(final E entity, final BusinessComponent bc) {
		entity.setDictionaryLnkRule(baseDAO.findById(DictionaryLnkRule.class, bc.getParentIdAsLong()));
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected final ActionResultDTO<D> doUpdateEntity(E entity, D data, BusinessComponent bc) {
		boolean isSqlService = SqlCrudmaService.class.getSimpleName()
				.equals(entity.getDictionaryLnkRule().getService().getServiceName());
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.type)) {
				entity.setType(DictionaryType.DICTIONARY_TERM_TYPE.lookupName(data.getType()));
				entity.setFieldName(null);
				entity.setDepartment(null);
				entity.setFieldTextValue(null);
				entity.setBcName(null);
				entity.setFieldDictValue(null);
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.ruleInversionFlg)) {
				entity.setRuleInversionFlg(data.isRuleInversionFlg());
			}
		}
		return doUpdateEntity(entity, data, isSqlService, bc);
	}


	protected ActionResultDTO<D> doUpdateEntity(E entity, D data, boolean isSqlService, BusinessComponent bc) {
		if (data.hasChangedFields()) {
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.fieldName)) {
				entity.setFieldName(data.getFieldName());
				Class<?> dtoClass = ReflectionUtils.forName(entity.getDictionaryLnkRule().getService().getDtoClass());
				if (DICTIONARY_FIELD.equals(entity.getType()) && !isSqlService) {
					entity.setFieldType(DTOUtils.getDictionaryType(dtoClass, data.getFieldName()));
				}
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.bcName)) {
				entity.setBcName(data.getBcName());
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.departmentId)) {
				entity.setDepartment(data.getDepartmentId() == null ? null
						: baseDAO.findById(Department.class, Long.valueOf(data.getDepartmentId())));
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.fieldTextValue)) {
				entity.setFieldTextValue(data.getFieldTextValue());
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.fieldDictValue)) {
				entity.setFieldDictValue(
						entity.getFieldType() == null ? null
								: dictionaryCache.lookupName(data.getFieldDictValue(), entity.getFieldType())
				);
			}
			if (data.isFieldChanged(DictionaryLnkRuleCondDto_.fieldType) && isSqlService) {
				entity.setFieldType(data.getFieldType());
				entity.setFieldDictValue(null);
			}
		}
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

	@Override
	public Actions<D> getActions() {
		return Actions.<D>builder()
				.create().add()
				.save().add()
				.delete().add()
				.build();
	}

}
