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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.tesler.core.ui.field.link.LinkToField;
import io.tesler.core.ui.model.json.FieldMeta.CheckboxFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.CheckboxSqlFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.ComboConditionFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DMNFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DateFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DateTimeFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DateTimeWithSecondsFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DictionaryFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.DiffTextFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.HiddenFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.InlinePickListFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.InputFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.MonthYearFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.MultifieldFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.MultivalueFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.NumberFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.PickListFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.PrintFormFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.RichTextMeta;
import io.tesler.core.ui.model.json.FieldMeta.TextFieldMeta;
import io.tesler.core.ui.model.json.FieldMeta.UploadFileFieldMeta;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = FieldMeta.ListColumnGroupMeta.class, visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = NumberFieldMeta.class, name = "number"),
		@JsonSubTypes.Type(value = NumberFieldMeta.class, name = "money"),
		@JsonSubTypes.Type(value = NumberFieldMeta.class, name = "percent"),
		@JsonSubTypes.Type(value = InputFieldMeta.class, name = "input"),
		@JsonSubTypes.Type(value = InputFieldMeta.class, name = "hint"),
		@JsonSubTypes.Type(value = DateFieldMeta.class, name = "date"),
		@JsonSubTypes.Type(value = CheckboxFieldMeta.class, name = "checkbox"),
		@JsonSubTypes.Type(value = CheckboxSqlFieldMeta.class, name = "checkboxSql"),
		@JsonSubTypes.Type(value = PickListFieldMeta.class, name = "pickList"),
		@JsonSubTypes.Type(value = InlinePickListFieldMeta.class, name = "inline-pickList"),
		@JsonSubTypes.Type(value = MultivalueFieldMeta.class, name = "multivalue"),
		@JsonSubTypes.Type(value = MultivalueFieldMeta.class, name = "multivalueHover"),
		@JsonSubTypes.Type(value = MultifieldFieldMeta.class, name = "multifield"),
		@JsonSubTypes.Type(value = DictionaryFieldMeta.class, name = "dictionary"),
		@JsonSubTypes.Type(value = TextFieldMeta.class, name = "text"),
		@JsonSubTypes.Type(value = DMNFieldMeta.class, name = "DMN"),
		@JsonSubTypes.Type(value = HiddenFieldMeta.class, name = "hidden"),
		@JsonSubTypes.Type(value = UploadFileFieldMeta.class, name = "fileUpload"),
		@JsonSubTypes.Type(value = DateTimeFieldMeta.class, name = "dateTime"),
		@JsonSubTypes.Type(value = MonthYearFieldMeta.class, name = "monthYear"),
		@JsonSubTypes.Type(value = DateTimeWithSecondsFieldMeta.class, name = "dateTimeWithSeconds"),
		@JsonSubTypes.Type(value = ComboConditionFieldMeta.class, name = "combo-condition"),
		@JsonSubTypes.Type(value = RichTextMeta.class, name = "richText"),
		@JsonSubTypes.Type(value = PrintFormFieldMeta.class, name = "printForm"),
		@JsonSubTypes.Type(value = DiffTextFieldMeta.class, name = "diffText")
})
public abstract class FieldMeta extends CellStyle {

	private String key;

	private String title;


	public interface FieldContainer {

		List<FieldMeta> getChildren();

	}

	@Getter
	@Setter
	public static class ListColumnGroupMeta extends FieldMeta implements FieldContainer {

		@JsonProperty("childrens")
		private List<FieldMeta> children;

	}

	@Getter
	@Setter
	public abstract static class FieldMetaBase extends FieldMeta {

		private FieldType type;

		private Boolean isValue;

		private Boolean isCol;

		private Boolean isRow;

		private String label;

		private Boolean xDefault;

		private Boolean yDefault;

		private Boolean fixedAxis;

		private Boolean required;

		private Boolean permanent;

		private Boolean drillDown;

		@LinkToField
		private String drillDownKey;

		@LinkToField
		private String drillDownTypeKey;

		private String bgColor;

		@LinkToField
		private String bgColorKey;

		@LinkToField
		private String snapshotKey;

		@LinkToField
		private String snapshotFileIdKey;

		@LinkToField
		private String iconParamsKey;

		private String iconType;

		private String iconColor;

		@LinkToField
		private String iconTypeKey;

		@LinkToField
		private String iconColorKey;

		private String groupName;

		private Long width;

		private String hintTitle;

		private String hintText;

		@LinkToField
		private String hintKey;

		private Long maxInput;

		private List<MultiSourceInfo> multisource;

	}

	@Getter
	@Setter
	public static class NumberFieldMeta extends FieldMetaBase {

		private Long digits;

		private Boolean nullable;

	}

	@Getter
	@Setter
	public static class InputFieldMeta extends FieldMetaBase {

		@LinkToField
		private String maskField;

		private InputMaskMeta mask;

	}

	@Getter
	@Setter
	public static class InputMaskMeta {

		private String type;

		private String mask;

	}

	@Getter
	@Setter
	public static class DateFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class CheckboxFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class CheckboxSqlFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class PickListFieldMeta extends FieldMetaBase {

		private String popupBcName;

		private Map<String, String> pickMap;

	}

	@Getter
	@Setter
	public static class InlinePickListFieldMeta extends PickListFieldMeta {

		private String searchSpec;

	}

	@Getter
	@Setter
	public static class MultivalueFieldMeta extends FieldMetaBase {

		private String popupBcName;

		private String assocValueKey;

		private String displayedKey;

	}

	@Getter
	@Setter
	public static class DictionaryFieldMeta extends FieldMetaBase {

		private String dictionaryName;

	}

	@Getter
	@Setter
	public static class TextFieldMeta extends FieldMetaBase {

		private Boolean popover;

		private Integer minInputRows;

		private Integer maxInputRows;

	}

	@Getter
	@Setter
	public static class DMNFieldMeta extends FieldMetaBase {

		private String popupBcName;

		private Map<String, Popup> popups;

		@Getter
		@Setter
		public static class Popup {

			private String popupBcName;

			private Map<String, String> pickMap;

		}

	}

	@Getter
	@Setter
	public static class HiddenFieldMeta extends FieldMetaBase {

		private String popupBcName;

		private Map<String, String> pickMap;

	}

	@Getter
	@Setter
	public static class UploadFileFieldMeta extends FieldMetaBase {

		@LinkToField
		private String fileIdKey;

		private String fileSource;

	}

	@Getter
	@Setter
	public static class DateTimeFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class MonthYearFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class DateTimeWithSecondsFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class ComboConditionFieldMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class RichTextMeta extends FieldMetaBase {

	}

	@Getter
	@Setter
	public static class PrintFormFieldMeta extends FieldMetaBase {

		@LinkToField
		private String fileIdKey;

		private String fileSource;

	}

	@Getter
	@Setter
	public static class MultifieldFieldMeta extends FieldMetaBase implements FieldContainer {

		@JsonProperty("fields")
		private List<FieldMeta> children;

		private String style;

	}

	@Getter
	@Setter
	public static class MultiSourceInfo {

		private String key;

		private String label;

	}

	@Getter
	@Setter
	public static class DiffTextFieldMeta extends FieldMetaBase {

		private String diffSource;

		private String diffTarget;

	}


}
