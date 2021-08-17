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

import com.google.common.collect.Lists;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.json.field.FieldMeta;
import io.tesler.core.util.JsonUtils;
import io.tesler.model.ui.entity.Widget;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class SimpleFormFieldExtractor extends BaseFieldExtractor {

	@Override
	public Set<BcField> extract(Widget widget) {
		final Set<BcField> widgetFields = new HashSet<>(extractFieldsFromTitle(widget, widget.getTitle()));
		for (final FieldMeta field : JsonUtils.readValue(FieldMeta[].class, widget.getFields())) {
			widgetFields.addAll(extract(widget, field));
		}
		return widgetFields;
	}

	@Override
	public List<String> getSupportedTypes() {
		return Lists.newArrayList(
				"Form"
		);
	}

	@Override
	public int getPriority() {
		return 2;
	}

}
