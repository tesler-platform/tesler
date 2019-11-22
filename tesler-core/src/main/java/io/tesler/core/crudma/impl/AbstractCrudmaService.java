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

package io.tesler.core.crudma.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.api.data.dto.rowmeta.FieldsDTO;
import io.tesler.api.data.dto.rowmeta.PreviewResult;
import io.tesler.core.crudma.Crudma;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionsDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.dto.rowmeta.RowMetaDTO;
import java.util.List;
import java.util.Map;


public abstract class AbstractCrudmaService implements Crudma {


	@Override
	public DataResponseDTO get(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public ResultPage<? extends DataResponseDTO> getAll(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public CreateResult create(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public PreviewResult preview(BusinessComponent bc, Map<String, Object> data) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public ActionResultDTO update(BusinessComponent bc, Map<String, Object> data) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public ActionResultDTO delete(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public ActionResultDTO invokeAction(BusinessComponent bc,
			String actionName,
			Map<String, Object> data) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public AssociateResultDTO associate(BusinessComponent bc, List<AssociateDTO> data) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public MetaDTO getMetaNew(BusinessComponent bc, CreateResult data) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public MetaDTO getMeta(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public MetaDTO getOnFieldUpdateMeta(BusinessComponent bc, DataResponseDTO dto) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	@Override
	public long count(BusinessComponent bc) {
		throw new UnsupportedOperationException(errorMessage("error.unsupported_operation"));
	}

	protected MetaDTO buildMeta(List<FieldDTO> fields) {
		return buildMeta(fields, new ActionsDTO());
	}

	protected MetaDTO buildMeta(List<FieldDTO> fields, ActionsDTO actions) {
		return new MetaDTO(new RowMetaDTO(actions, FieldsDTO.of(fields)));
	}

}
