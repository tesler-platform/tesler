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

package io.tesler.core.ui.field.link;

import io.tesler.core.ui.field.CustomFieldExtractor;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.BcField.Attribute;
import io.tesler.model.ui.entity.Widget;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;


public final class LinkFieldExtractor {

	@SneakyThrows
	public static Set<BcField> extract(final Widget widget, final Object object) {
		return extract(widget.getId(), widget.getBc(), object);
	}

	@SneakyThrows
	public static Set<BcField> extract(final Long widgetId, final String bc, final Object object) {
		final Set<BcField> fields = new HashSet<>();
		for (final Field field : FieldUtils.getAllFieldsList(object.getClass())) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(LinkToField.class) && field.get(object) != null) {
				fields.add(new BcField(bc, (String) field.get(object))
						.putAttribute(Attribute.WIDGET_ID, widgetId)
				);
			}
		}
		Set<BcField> customFields = CustomFieldExtractor.extract(widgetId, bc, object);
		fields.addAll(customFields);
		return fields;
	}

}
