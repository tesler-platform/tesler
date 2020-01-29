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

package io.tesler.core.service.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.dictionary.CoreDictionaries.DatabaseEvent;
import io.tesler.api.data.dictionary.CoreDictionaries.NotificationRecipient;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractResponseService;
import io.tesler.core.dto.data.CommentDTO;
import io.tesler.core.dto.data.FieldCommentDTO;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.service.DTOMapper;
import io.tesler.core.service.FieldCommentService;
import io.tesler.core.service.FieldCommentsDeferredResult;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.api.notifications.INotificationEventBuilder;
import io.tesler.model.core.entity.User;
import io.tesler.model.ui.entity.FieldComment;
import io.tesler.model.ui.entity.FieldComment_;
import io.tesler.model.ui.listeners.hbn.change.notifications.FieldCommentEventGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class FieldCommentServiceImpl extends AbstractResponseService<FieldCommentDTO, FieldComment>
		implements FieldCommentService {

	@Autowired
	private SessionService sessionService;

	@PersistenceContext(unitName = "teslerEntityManagerFactory")
	private EntityManager entityManager;

	@Autowired
	private FieldCommentsPollingService fieldCommentsPollingService;

	@Autowired
	private FieldCommentEventGenerator eventGenerator;

	@Autowired
	private DTOMapper dtoMapper;

	protected FieldCommentServiceImpl() {
		super(FieldCommentDTO.class, FieldComment.class, null, null);
	}

	@Override
	public DeferredResult<List<FieldCommentDTO>> addTaskInQueue(Long userId, List<String> bcNames,
			LocalDateTime timestamp) {
		FieldCommentsDeferredResult result = new FieldCommentsDeferredResult(userId, bcNames, timestamp);
		fieldCommentsPollingService.addToQueue(result);
		return result;
	}

	@Override
	protected FieldCommentDTO entityToDto(BusinessComponent bc, FieldComment comment) {
		return dtoMapper.entityToDto(comment, FieldCommentDTO.class);
	}

	@Override
	public List<FieldCommentDTO> getList(List<String> bcName, LocalDateTime timestamp) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FieldComment> cq = cb.createQuery(FieldComment.class);
		Root<FieldComment> root = cq.from(FieldComment.class);
		Expression<String> exp = root.get(FieldComment_.bc);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(exp.in(bcName));
		if (timestamp != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get(FieldComment_.createdDate), timestamp));
		}
		cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		List<FieldComment> results = entityManager.createQuery(cq).getResultList();
		return results.stream().map(FieldCommentDTO::new).peek(
				fieldCommentDTO -> {
					User user = baseDAO.findById(User.class, fieldCommentDTO.getUserId());
					fieldCommentDTO.setFirstName(user.getFirstName());
					fieldCommentDTO.setPatronymic(user.getPatronymic());
					fieldCommentDTO.setLastName(user.getLastName());
					fieldCommentDTO.setLogin(user.getLogin());
					fieldCommentDTO.setFullName(user.getFullName());
				}
		).collect(Collectors.toList());
	}

	@Override
	public List<FieldCommentDTO> getList(String bcName, String rowId, String fieldName) {
		List<FieldComment> data = baseDAO.getList(
				FieldComment.class,
				(root, cq, cb) -> cb.and(
						cb.equal(root.get(FieldComment_.bc), bcName),
						cb.equal(root.get(FieldComment_.rowId), rowId),
						cb.equal(root.get(FieldComment_.fieldName), fieldName)
				)
		);
		return data.stream().map(FieldCommentDTO::new).collect(Collectors.toList());
	}

	@Override
	public ActionResultDTO<FieldCommentDTO> createEntity(BusinessComponent bc, String bcName, String rowId,
			String fieldName,
			CommentDTO dto) {
		FieldComment comment = FieldComment.builder().userId(sessionService.getSessionUser().getId()).parentId(bc.getParentIdAsLong())
				.bc(bcName).rowId(rowId).fieldName(fieldName).content(dto.getData()).build();
		baseDAO.save(comment);

		if (bc.getParentIdAsLong() != null) {
			FieldComment parent = baseDAO.findById(FieldComment.class, bc.getParentIdAsLong());
			User author = baseDAO.findById(User.class, parent.getUserId());
			eventGenerator.builder(comment, DatabaseEvent.COMMENT_ANSWERED)
					.addRecipient(NotificationRecipient.COMMENT_AUTHOR, author)
					.setPerformer(sessionService.getSessionUser())
					.addModel("url", dto.getUrl())
					.publish();
		}
		notifyMentionedUsers(dto, comment, DatabaseEvent.COMMENT_MENTION_CREATED);
		return new ActionResultDTO<>(entityToDto(bc, comment));
	}

	@Override
	public ActionResultDTO<FieldCommentDTO> updateEntity(BusinessComponent bc, CommentDTO dto) {
		FieldComment comment = baseDAO.findById(FieldComment.class, bc.getIdAsLong());
		if (comment == null) {
			throw new BusinessException().addPopup(errorMessage("error.comment_not_found"));
		}
		if (!Objects.equals(comment.getUserId(), sessionService.getSessionUser().getId())) {
			throw new BusinessException().addPopup(errorMessage("error.not_enough_permissions"));
		}
		if (!Objects.equals(comment.getContent(), dto.getData())) {
			comment.setContent(dto.getData());
			notifyMentionedUsers(dto, comment, DatabaseEvent.COMMENT_MENTION_UPDATED);
		}
		comment.setContent(dto.getData());
		return new ActionResultDTO<>(entityToDto(bc, comment));
	}

	private void notifyMentionedUsers(CommentDTO dto, FieldComment comment, LOV event) {
		List<Long> mentions = dto.getMentions();
		if (mentions != null && !mentions.isEmpty()) {
			INotificationEventBuilder builder = eventGenerator.builder(comment, event)
					.setPerformer(sessionService.getSessionUser())
					.addModel("url", dto.getUrl());
			mentions.forEach(userId ->
					builder.addRecipient(NotificationRecipient.MENTIONED_USER, baseDAO.findById(User.class, userId))
			);
			builder.publish();
		}
	}

	@Override
	public ActionResultDTO<FieldCommentDTO> updateEntity(BusinessComponent bc, DataResponseDTO data) {
		Long id = bc.getIdAsLong();
		FieldComment comment = baseDAO.findById(FieldComment.class, id);
		FieldCommentDTO commentDTO = entityToDto(bc, comment);
		String newContent = ((FieldCommentDTO) data).getContent();
		if (!newContent.equals(commentDTO.getContent())) {
			comment.setContent(newContent);
		}
		return new ActionResultDTO<>(entityToDto(bc, comment));
	}

}
