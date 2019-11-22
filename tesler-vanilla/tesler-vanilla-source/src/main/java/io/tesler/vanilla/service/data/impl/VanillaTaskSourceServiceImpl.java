/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.data.impl;

import io.tesler.api.data.ResultPage;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.vanilla.dto.VanillaTaskSourceDTO;
import io.tesler.vanilla.entity.VanillaSourceDict;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.service.data.VanillaTaskSourceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VanillaTaskSourceServiceImpl extends
		AbstractResponseService<VanillaTaskSourceDTO, VanillaSourceDict> implements VanillaTaskSourceService {

	public VanillaTaskSourceServiceImpl() {
		super(VanillaTaskSourceDTO.class, VanillaSourceDict.class, null, null);
	}

	@Override
	public ResultPage<VanillaTaskSourceDTO> getList(BusinessComponent bc) {
		QueryParameters params = bc.getParameters();
		int limit = params.getPageSize();
		VanillaTask task = baseDAO.findById(VanillaTask.class, bc.getParentIdAsLong());
		List<VanillaSourceDict> sourceDicts = new ArrayList<>(task.getSourceDicts());
		return entitiesToDtos(bc, entityListToResultPage(sourceDicts, limit));
	}

	@Override
	public ActionResultDTO<VanillaTaskSourceDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

}
