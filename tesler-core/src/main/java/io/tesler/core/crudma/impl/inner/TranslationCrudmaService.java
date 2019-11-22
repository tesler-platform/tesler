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

package io.tesler.core.crudma.impl.inner;

import io.tesler.api.data.dto.UniversalDTO;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.model.core.api.Translatable;
import io.tesler.model.core.api.Translation;
import io.tesler.model.core.api.TranslationService;
import io.tesler.model.core.entity.BaseEntity;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TranslationCrudmaService extends UniversalCrudmaService<UniversalDTO, Translation> {

	@Autowired
	private BcRegistry bcRegistry;

	@Autowired
	private TranslationService translationService;

	@Override
	protected Class<UniversalDTO> getDtoClass() {
		return UniversalDTO.class;
	}

	@Override
	public CreateResult<UniversalDTO> create(BusinessComponent bc) {
		return doCreate(bc);
	}

	private <L extends Translation<E, L>, E extends BaseEntity & Translatable<E, L>> CreateResult<UniversalDTO> doCreate(
			BusinessComponent bc) {
		E parent = getParentEntity(bc);
		List<L> created = translationService.populate(parent);
		return new CreateResult<>(entityToDto(
				created.get(0),
				getDtoClass(),
				extractNames(getAttributes(bc))
		)).setAction(PostAction.refreshBc(bc));
	}

	@Override
	protected UniversalDTO entityToDto(Translation entity, Class<? extends UniversalDTO> dtoClass,
			Set<String> attributes) {
		UniversalDTO result = super.entityToDto(entity, dtoClass, attributes);
		result.setId(entity.getLanguage());
		return result;
	}

	@Override
	protected Translation getEntity(BusinessComponent bc) {
		Translatable<?, ?> parent = getParentEntity(bc);
		return parent.getTranslation(bc.getId(), null).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	protected Collection<? extends Translation> getEntities(BusinessComponent bc) {
		Translatable<?, ?> parent = getParentEntity(bc);
		return parent.getTranslations().values();
	}

	@Override
	public long count(BusinessComponent bc) {
		return getEntities(bc).size();
	}

	@Override
	protected Class<? extends Translation> getEntityClass(BusinessComponent bc) {
		return getParentEntity(bc).getTranslationType();
	}

	@Override
	protected List<Attribute<?, ?>> getAttributes(BusinessComponent bc) {
		return super.getAttributes(getEntityClass(bc), a -> String.class.isAssignableFrom(a.getJavaType()));
	}

	@SuppressWarnings("unchecked")
	protected <E extends BaseEntity & Translatable<?, ? extends Translation<?, ?>>> E getParentEntity(
			BusinessComponent bc) {
		InnerBcDescription bcDescription = (InnerBcDescription) bcRegistry.getBcDescription(bc.getParentName());
		Class<? extends BaseEntity> entityClass = responseFactory.getEntityFromService(bcDescription);
		return (E) jpaDao.findById(entityClass, bc.getParentIdAsLong());
	}

	@Override
	protected boolean isSaveAvailable(BusinessComponent bc) {
		return true;
	}

	@Override
	protected boolean isCreateAvailable(BusinessComponent bc) {
		return !translationService.getMissingTranslations(getParentEntity(bc)).isEmpty();
	}

}
