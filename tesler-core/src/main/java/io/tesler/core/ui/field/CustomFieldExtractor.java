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

package io.tesler.core.ui.field;

import io.tesler.api.util.ServiceUtils;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.BcField.Attribute;
import io.tesler.core.ui.model.json.FieldMeta.FieldMetaBase;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.SneakyThrows;

public class CustomFieldExtractor {

	@SneakyThrows
	public static Set<BcField> extract(final Long widgetId, final String bc, final Object meta) {
		final Set<BcField> fields = new HashSet<>();
		CustomFields service = ServiceUtils.getService(CustomFields.class, null);
		if (service == null) {
			return fields;
		}

		Map<String, String> custom = ((FieldMetaBase) meta).getCustomFields();
		List<String> fieldNames = service.getFieldNames();

		for (Entry<String, String> entry : custom.entrySet()) {
			if (!fieldNames.contains(entry.getKey())) {
				throw new IllegalArgumentException("Not found field with key:" + entry.getKey());
			}

			fields.add(new BcField(bc, entry.getValue())
					.putAttribute(Attribute.WIDGET_ID, widgetId)
			);
		}

		return fields;
	}

}
