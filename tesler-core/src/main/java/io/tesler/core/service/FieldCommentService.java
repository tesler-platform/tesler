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

package io.tesler.core.service;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.data.CommentDTO;
import io.tesler.core.dto.data.FieldCommentDTO;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.model.ui.entity.FieldComment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.context.request.async.DeferredResult;


public interface FieldCommentService extends ResponseService<FieldCommentDTO, FieldComment> {

	List<FieldCommentDTO> getList(List<String> bcName, LocalDateTime timestamp);

	List<FieldCommentDTO> getList(String bcName, String rowId, String fieldName);

	ActionResultDTO<FieldCommentDTO> createEntity(BusinessComponent bc, String bcName, String rowId, String fieldName,
			CommentDTO comment);

	ActionResultDTO<FieldCommentDTO> updateEntity(BusinessComponent bc, CommentDTO comment);

	DeferredResult<List<FieldCommentDTO>> addTaskInQueue(Long userId, List<String> bcNames, LocalDateTime timestamp);

}
