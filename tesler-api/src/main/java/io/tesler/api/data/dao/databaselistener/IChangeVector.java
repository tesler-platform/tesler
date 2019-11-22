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

package io.tesler.api.data.dao.databaselistener;

import io.tesler.api.data.dictionary.LOV;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import javax.persistence.metamodel.Attribute;


public interface IChangeVector {

	default LOV getEventName() {
		return null;
	}

	@SuppressWarnings("unchecked")
	default <E, V> V getValue(Attribute<E, V> field) {
		try {
			E entity = unwrap(field.getDeclaringType().getJavaType());
			Member member = field.getJavaMember();
			if (member instanceof Method) {
				return (V) ((Method) member).invoke(entity);
			} else if (member instanceof Field) {
				return (V) ((Field) member).get(entity);
			} else {
				throw new IllegalArgumentException("Unexpected java member type. Expecting method or field, found: " + member);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	Object getEntity();

	default <E> E unwrap(Class<E> cls) {
		return cls.cast(getEntity());
	}

	default boolean isNew() {
		return false;
	}

	default boolean isDelete() {
		return false;
	}

	default boolean isUpdate() {
		return false;
	}

	default boolean isUnManaged() {
		return false;
	}

	default boolean hasChanged(Attribute<?, ?> attribute) {
		return false;
	}

	default <T> T getOldValue(Attribute<?, ?> attribute) {
		return null;
	}

}
