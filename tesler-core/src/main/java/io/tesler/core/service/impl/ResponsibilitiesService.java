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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.Department;
import io.tesler.model.core.entity.Responsibilities;
import io.tesler.model.core.entity.Responsibilities_;
import io.tesler.model.core.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponsibilitiesService {

	private final JpaDao jpaDao;

	private final ObjectMapper objectMapper;

	private List<Responsibilities> getListByUserList(User user, LOV userRole, String responsibilityType) {
		// В листе может быть не более одной записи
		return jpaDao.getList(
				Responsibilities.class,
				(root, cq, cb) -> cb.and(
						cb.equal(root.get(Responsibilities_.departmentId), user.getDepartment().getId()),
						cb.equal(root.get(Responsibilities_.internalRoleCD), userRole),
						cb.equal(root.get(Responsibilities_.responsibilityType), responsibilityType)
				)
		);
	}

	public Map<String, Boolean> getListRespByUser(User user, LOV userRole) {
		return getListByUserList(user, userRole, "VIEW")
				.stream()
				.collect(
						Collectors.toMap(
								Responsibilities::getView,
								Responsibilities::isReadOnly,
								(x1, x2) -> x2
						)
				);
	}

	public String getListScreensByUser(User user, LOV userRole) {
		return getListByUserList(user, userRole, "SCREEN")
				.stream()
				.map(Responsibilities::getScreens)
				.filter(StringUtils::isNotBlank)
				.findFirst()
				.orElse(null);
	}

	public Set<String> getViewResponsibilities(final Department department) {
		return new HashSet<>(
				jpaDao.getList(
						Responsibilities.class,
						String.class,
						(root, cb) -> root.get(Responsibilities_.view),
						(root, cq, cb) -> cb.and(
								cb.equal(root.get(Responsibilities_.departmentId), department.getId()),
								cb.equal(root.get(Responsibilities_.responsibilityType), "VIEW")
						)
				)
		);
	}

}
