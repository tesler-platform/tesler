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
import io.tesler.vanilla.dto.VanillaTaskDTO;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord;
import io.tesler.vanilla.entity.VanillaTask_;
import io.tesler.vanilla.service.data.VanillaTaskParentService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

@Service
public class VanillaTaskParentServiceImpl extends AbstractResponseService<VanillaTaskDTO, VanillaTask> implements
		VanillaTaskParentService {

	public VanillaTaskParentServiceImpl() {
		super(VanillaTaskDTO.class, VanillaTask.class, null, null);
	}

	@Override
	public ResultPage<VanillaTaskDTO> getList(BusinessComponent bc) {
		QueryParameters params = bc.getParameters();

		VanillaTaskRelatedRecord vanillaTaskRelatedRecord = baseDAO
				.findById(VanillaTaskRelatedRecord.class, bc.getParentIdAsLong());

		ResultPage<VanillaTask> entities = baseDAO.getList(
				VanillaTask.class,
				VanillaTaskDTO.class,
				Specifications.where((root, cq, cb) -> cb.equal(
						root.get(VanillaTask_.id),
						vanillaTaskRelatedRecord.getChildId()
				)),
				params
		);

		List<VanillaTaskDTO> result = new ArrayList<>();
		entities.getResult().forEach(vanillaTask -> result.add(new VanillaTaskDTO(vanillaTask)));

		return new ResultPage<>(result, entities.isHasNext());
	}

	@Override
	public ActionResultDTO<VanillaTaskDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

}
