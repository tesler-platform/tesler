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

package io.tesler.sqlbc.crudma;

import io.tesler.api.util.tz.TimeZoneUtil;
import io.tesler.core.controller.param.SearchOperation;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.model.ui.entity.Bc;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import io.tesler.sqlbc.exception.BadSqlComponentException;
import io.tesler.sqlbc.dao.SqlFieldType;


public final class SqlBcDescription extends BcDescription {

	@Getter
	private final String query;

	@Getter
	private final String defaultOrder;

	@Getter
	private final String reportDateField;

	@Getter
	private final boolean editable;

	private final LazyInitializer<List<Field>> fieldsInitializer;

	@Getter
	private final List<Bind> binds;

	public SqlBcDescription(Bc bc, List<Bind> binds, LazyInitializer<List<Field>> fieldsInitializer) {
		super(bc.getName(), bc.getParentName(), SqlCrudmaService.class, Boolean.TRUE.equals(bc.getRefresh()));
		this.id = bc.getId();
		this.query = bc.getQuery();
		this.defaultOrder = bc.getDefaultOrder();
		this.reportDateField = bc.getReportDateField();
		this.pageLimit = bc.getPageLimit();
		this.editable = Boolean.TRUE.equals(bc.getEditable());
		this.fieldsInitializer = fieldsInitializer;
		this.binds = binds;
		this.bindsString = bc.getBinds();
	}

	public List<Field> getFields() {
		try {
			return fieldsInitializer.get();
		} catch (Exception e) {
			throw new BadSqlComponentException(getName(), e);
		}
	}

	@Getter
	@AllArgsConstructor
	public static final class Field {

		private final String columnName;

		private final SqlFieldType type;

		private final Boolean editable;

		public String getFieldName() {
			return columnName.toLowerCase();
		}

		public boolean isTzAware() {
			return TimeZoneUtil.hasTzAwareSuffix(this.getFieldName()) && this.getType().isChronological();
		}

	}

	@Getter
	@AllArgsConstructor
	public static final class Bind {

		private String bindName;

		private SearchOperation type;

		public boolean isExistInQuery(String query) {
			return query.contains(":" + getBindName());
		}

	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static final class Bindings {

		private String title;

		private String key;

		private String type;

		private List<Map<String, Object>> operations;

		private String popupBcName;

		private Map<String, Object> pickMap;

		private List<String> dictionaryValues;

		private Boolean permanent;


	}

}
