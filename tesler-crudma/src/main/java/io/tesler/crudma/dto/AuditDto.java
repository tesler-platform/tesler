/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.dto;

import static io.tesler.api.util.i18n.LocalizationFormatter.uiMessage;
import static org.hibernate.envers.RevisionType.ADD;
import static org.hibernate.envers.RevisionType.DEL;
import static org.hibernate.envers.RevisionType.MOD;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.exception.ServerException;
import io.tesler.model.core.entity.ExtRevisionEntity;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.envers.RevisionType;

@Getter
public class AuditDto extends DataResponseDTO {

	@JsonIgnore
	private static final Map<RevisionType, Supplier<String>> REVISION_TYPES = ImmutableMap.of(
			ADD, () -> uiMessage("audit.creation"),
			MOD, () -> uiMessage("audit.modification"),
			DEL, () -> uiMessage("audit.deletion")
	);

	private final Date revisionDate;

	private final String revisionType;

	private final String revisionAuthor;

	private final Long revisionAuthorId;

	private Map<String, Object> fields = new HashMap<>();

	public AuditDto(final DataResponseDTO dto, final ExtRevisionEntity revisionEntity, final RevisionType revisionType) {
		this.id = String.valueOf(revisionEntity.getId());
		this.revisionDate = revisionEntity.getRevisionDate();
		this.revisionType = Optional.ofNullable(REVISION_TYPES.get(revisionType)).map(Supplier::get).orElse(null);
		this.revisionAuthor = revisionEntity.getUser() == null ? null : revisionEntity.getUser().getUserNameInitials();
		this.revisionAuthorId = revisionEntity.getUser() == null ? null : revisionEntity.getUser().getId();
		for (final Field field : FieldUtils.getAllFieldsList(dto.getClass())) {
			if (!"id".equals(field.getName()) && !field.isAnnotationPresent(JsonIgnore.class)) {
				try {
					fields.put(field.getName(), FieldUtils.readField(field, dto, true));
				} catch (Exception e) {
					throw new ServerException("Ошибка при создании объекта аудита", e);
				}
			}
		}
	}

	@JsonAnyGetter
	public Map<String, Object> getFields() {
		return fields;
	}

}
