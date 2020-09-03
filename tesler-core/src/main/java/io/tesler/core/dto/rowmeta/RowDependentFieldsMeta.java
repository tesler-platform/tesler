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

package io.tesler.core.dto.rowmeta;

import static io.tesler.api.data.dictionary.DictionaryCache.dictionary;

import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.FieldsDTO;
import io.tesler.api.data.dto.rowmeta.IconCode;
import io.tesler.constgen.DtoField;
import io.tesler.core.dto.FieldDrillDown;
import io.tesler.core.service.action.DrillDownTypeSpecifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RowDependentFieldsMeta<T extends DataResponseDTO> extends FieldsDTO {

	/**
	 * Adds a value to an existing list of selectable values
	 *
	 * @param field widget field with type dictionary
	 * @param dictDTO DTO with dictionary value
	 */
	public final void addConcreteValue(DtoField<? super T, ?> field, SimpleDictionary dictDTO) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> fieldDTO.addValue(dictDTO));
	}

	/**
	 * Fills the list of selectable values in the dropdown list with concrete dictionary values
	 *
	 * @param field widget field with type dictionary
	 * @param type dictionary type
	 * @param lovs list of dictionary codes (with type LOV)
	 */
	public final void setDictionaryTypeWithConcreteValuesFromList(DtoField<? super T, ?> field, IDictionaryType type,
			List<LOV> lovs) {
		String[] keys = lovs.stream()
				.filter(Objects::nonNull)
				.map(LOV::getKey)
				.toArray(String[]::new);
		setDictionaryTypeWithConcreteValues(field, type, keys);
	}

	@Deprecated
	public void setDictionaryValuesWithIcons(String field, IDictionaryType type, Map<LOV, IconCode> valueIconMap) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDictionaryName(type.getName());
					fieldDTO.clearValues();
					valueIconMap
							.forEach((key, value) -> fieldDTO
									.setIconWithValue(type.lookupValue(key), value, false));
				});
	}

	//DtoField METHODS
	@SafeVarargs
	public final void setRequired(DtoField<? super T, ?>... fields) {
		required(true, fields);
	}

	@SafeVarargs
	public final void setNotRequired(DtoField<? super T, ?>... fields) {
		required(false, fields);
	}

	@SafeVarargs
	public final void required(boolean required, DtoField<? super T, ?>... fields) {
		Stream.of(fields).forEach(field ->
				Optional.ofNullable(field)
						.map(dtoField -> this.fields.get(dtoField.getName()))
						.ifPresent(fieldDTO -> fieldDTO.setRequired(required)));
	}

	public final void disableFields() {
		fields.values().forEach(fieldDTO -> fieldDTO.setDisabled(true));
	}

	@SafeVarargs
	public final void setDisabled(DtoField<? super T, ?>... fields) {
		disable(true, fields);
	}

	@SafeVarargs
	public final void setEnabled(DtoField<? super T, ?>... fields) {
		disable(false, fields);
	}

	public final void disable(final boolean disabled, DtoField<? super T, ?>... fields) {
		List<String> fieldsList = Stream.of(fields).map(DtoField::getName).collect(Collectors.toList());
		disable(disabled, fieldsList);
	}

	public void setDisabled(List<String> fields) {
		disable(true, fields);
	}

	private final void disable(final boolean disabled, List<String> fields) {
		fields.forEach(field -> Optional.ofNullable(field).map(dtoField -> this.fields.get(dtoField))
				.ifPresent(fieldDTO -> fieldDTO.setDisabled(disabled)));
	}

	public final void setDictionaryTypeWithAllValues(DtoField<? super T, ?> field, IDictionaryType type) {
		setDictionaryTypeWithAllValues(field, type.getName());
	}

	public final void setDictionaryTypeWithAllValues(DtoField<? super T, ?> field, String type) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDictionaryName(type);
					fieldDTO.clearValues();
					fieldDTO.setValues(dictionary().getAll(type));
				});
	}

	@Deprecated
	public final void setDictionaryTypeWithConcreteValues(DtoField<? super T, ?> field, IDictionaryType type,
			String... keys) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDictionaryName(type.getName());
					fieldDTO.clearValues();
					List<SimpleDictionary> dictDTOList = new ArrayList<>();
					for (String key : keys) {
						SimpleDictionary dto = dictionary().get(type, key);
						if (dto != null) {
							dictDTOList.add(dto);
						}
					}
					fieldDTO.setValues(dictDTOList);
				});
	}

	public final void setDictionaryTypeWithCustomValues(DtoField<? super T, ?> field, String... keys) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.clearValues();
					List<SimpleDictionary> dictDTOList = new ArrayList<>();
					for (String key : keys) {
						SimpleDictionary dto = new SimpleDictionary(key, key);
						dictDTOList.add(dto);
					}
					fieldDTO.setValues(dictDTOList);
				});
	}


	public final void setDictionaryTypeWithConcreteValues(DtoField<? super T, ?> field, IDictionaryType type,
			List<LOV> lovs) {
		String[] keys = lovs.stream()
				.filter(Objects::nonNull)
				.map(LOV::getKey)
				.toArray(String[]::new);
		setDictionaryTypeWithConcreteValues(field, type, keys);
	}

	public final void setDictionaryTypeWithConcreteValues(DtoField<? super T, ?> field, IDictionaryType type,
			LOV... lovs) {
		String[] keys = Arrays.stream(lovs)
				.filter(Objects::nonNull)
				.map(LOV::getKey)
				.toArray(String[]::new);
		setDictionaryTypeWithConcreteValues(field, type, keys);
	}

	public final void setConcreteValues(DtoField<? super T, ?> field, Collection<SimpleDictionary> dictDtoList) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDictionaryName(field.getName());
					fieldDTO.clearValues();
					fieldDTO.setValues(dictDtoList);
				});
	}

	public final void setDrilldown(DtoField<? super T, ?> field, DrillDownTypeSpecifier drillDownType, String drillDown) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDrillDown(drillDown);
					fieldDTO.setDrillDownType(drillDownType.getValue());
				});
	}

	public final void setDrilldowns(final List<FieldDrillDown> drillDowns) {
		for (final FieldDrillDown drillDown : drillDowns) {
			Optional.ofNullable(drillDown).map(dd -> fields.get(dd.getTaskField()))
					.ifPresent(fieldDTO -> {
						fieldDTO.setDrillDown(drillDown.getUrl());
						fieldDTO.setDrillDownType(drillDown.getType().getValue());
					});
		}
	}

	public final void setDictionaryValuesWithIcons(DtoField<? super T, ?> field, IDictionaryType type,
			Map<LOV, IconCode> valueIconMap) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> {
					fieldDTO.setDictionaryName(type.getName());
					fieldDTO.clearValues();
					valueIconMap
							.forEach((key, value) -> fieldDTO
									.setIconWithValue(type.lookupValue(key), value, false));
				});
	}

	public final void setCurrentValue(DtoField<? super T, ?> field, Object value) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> fieldDTO.setCurrentValue(value));
	}

	public final void setPlaceholder(DtoField<? super T, ?> field, String placeholder) {
		Optional.ofNullable(field).map(dtoField -> fields.get(dtoField.getName()))
				.ifPresent(fieldDTO -> fieldDTO.setPlaceholder(placeholder));
	}

}
