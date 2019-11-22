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

package io.tesler.core.ui.model.json;

import io.tesler.core.ui.field.link.LinkToField;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PivotMeta {

	private String title;

	private Boolean bordered;

	private List<TableColRow> rows;

	private List<TableColRow> cols;

	private List<TableValue> values;

	@Getter
	@Setter
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "from", defaultImpl = TableColRowConst.class, visible = true)
	@JsonSubTypes({
			@JsonSubTypes.Type(value = TableColRowConst.class, name = "const"),
			@JsonSubTypes.Type(value = TableColRowFromData.class, name = "data")
	})
	public abstract static class TableColRow extends CellStyle {

		private String id;

		private String from;

		private List<TableColRow> children;

	}

	@Getter
	@Setter
	public static class TableColRowConst extends TableColRow {

		private String title;

	}

	@Getter
	@Setter
	public static class TableColRowFromData extends TableColRow {

		@LinkToField
		private String key;

	}

	@Getter
	@Setter
	public static class TableValue extends CellStyle {

		private String row;

		private String col;

		private FieldMeta field;

	}

}
