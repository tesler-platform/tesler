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

package io.tesler.core.service;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionsDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.core.service.rowmeta.RowMetaType;
import io.tesler.core.util.session.CreationState;
import io.tesler.model.core.entity.BaseEntity;
import java.util.List;


public interface ResponseService<T extends DataResponseDTO, E extends BaseEntity> {

	BaseEntity getOneAsEntity(BusinessComponent bc);

	T getOne(BusinessComponent bc);

	boolean hasPersister();

	ResultPage<T> getList(BusinessComponent bc);

	CreateResult<T> createEntity(BusinessComponent bc, CreationState creationState);

	ActionResultDTO<T> updateEntity(BusinessComponent bc, DataResponseDTO data);

	ActionResultDTO<T> preview(BusinessComponent bc, DataResponseDTO data);

	ActionResultDTO<T> deleteEntity(BusinessComponent bc);

	ActionResultDTO<T> invokeAction(BusinessComponent bc, String actionName, DataResponseDTO data);

	AssociateResultDTO associate(List<AssociateDTO> data, BusinessComponent bc);

	ActionsDTO getAvailableActions(RowMetaType metaType, DataResponseDTO data, BusinessComponent bc);

	ActionResultDTO onCancel(BusinessComponent bc);

	Class<? extends FieldMetaBuilder<T>> getFieldMetaBuilder();

	long count(BusinessComponent bc);

	Class<T> getTypeOfDTO();

	Class<E> getTypeOfEntity();

	void validate(BusinessComponent bc, DataResponseDTO data);

	<V> V unwrap(Class<V> cls);

	/**
	 * Определяет поддерживается ли отложенное сохранение новых объектов или нет
	 *
	 * @param bc БК
	 */
	boolean isDeferredCreationSupported(BusinessComponent bc);

}
