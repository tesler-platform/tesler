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

package io.tesler.core.controller;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.crudma.CrudmaActionHolder;
import io.tesler.core.crudma.CrudmaActionType;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.dto.data.CommentDTO;
import io.tesler.core.dto.data.FieldCommentDTO;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.FieldCommentService;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.core.util.session.SessionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class FieldCommentController {

	@Autowired
	private FieldCommentService fieldCommentService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private ResponseBuilder resp;

	@Autowired
	private CrudmaActionHolder crudmaActionHolder;

	@RequestMapping(method = RequestMethod.GET, value = "/comments")
	public ResponseDTO getViewComments(@RequestParam(value = "bc") String bc) {
		if (bc == null) {
			throw new ClientException(errorMessage("error.missing_parameter", "bc"));
		}
		ArrayList<String> bcNames = new ArrayList<>(Arrays.asList(bc.split(",")));
		List<FieldCommentDTO> result = fieldCommentService.getList(bcNames, null);
		return resp.build(result);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/comments/new")
	public DeferredResult<List<FieldCommentDTO>> checkNewComments(@RequestParam(value = "bc") String bc) {
		if (bc == null) {
			throw new ClientException(errorMessage("error.missing_parameter", "bc"));
		}
		ArrayList<String> bcNames = new ArrayList<>(Arrays.asList(bc.split(",")));
		return fieldCommentService.addTaskInQueue(sessionService.getSessionUser().getId(), bcNames,
				DateTimeUtil.now()
		);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/comments/{bcName}/{rowId}/{fieldName}")
	public ResponseDTO getFieldComments(@PathVariable String bcName, @PathVariable String rowId,
			@PathVariable String fieldName) {
		return resp.build(fieldCommentService.getList(bcName, rowId, fieldName));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/comments/{bcName}/{rowId}/{fieldName}")
	public ResponseDTO addNewComment(@PathVariable String bcName, @PathVariable String rowId,
			@PathVariable String fieldName, @RequestBody CommentDTO dto) {
		if (dto == null || StringUtils.isBlank(dto.getData())) {
			throw new BusinessException().addPopup(errorMessage("error.empty_comment"));
		}
		BusinessComponent bc = bc(null, null);
		crudmaActionHolder.of(CrudmaActionType.CREATE).setBc(bc);
		return resp.build(
				fieldCommentService.createEntity(
						bc,
						bcName,
						rowId,
						fieldName,
						dto
				)
		);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/comments/{parentId}")
	public ResponseDTO replyToComment(@PathVariable Long parentId, @RequestBody CommentDTO dto) {
		if (dto == null || StringUtils.isBlank(dto.getData())) {
			throw new BusinessException().addPopup(errorMessage("error.empty_comment"));
		}
		FieldCommentDTO comment = fieldCommentService.getOne(bc(null, parentId));
		if (comment == null) {
			throw new BusinessException().addPopup(errorMessage("error.comment_not_found"));
		}
		BusinessComponent bc = bc(parentId, null);
		crudmaActionHolder.of(CrudmaActionType.CREATE).setBc(bc);
		return resp.build(
				fieldCommentService.createEntity(
						bc,
						comment.getBcName(),
						comment.getRowId(),
						comment.getFieldName(),
						dto
				)
		);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/comments/{id}")
	public ResponseDTO edit(@PathVariable Long id, @RequestBody CommentDTO dto) {
		if (dto == null || StringUtils.isBlank(dto.getData())) {
			throw new ClientException("Запрос должен содержать комментарий в поле {\"data\":{}}");
		}
		BusinessComponent bc = bc(null, id);
		crudmaActionHolder.of(CrudmaActionType.UPDATE).setBc(bc);
		return resp.build(fieldCommentService.updateEntity(bc, dto));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/comments/{id}")
	public ResponseDTO delete(@PathVariable Long id) {
		FieldCommentDTO comment = fieldCommentService.getOne(bc(null, id));
		if (comment == null) {
			throw new BusinessException().addPopup(errorMessage("error.comment_not_found"));
		}
		if (!Objects.equals(comment.getUserId(), sessionService.getSessionUser().getId())) {
			throw new BusinessException().addPopup(errorMessage("error.not_enough_permissions"));
		}
		BusinessComponent bc = bc(null, id);
		crudmaActionHolder.of(CrudmaActionType.DELETE).setBc(bc);
		return resp.build(fieldCommentService.deleteEntity(bc));
	}

	private BusinessComponent bc(Long parentId, Long id) {
		return new BusinessComponent(
				id == null ? null : id.toString(),
				parentId == null ? null : parentId.toString(),
				new InnerBcDescription(
						"fieldComments",
						null,
						FieldCommentService.class,
						true
				)
		);
	}

}
