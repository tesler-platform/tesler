/*-
 * #%L
 * IO Tesler - Liquibase
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

package io.tesler.db.migration.liquibase.data;

import io.tesler.db.migration.liquibase.annotations.DBEntity;
import io.tesler.db.migration.liquibase.annotations.DBField;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import liquibase.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LqbAbstractEntity {

	public final List<Field> getDBRelatedFields() {
		List<Field> result = new ArrayList<>();
		Class clazz = getClass();
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				DBField DBField = field.getAnnotation(DBField.class);
				if (DBField == null) {
					continue;
				}
				if (StringUtils.isEmpty(DBField.columnName())) {
					continue;
				}
				result.add(field);
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	public String getFunctionValue(Field field) throws Exception {
		DBField DBField = field.getAnnotation(DBField.class);
		if (StringUtils.isEmpty(DBField.functionField())) {
			return null;
		}
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(DBField.functionField());
		return (String) propertyDescriptor.getReadMethod().invoke(this);
	}

	public String getSequenceValue(Field field) throws Exception {
		DBField DBField = field.getAnnotation(DBField.class);
		if (StringUtils.isEmpty(DBField.sequenceField())) {
			return null;
		}
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(DBField.sequenceField());
		return (String) propertyDescriptor.getReadMethod().invoke(this);
	}

	public String getFileValue(Field field) throws Exception {
		DBField DBField = field.getAnnotation(DBField.class);
		if (StringUtils.isEmpty(DBField.fileField())) {
			return null;
		}
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(DBField.fileField());
		return (String) propertyDescriptor.getReadMethod().invoke(this);
	}

	public String getTable() {
		DBEntity DBEntity = getClass().getAnnotation(DBEntity.class);
		if (DBEntity != null) {
			return DBEntity.tableName();
		}
		return null;
	}

	public String getColumn(Field field) throws Exception {
		DBField DBField = field.getAnnotation(DBField.class);
		return DBField.columnName();
	}

	public String getPrimaryKey() {
		DBEntity DBEntity = getClass().getAnnotation(DBEntity.class);
		if (DBEntity != null) {
			return DBEntity.primaryKey();
		}
		return null;
	}

	public boolean insertNulls(Field field) throws Exception {
		DBField DBField = field.getAnnotation(DBField.class);
		return DBField.insertNulls();
	}

	public Object getPKValue() throws Exception {
		String pkName = getPrimaryKey();
		if (pkName == null) {
			return null;
		}
		for (Field field : getDBRelatedFields()) {
			if (pkName.equalsIgnoreCase(getColumn(field))) {
				return field.get(this);
			}
		}
		return null;
	}

	protected BeanInfo getBeanInfo() throws Exception {
		return Introspector.getBeanInfo(getClass());
	}

	protected PropertyDescriptor getPropertyDescriptor(String name) throws Exception {
		return Arrays.stream(getBeanInfo().getPropertyDescriptors())
				.filter(p -> name.equals(p.getName()))
				.findFirst().orElseThrow(() -> new IllegalArgumentException(name));
	}

}
