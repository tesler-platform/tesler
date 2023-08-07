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

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.PreviewResult;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.MetaDTO;
import java.util.List;
import java.util.Map;


public interface Crudma<T extends BcDescription> {

	/**
	 * Returns object based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link DataResponseDTO} information about entity, it's changed fields, errors
	 */
	DataResponseDTO get(BusinessComponent<T> bc);

	/**
	 * Returns all matched objects based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link ResultPage} class with list of objects
	 */
	ResultPage<? extends DataResponseDTO> getAll(BusinessComponent<T> bc);

	/**
	 * Creates an entity based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link CreateResult} class with DataResponseDTO and postactions
	 */
	CreateResult create(BusinessComponent<T> bc);

	/**
	 * Updates an entity based on a business component by map
	 * Used in forceactive fields
	 *
	 * @param bc businessComponent
	 * @param data information about entity
	 * @return {@link PreviewResult} class with DataResponseDTO
	 */
	PreviewResult preview(BusinessComponent<T> bc, Map<String, Object> data);

	/**
	 * Updates an entity based on a business component by map
	 *
	 * @param bc businessComponent
	 * @param data information about entity
	 * @return {@link ActionResultDTO} class with DataResponseDTO and postactions
	 */
	ActionResultDTO update(BusinessComponent<T> bc, Map<String, Object> data);

	/**
	 * Deletes an entity based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link ActionResultDTO} class with DataResponseDTO and postactions
	 */
	ActionResultDTO delete(BusinessComponent<T> bc);

	/**
	 * Invokes action with given name, add preactions, loads or updates entity if necessary
	 *
	 * @param bc businessComponent
	 * @param actionName name of action
	 * @param data information about entity
	 * @return {@link ActionResultDTO} class with DataResponseDTO and postactions
	 */
	ActionResultDTO invokeAction(BusinessComponent<T> bc, String actionName, Map<String, Object> data);

	/**
	 * Creates links between entities
	 *
	 * @param data information about an entity, whether the entity was associated
	 * @param bc businessComponent
	 * @return {@link AssociateResultDTO} class with DataResponseDTO and postactions
	 */
	AssociateResultDTO associate(BusinessComponent<T> bc, List<AssociateDTO> data);

	/**
	 * Returns new meta for entity based on a business component
	 *
	 * @param data class with DataResponseDTO and postactions
	 * @param bc businessComponent
	 * @return {@link MetaDTO} class with meta DTO and postactions
	 */
	MetaDTO getMetaNew(BusinessComponent<T> bc, CreateResult data);

	/**
	 * Returns meta for entity based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link MetaDTO} class with meta DTO and postactions
	 */
	MetaDTO getMeta(BusinessComponent<T> bc);

	/**
	 * Returns empty meta for entity based on a business component
	 *
	 * @param bc businessComponent
	 * @return {@link MetaDTO} class with meta DTO and postactions
	 */
	MetaDTO getMetaEmpty(BusinessComponent<T> bc);

	/**
	 * Returns on-field-update meta for entity based on a business component
	 *
	 * @param bc businessComponent
	 * @param dto information about entity, it's changed fields, errors
	 * @return {@link MetaDTO} class with meta DTO and postactions
	 */
	MetaDTO getOnFieldUpdateMeta(BusinessComponent<T> bc, DataResponseDTO dto);

	/**
	 * Returns the number of matching entities
	 *
	 * @param bc businessComponent
	 * @return count
	 */
	long count(BusinessComponent<T> bc);

}
