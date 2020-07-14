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

package io.tesler.core.dto.data;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.DateValueProvider;
import io.tesler.model.ui.entity.FieldComment;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldCommentDTO extends DataResponseDTO {

	private String bcName;

	private String fieldName;

	private Long parentId;

	private Long userId;

	private String firstName;

	private String patronymic;

	private String lastName;

	private String login;

	private String rowId;

	private String content;

	private String fullName;

	@SearchParameter(provider = DateValueProvider.class, strict = true)
	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;

	public FieldCommentDTO(FieldComment entity) {
		id = entity.getId().toString();
		bcName = entity.getBc();
		parentId = entity.getParentId();
		rowId = entity.getRowId();
		content = entity.getContent();
		fieldName = entity.getFieldName();
		userId = entity.getUserId();
		createdDate = entity.getCreatedDate();
		updatedDate = entity.getUpdatedDate();
	}

}
