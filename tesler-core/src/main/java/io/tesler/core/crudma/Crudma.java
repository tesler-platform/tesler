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
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.MetaDTO;
import java.util.List;
import java.util.Map;


public interface Crudma {

	DataResponseDTO get(BusinessComponent bc);

	ResultPage<? extends DataResponseDTO> getAll(BusinessComponent bc);

	CreateResult create(BusinessComponent bc);

	PreviewResult preview(BusinessComponent bc, Map<String, Object> data);

	ActionResultDTO update(BusinessComponent bc, Map<String, Object> data);

	ActionResultDTO delete(BusinessComponent bc);

	ActionResultDTO invokeAction(BusinessComponent bc, String actionName, Map<String, Object> data);

	AssociateResultDTO associate(BusinessComponent bc, List<AssociateDTO> data);

	MetaDTO getMetaNew(BusinessComponent bc, CreateResult data);

	MetaDTO getMeta(BusinessComponent bc);

	MetaDTO getMetaEmpty(BusinessComponent bc);

	MetaDTO getOnFieldUpdateMeta(BusinessComponent bc, DataResponseDTO dto);

	long count(BusinessComponent bc);

}
