/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.data.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tesler.api.data.IDataContainer;
import io.tesler.constgen.DtoField;
import io.tesler.constgen.DtoMetamodelIgnore;
import io.tesler.constgen.GeneratesDtoMetamodel;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GeneratesDtoMetamodel
@JsonFilter("dtoPropertyFilter")
public abstract class DataResponseDTO implements CheckedDto, IDataContainer<DataResponseDTO>, Serializable {

	@JsonIgnore
	@DtoMetamodelIgnore
	protected Set<String> changedFields = new TreeSet<>();

	protected String id;

	protected Entity errors;

	@Ephemeral
	protected long vstamp;

	@JsonIgnore
	@DtoMetamodelIgnore
	private Set<String> serializableFields;

	@JsonIgnore
	@DtoMetamodelIgnore
	private Set<String> computedFields;

	public boolean hasChangedFields() {
		return changedFields.size() > 0;
	}

	public boolean isFieldChanged(final DtoField<?, ?> dtoField) {
		return isFieldChanged(dtoField.getName());
	}

	public boolean isFieldChanged(final String fieldName) {
		return changedFields.contains(fieldName);
	}

	public void addChangedField(String fieldName) {
		changedFields.add(fieldName);
	}

	public void addChangedField(DtoField<?, ?> dtoField) {
		addChangedField(dtoField.getName());
	}

	public boolean isFieldSerializable(String fieldName) {
		return serializableFields == null || serializableFields.contains(fieldName);
	}

	public boolean isFieldComputed(String fieldName) {
		return computedFields == null || computedFields.contains(fieldName);
	}

	public void addComputedField(String fieldName) {
		if (computedFields == null) {
			computedFields = new TreeSet<>();
		}
		computedFields.add(fieldName);
	}

	@Override
	public void transformData(Function<DataResponseDTO, DataResponseDTO> function) {
		function.apply(this);
	}

}
