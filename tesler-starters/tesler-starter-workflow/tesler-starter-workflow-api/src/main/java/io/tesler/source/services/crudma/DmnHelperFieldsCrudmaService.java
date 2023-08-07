/*-
 * #%L
 * IO Tesler - Workflow API
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

package io.tesler.source.services.crudma;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.impl.AbstractCrudmaService;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.util.ListPaging;
import io.tesler.source.dto.DmnTaskFieldsDto;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DmnHelperFieldsCrudmaService extends AbstractCrudmaService<ExtremeBcDescription> {

	private static final List<DmnTaskFieldsDto> HELPER_FIELDS = ImmutableList.<DmnTaskFieldsDto>builder()
			.add(new DmnTaskFieldsDto("1", "Сегодняшний день", "helper.today", "date"))
			.build();

	private static final List<FieldDTO> FIELD_DTO_LIST = ImmutableList.<FieldDTO>builder()
			.add(FieldDTO.disabledFilterableField("id"))
			.add(FieldDTO.disabledFilterableField("title"))
			.add(FieldDTO.disabledFilterableField("key"))
			.add(FieldDTO.disabledFilterableField("type"))
			.build();

	@Override
	public ResultPage<? extends DataResponseDTO> getAll(BusinessComponent<ExtremeBcDescription> bc) {
		return ListPaging.getResultPage(HELPER_FIELDS, bc.getParameters());
	}

	@Override
	public long count(BusinessComponent<ExtremeBcDescription> bc) {
		return HELPER_FIELDS.size();
	}

	@Override
	public MetaDTO getMeta(BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(FIELD_DTO_LIST);
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(Collections.emptyList());
	}

}
