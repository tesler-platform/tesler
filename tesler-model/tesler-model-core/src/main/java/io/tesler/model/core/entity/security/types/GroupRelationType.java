/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.entity.security.types;

import static java.lang.Integer.parseInt;

import io.tesler.api.util.MapUtils;
import io.tesler.model.core.entity.security.GroupRelation;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The type of relationship between the group and other entities
 */
@Getter
@RequiredArgsConstructor
public enum GroupRelationType {

	/**
	 * user, direct member of the group
	 */
	MEMBER_USER(parseInt(Values.MEMBER_USER)),
	/**
	 * group, direct member of the group
	 */
	MEMBER_GROUP(parseInt(Values.MEMBER_GROUP)),
	/**
	 * SUPER_GROUP - ranking group
	 */
	SUPER_GROUP(parseInt(Values.SUPER_GROUP));

	private static final Map<Integer, GroupRelationType> ALL_TYPES = MapUtils
			.of(GroupRelationType.class, GroupRelationType::getIntValue);

	private final int intValue;


	public static GroupRelationType of(int intValue) {
		return ALL_TYPES.getOrDefault(intValue, MEMBER_USER);
	}

	public GroupRelation toRelation(Long id) {
		return new GroupRelation(this, id);
	}

	/**
	 * String constants to use as a discriminator
	 */
	public static class Values {

		public static final String MEMBER_USER = "0";

		public static final String MEMBER_GROUP = "1";

		public static final String SUPER_GROUP = "2";

	}

}
