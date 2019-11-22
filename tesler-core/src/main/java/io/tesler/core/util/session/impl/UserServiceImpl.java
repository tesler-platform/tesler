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

package io.tesler.core.util.session.impl;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.core.controller.UserController.UserDto;
import io.tesler.core.util.session.UserService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.User_;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final JpaDao jpaDao;

	@Override
	public ResultPage<UserDto> getByMention(String mention, PageSpecification page) {
		ResultPage<User> users = jpaDao.getPage(User.class, (root, cq, cb) -> {
			Predicate[] searchTokens = Stream.of(mention.split(" "))
					.filter(Objects::nonNull)
					.map(String::toLowerCase)
					.map(item -> cb.or(
							cb.like(cb.lower(root.get(User_.fullUserName)), "%" + item + "%"),
							cb.like(cb.lower(root.get(User_.email)), "%" + item + "%")
					))
					.toArray(Predicate[]::new);
			return cb.or(searchTokens);
		}, page);
		return ResultPage.of(users, this::entityToDto);
	}

	private UserDto entityToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId().toString());
		dto.setFio(user.getFullName());
		dto.setEmail(user.getEmail());
		dto.setPhone(user.getPhone());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setPatronymic(user.getPatronymic());
		dto.setLogin(user.getLogin());
		return dto;
	}

	@Override
	public User getUserByLogin(String login) {
		List<User> user = jpaDao.getList(User.class, (root, cq, cb) ->
				cb.equal(
						cb.upper(root.get(User_.login)),
						login.toUpperCase()
				)
		);
		return !user.isEmpty() ? user.listIterator().next() : null;
	}

}
