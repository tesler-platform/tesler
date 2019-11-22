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

import io.tesler.api.data.dictionary.CoreDictionaries.UserActivityType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.dto.data.view.BrowseViewDto;
import io.tesler.core.dto.data.view.BrowseViewDto.PreviousBrowse;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.UserActivity;
import io.tesler.model.core.entity.UserActivity_;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserActivityLogger {

	private final JpaDao jpaDao;

	public void login(final Long userId) {
		save(userId, UserActivityType.LOGIN, null, null);
	}

	public Long browseView(final User user, final BrowseViewDto data) {
		final PreviousBrowse previousBrowse = data.getPreviousBrowse();
		if (previousBrowse != null) {
			update(user.getId(), UserActivityType.BROWSE_VIEW, previousBrowse.getId(), previousBrowse.getDuration());
		}
		final BrowseViewDto.View view = data.getView();
		if (view != null) {
			return save(user.getId(), UserActivityType.BROWSE_VIEW, view.getName(), view.getUrl());
		}
		return null;
	}

	private Long save(final Long userId, final LOV type, final String viewName, final String url) {
		try {
			final UserActivity log = new UserActivity();
			log.setDate(DateTimeUtil.now());
			log.setType(type);
			log.setUser(jpaDao.findById(User.class, userId));
			log.setViewName(viewName);
			log.setUrl(url);
			return jpaDao.save(log);
		} catch (Exception e) {
			log.info(String.format("Ошибка при логировании активности %s пользователя %d", type.getKey(), userId), e);
			return null;
		}
	}

	private void update(final Long userId, final LOV type, final Long id, final Long duration) {
		try {
			Optional.ofNullable(jpaDao.getSingleResultOrNull(UserActivity.class, (root, query, cb) -> cb.and(
					cb.equal(root.get(UserActivity_.id), id),
					cb.equal(root.get(UserActivity_.user).get(BaseEntity_.id), userId),
					cb.equal(root.get(UserActivity_.type), type),
					cb.isNull(root.get(UserActivity_.duration))
			))).ifPresent(activity -> activity.setDuration(duration));
		} catch (Exception e) {
			log.info(String.format("Ошибка при логировании активности %s пользователя %d", type.getKey(), userId), e);
		}
	}

}
