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

package io.tesler.core.dto.rowmeta;

import io.tesler.core.crudma.MetaContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@EqualsAndHashCode
public class MetaDTO implements MetaContainer<MetaDTO> {

	private final RowMetaDTO row;

	private List<PostAction> postActions;

	public MetaDTO(RowMetaDTO row) {
		this.row = row;
	}

	@Override
	public void transformMeta(Function<MetaDTO, MetaDTO> function) {
		function.apply(this);
	}

}
