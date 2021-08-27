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

package io.tesler.acl.converter;

import io.tesler.acl.entity.types.GroupRelationType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GroupRelationTypeConverter implements AttributeConverter<GroupRelationType, Integer> {

	public Integer convertToDatabaseColumn(GroupRelationType attribute) {
		return attribute != null
				? attribute.getIntValue()
				: GroupRelationType.MEMBER_USER.getIntValue();
	}

	public GroupRelationType convertToEntityAttribute(Integer dbData) {
		return GroupRelationType.of(dbData);
	}

}
