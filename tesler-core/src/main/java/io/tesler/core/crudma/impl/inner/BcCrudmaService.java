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
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.impl.AbstractCrudmaService;
import io.tesler.core.dto.data.BcDto;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.util.ListPaging;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BcCrudmaService extends AbstractCrudmaService<ExtremeBcDescription> {

	private final List<BcDto> bcList;

	private final List<FieldDTO> fieldDtoList = ImmutableList.<FieldDTO>builder()
			.add(FieldDTO.disabledFilterableField("id"))
			.add(FieldDTO.disabledFilterableField("name"))
			.build();

	public BcCrudmaService(final BcRegistry bcRegistry) {
		bcList = bcRegistry.getAllBcNames().stream().map(BcDto::new).collect(Collectors.toList());
	}

	@Override
	public ResultPage<BcDto> getAll(final BusinessComponent<ExtremeBcDescription> bc) {
		return ListPaging.getResultPage(bcList, bc.getParameters());
	}

	@Override
	public long count(final BusinessComponent<ExtremeBcDescription> bc) {
		return bcList.size();
	}

	@Override
	public MetaDTO getMeta(final BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(fieldDtoList);
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(Collections.emptyList());
	}

}
