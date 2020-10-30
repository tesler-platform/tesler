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

package io.tesler.api.data.dto.rowmeta;

import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.Ephemeral;
import io.tesler.api.data.dto.TZAware;
import io.tesler.api.util.tz.TimeZoneUtil;
import io.tesler.constgen.DtoField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDTO {

	String key;

	Boolean disabled;

	Boolean forceActive;

	Boolean ephemeral;

	Boolean hidden;

	Boolean required;

	Boolean filterable;

	String placeholder;

	@JsonIgnore
	boolean tzAware;

	String drillDown;

	String drillDownType;

	String dictionaryName;

	@JsonInclude
	Object currentValue;

	Set<DictValue> values = new LinkedHashSet<>();

	Set<DictValue> filterValues = new LinkedHashSet<>();

	Map<String, String> options = new HashMap<>();

	private FieldDTO() {
	}

	public FieldDTO(Field field) {
		this.disabled = true;
		this.required = false;
		this.forceActive = false;
		this.hidden = false;
		this.ephemeral = isEphemeral(field);
		this.filterable = false;
		this.key = field.getName();
		this.tzAware = isTzAware(field);
	}

	public void addOption(String key, String value) {
		this.options.put(key, value);
	}

	public static FieldDTO disabledField(String key) {
		FieldDTO field = new FieldDTO();
		field.setKey(key);
		field.setDisabled(Boolean.TRUE);
		return field;
	}

	public static FieldDTO enabledField(String key) {
		FieldDTO field = new FieldDTO();
		field.setKey(key);
		field.setDisabled(Boolean.FALSE);
		return field;
	}

	public static FieldDTO disabledFilterableField(String key, Collection<SimpleDictionary> filterValues) {
		FieldDTO field = disabledField(key);
		field.setFilterable(Boolean.TRUE);
		field.setFilterValues(filterValues);
		return field;
	}

	public static FieldDTO disabledFilterableField(String key) {
		return disabledFilterableField(key, Collections.emptyList());
	}

	public static FieldDTO disabledFilterableField(DtoField<? extends DataResponseDTO, ?> field) {
		return disabledFilterableField(field.getName(), Collections.emptyList());
	}

	public static FieldDTO disabledFilterableField(DtoField<? extends DataResponseDTO, ?> field,
			Collection<SimpleDictionary> filterValues) {
		return disabledFilterableField(field.getName(), filterValues);
	}

	public static boolean isTzAware(Field field) {
		TZAware tzAware = field.getDeclaredAnnotation(TZAware.class);
		return tzAware != null || TimeZoneUtil.hasTzAwareSuffix(field.getName());
	}

	public static boolean isEphemeral(Field field) {
		return field.getDeclaredAnnotation(Ephemeral.class) != null;
	}

	public void setValues(Collection<SimpleDictionary> SimpleDictionaryList) {
		if (SimpleDictionaryList == null) {
			return;
		}
		SimpleDictionaryList.forEach(this::addValue);
	}

	public void addValue(final SimpleDictionary SimpleDictionary) {
		if (SimpleDictionary != null && SimpleDictionary.isActive()) {
			values.add(new DictValue(SimpleDictionary.getValue()));
		}
	}

	public void removeValue(final SimpleDictionary SimpleDictionary) {
		if (SimpleDictionary != null) {
			values.remove(new DictValue(SimpleDictionary.getValue()));
		}
	}

	public void clearValues() {
		values.clear();
	}

	public void setFilterValues(Collection<SimpleDictionary> SimpleDictionaryList) {
		if (SimpleDictionaryList == null) {
			return;
		}
		SimpleDictionaryList.forEach(this::addFilterValue);
	}

	public void addFilterValue(final SimpleDictionary SimpleDictionary) {
		if (SimpleDictionary != null) {
			filterValues.add(new DictValue(SimpleDictionary.getValue()));
		}
	}

	public void removeFilterValue(final SimpleDictionary SimpleDictionary) {
		if (SimpleDictionary != null) {
			filterValues.remove(new DictValue(SimpleDictionary.getValue()));
		}
	}

	public void clearFilterValues() {
		filterValues.clear();
	}

	public void setIconWithValue(String val, IconCode icon, boolean isFilterValue) {
		Set<DictValue> dictValues = isFilterValue ? filterValues : values;
		dictValues.add(new DictValue(val, icon.code));
	}

	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Getter
	public static final class DictValue {

		private final String value;

		private final String icon;

		@JsonIgnore
		private final int hash;

		private final Map<String, String> options = new HashMap<>();

		private DictValue(String value) {
			this(value, null);
		}

		public DictValue(String value, String icon) {
			this.value = value;
			this.icon = icon;
			this.hash = value != null ? value.hashCode() : 0;
		}

		public void addOption(String key, String value) {
			this.options.put(key, value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof DictValue)) {
				return false;
			}
			DictValue dictValue = (DictValue) o;
			return value == null ? dictValue.value == null : value.equals(dictValue.value);
		}

		@Override
		public int hashCode() {
			return hash;
		}

	}

}

