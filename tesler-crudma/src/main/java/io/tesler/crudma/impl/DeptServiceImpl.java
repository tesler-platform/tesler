/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.crudma.api.DeptService;
import io.tesler.crudma.dto.DepartmentDTO;
import io.tesler.crudma.meta.DeptFieldMetaBuilder;
import io.tesler.model.core.entity.Department;
import org.springframework.stereotype.Service;

@Service
public class DeptServiceImpl extends AbstractResponseService<DepartmentDTO, Department> implements DeptService {

	public DeptServiceImpl() {
		super(DepartmentDTO.class, Department.class, null, DeptFieldMetaBuilder.class);
	}

	@Override
	public ActionResultDTO<DepartmentDTO> deleteEntity(BusinessComponent<InnerBcDescription> businessComponent) {
		throw new UnsupportedOperationException();
	}

}
