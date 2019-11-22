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
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.AssociateResultDTO;
import io.tesler.core.service.action.Actions;
import io.tesler.vanilla.dto.VanillaRelatedRecordsDTO;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord;
import io.tesler.vanilla.entity.VanillaTaskRelatedRecord_;
import io.tesler.vanilla.service.data.VanillaRelatedRecordsService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

@Service
public class VanillaRelatedRecordsServiceImpl extends
		AbstractResponseService<VanillaRelatedRecordsDTO, VanillaTaskRelatedRecord> implements
		VanillaRelatedRecordsService {

	public VanillaRelatedRecordsServiceImpl() {
		super(VanillaRelatedRecordsDTO.class, VanillaTaskRelatedRecord.class, null, null);
	}

	@Override
	public ResultPage<VanillaRelatedRecordsDTO> getList(BusinessComponent bc) {
		QueryParameters params = bc.getParameters();
		List<VanillaRelatedRecordsDTO> result = new ArrayList<>();
		ResultPage<VanillaTaskRelatedRecord> entities = baseDAO.getList(
				VanillaTaskRelatedRecord.class,
				VanillaRelatedRecordsDTO.class,
				Specifications.where(
						(root, cq, cb) -> cb.equal(root.get(VanillaTaskRelatedRecord_.parTaskId), bc.getParentIdAsLong())
				),
				params
		);
		for (VanillaTaskRelatedRecord rel : entities.getResult()) {
			if ("Задача".equals(rel.getChildType())) {
				result.add(entityToDto(bc, rel));
			}
		}
		return new ResultPage<>(result, entities.isHasNext());
	}

	@Override
	protected VanillaRelatedRecordsDTO entityToDto(BusinessComponent bc, VanillaTaskRelatedRecord entity) {
		VanillaRelatedRecordsDTO result = super.entityToDto(bc, entity);
		VanillaTask task = baseDAO.findById(VanillaTask.class, entity.getChildId());
		result.setName(task.getName());
		return result;
	}

	@Override
	protected Specification<VanillaTaskRelatedRecord> getParentSpecification(BusinessComponent bc) {
		return (root, cq, cb) -> {
			if (parentSpec != null) {
				return cb.equal(root.get(VanillaTaskRelatedRecord_.parTaskId), bc.getParentIdAsLong());
			} else {
				return cb.and();
			}
		};
	}

	public ActionResultDTO<VanillaRelatedRecordsDTO> deleteEntity(BusinessComponent businessComponent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Actions<VanillaRelatedRecordsDTO> getActions() {
		return Actions.<VanillaRelatedRecordsDTO>builder()
				.associate().add()
				.build();
	}

	@Override
	protected AssociateResultDTO doAssociate(List<AssociateDTO> data, BusinessComponent bc) {
		for (AssociateDTO dto : data) {
			if (dto.getAssociated()) {

				Specification<VanillaTaskRelatedRecord> specification = (root, cq, cb) ->
						cb.and(
								cb.equal(root.get(VanillaTaskRelatedRecord_.parTaskId), Long.parseLong(dto.getId())),
								cb.equal(root.get(VanillaTaskRelatedRecord_.childId), bc.getParentIdAsLong()),
								cb.equal(root.get(VanillaTaskRelatedRecord_.childType), "Задача")
						);
				if (baseDAO.getList(VanillaTaskRelatedRecord.class, specification).isEmpty()) {
					VanillaTaskRelatedRecord relRec = new VanillaTaskRelatedRecord();
					relRec.setChildId(Long.valueOf(dto.getId()));
					relRec.setParTaskId(bc.getParentIdAsLong());
					relRec.setChildType("Задача");
					baseDAO.save(relRec);
				}
			} else {
				Specification<VanillaTaskRelatedRecord> specification = (root, cq, cb) ->
						cb.and(
								cb.equal(root.get(VanillaTaskRelatedRecord_.parTaskId), bc.getParentIdAsLong()),
								cb.equal(root.get(VanillaTaskRelatedRecord_.childId), Long.parseLong(dto.getId())),
								cb.equal(root.get(VanillaTaskRelatedRecord_.childType), "Задача")
						);
				List<VanillaTaskRelatedRecord> relRec = baseDAO.getList(VanillaTaskRelatedRecord.class, specification);
				if (!relRec.isEmpty()) {
					relRec.forEach(rel -> baseDAO.delete(rel));
				}
			}
		}
		return new AssociateResultDTO(Collections.emptyList());
	}

}
