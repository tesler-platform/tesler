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

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.PreviewResult;
import io.tesler.api.exception.ServerException;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.AbstractCrudmaService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.ResponseService;
import io.tesler.core.service.rowmeta.RowMetaType;
import io.tesler.core.service.rowmeta.RowResponseService;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class InnerCrudmaService extends AbstractCrudmaService {

	@Autowired
	private ResponseFactory respFactory;

	@Lazy
	@Autowired
	private RowResponseService rowMeta;

	@Override
	public CreateResult create(BusinessComponent bc) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.createEntity(bc);
	}

	@Override
	public DataResponseDTO get(BusinessComponent bc) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.getOne(bc);
	}

	@Override
	public ResultPage<? extends DataResponseDTO> getAll(BusinessComponent bc) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.getList(bc);
	}

	@Override
	public PreviewResult preview(BusinessComponent bc, Map<String, Object> data) {
		final InnerBcDescription bcDescription = bc.getDescription();
		final ResponseService<?, ?> responseService = respFactory.getService(bcDescription);
		final DataResponseDTO requestDto = respFactory.getDTOFromMapIgnoreBusinessErrors(
				data, respFactory.getDTOFromService(bcDescription), bc
		);
		final DataResponseDTO responseDto = responseService.preview(bc, requestDto).getRecord();

		responseDto.setErrors(requestDto.getErrors());
		return new PreviewResult(requestDto, responseDto);
	}

	@Override
	public ActionResultDTO update(BusinessComponent bc, Map<String, Object> data) {
		final InnerBcDescription bcDescription = bc.getDescription();
		ResponseService<?, ?> responseService = respFactory.getService(bcDescription);
		DataResponseDTO requestDTO = respFactory.getDTOFromMap(data, respFactory.getDTOFromService(bcDescription), bc);
		responseService.validate(bc, requestDTO);
		return responseService.updateEntity(bc, requestDTO);
	}

	@Override
	public ActionResultDTO delete(BusinessComponent bc) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.deleteEntity(bc);
	}

	@Override
	public AssociateResultDTO associate(BusinessComponent bc, List<AssociateDTO> data) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.associate(data, bc);
	}

	@Override
	public ActionResultDTO invokeAction(BusinessComponent bc,
			String actionName,
			Map<String, Object> data) {
		final InnerBcDescription bcDescription = bc.getDescription();
		ResponseService<?, ?> responseService = respFactory.getService(bcDescription);
		DataResponseDTO requestDTO = respFactory.getDTOFromMap(data, respFactory.getDTOFromService(bcDescription), bc);
		return responseService.invokeAction(bc, actionName, requestDTO);
	}

	@Override
	public MetaDTO getMetaNew(BusinessComponent bc, CreateResult createResult) {
		final InnerBcDescription bcDescription = bc.getDescription();
		ResponseService<?, ?> responseService = getResponseService(bcDescription);
		return rowMeta.getResponse(RowMetaType.META_NEW, createResult, bc, responseService);
	}

	@Override
	public MetaDTO getMeta(BusinessComponent bc) {
		final InnerBcDescription bcDescription = bc.getDescription();
		ResponseService<?, ?> service = respFactory.getService(bcDescription);
		try {
			return rowMeta.getResponse(RowMetaType.META, getDto(service, bc), bc, service);
		} catch (BusinessException e) {
			throw new BusinessException().addPopup(e.getMessage());
		} catch (Exception e) {
			throw new ServerException(e.getMessage(), e);
		}
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent bc) {
		final InnerBcDescription bcDescription = bc.getDescription();
		ResponseService<?, ?> service = respFactory.getService(bcDescription);
		try {
			return rowMeta.getResponse(RowMetaType.META_EMPTY, getDto(service, bc), bc, service);
		} catch (BusinessException e) {
			throw new BusinessException().addPopup(e.getMessage());
		} catch (Exception e) {
			throw new ServerException(e.getMessage(), e);
		}
	}

	@Override
	public MetaDTO getOnFieldUpdateMeta(BusinessComponent bc, DataResponseDTO dto) {
		final InnerBcDescription bcDescription = bc.getDescription();
		final ResponseService<?, ?> service = respFactory.getService(bcDescription);
		return rowMeta.getResponse(RowMetaType.ON_FIELD_UPDATE_META, dto, bc, service);
	}

	@Override
	public long count(BusinessComponent bc) {
		ResponseService<?, ?> responseService = getResponseService(bc.getDescription());
		return responseService.count(bc);
	}

	private ResponseService<?, ?> getResponseService(InnerBcDescription innerBcDescription) {
		return respFactory.getService(innerBcDescription);
	}

	@SneakyThrows
	private DataResponseDTO getDto(ResponseService<?, ?> service, BusinessComponent bc) {
		if (bc.getId() != null && service.hasPersister()) {
			return service.getOne(bc);
		}
		Class<?> dto = respFactory.getDTOFromService(bc.getDescription());
		return (DataResponseDTO) dto.getConstructor().newInstance();
	}

}
