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
import io.tesler.vanilla.dto.VanillaRelatedRecordsAssociateDTO;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord_;
import io.tesler.vanilla.entity.VanillaTask_;
import io.tesler.vanilla.service.data.VanillaRelatedRecordsAssocService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class VanillaRelatedRecordsAssocServiceImpl extends
		AbstractResponseService<VanillaRelatedRecordsAssociateDTO, VanillaTask> implements
		VanillaRelatedRecordsAssocService {

	public VanillaRelatedRecordsAssocServiceImpl() {
		super(VanillaRelatedRecordsAssociateDTO.class, VanillaTask.class, null, null);
	}

	// TODO implemente sort and filter
	@Override
	public ResultPage<VanillaRelatedRecordsAssociateDTO> getList(BusinessComponent bc) {
		QueryParameters params = bc.getParameters();
		ResultPage<VanillaTask> tasks = entityListToResultPage(
				findTasksExceptCurrent(bc.getParentIdAsLong()),
				params.getPageSize()
		);
		List<VanillaRelatedRecordsAssociateDTO> result = tasks.getResult().stream()
				.map(task -> {
					VanillaRelatedRecordsAssociateDTO dto = new VanillaRelatedRecordsAssociateDTO();
					dto.setId(task.getId().toString());
					dto.setName(task.getName());
					dto.setType("Задача");
					Specification<VanillaTaskRelatedRecord> searchSpec = (root, cq, cb) -> cb.and(
							cb.equal(root.get(VanillaTaskRelatedRecord_.parTaskId), bc.getParentIdAsLong()),
							cb.equal(root.get(VanillaTaskRelatedRecord_.childId), task.getId()),
							cb.equal(root.get(VanillaTaskRelatedRecord_.childType), "Задача")
					);
					if (!baseDAO.getList(VanillaTaskRelatedRecord.class, searchSpec).isEmpty()) {
						dto.set_associate(true);
					} else {
						dto.set_associate(false);
					}
					return dto;
				})
				.collect(Collectors.toList());
		return new ResultPage<>(result, tasks.isHasNext());
	}

	@Override
	public ActionResultDTO<VanillaRelatedRecordsAssociateDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

	private List<VanillaTask> findTasksExceptCurrent(Long taskId) {
		return baseDAO.getList(VanillaTask.class, (root, query, cb) -> {
			query.orderBy(cb.desc(root.get(VanillaTask_.planDate)), cb.desc(root.get(VanillaTask_.createDate)));
			return cb.and(
					cb.notEqual(root.get(VanillaTask_.id), taskId)
			);
		});
	}

}
