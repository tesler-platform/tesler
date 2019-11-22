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

package io.tesler.core.dto;

import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;


@UtilityClass
public class DTOUtils {

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <D extends DataResponseDTO> DtoField<D, ?> getField(Class<D> cls, String fieldName) {
		final Field metamodelField = FieldUtils.getField(Class.forName(cls.getName() + "_"), fieldName);
		if (metamodelField == null) {
			return null;
		}
		return (DtoField<D, ?>) metamodelField.get(null);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <D extends DataResponseDTO> Set<DtoField<D, ?>> getAllFields(Class<D> cls) {
		final Set<DtoField<D, ?>> fields = new HashSet<>();
		for (final Field field : FieldUtils.getAllFieldsList(Class.forName(cls.getName() + "_"))) {
			fields.add((DtoField<D, ?>) field.get(null));
		}
		return fields;
	}

	public static String getDictionaryType(Class<?> cls, String fieldName) {
		return Optional.ofNullable(FieldUtils.getField(cls, fieldName, true))
				.map(LovUtils::getType)
				.map(IDictionaryType::getName)
				.orElse(null);
	}

}
