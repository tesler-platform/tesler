/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.meta;

import static io.tesler.vanilla.dto.VanillaDocDTO_.disablePlanDate;
import static io.tesler.vanilla.dto.VanillaDocDTO_.disableTaskStatus;
import static io.tesler.vanilla.dto.VanillaDocDTO_.drllDwnWthSrchSpc;
import static io.tesler.vanilla.dto.VanillaDocDTO_.ephemeral;
import static io.tesler.vanilla.dto.VanillaDocDTO_.errorCategory;
import static io.tesler.vanilla.dto.VanillaDocDTO_.errorName;
import static io.tesler.vanilla.dto.VanillaDocDTO_.errorType;
import static io.tesler.vanilla.dto.VanillaDocDTO_.forceName;
import static io.tesler.vanilla.dto.VanillaDocDTO_.forceTaskStatus;
import static io.tesler.vanilla.dto.VanillaDocDTO_.hidden;
import static io.tesler.vanilla.dto.VanillaDocDTO_.maskedPhone;
import static io.tesler.vanilla.dto.VanillaDocDTO_.maskedPostalCode;
import static io.tesler.vanilla.dto.VanillaDocDTO_.name;
import static io.tesler.vanilla.dto.VanillaDocDTO_.planDate;
import static io.tesler.vanilla.dto.VanillaDocDTO_.result;
import static io.tesler.vanilla.dto.VanillaDocDTO_.richTextEditor;
import static io.tesler.vanilla.dto.VanillaDocDTO_.rowMetaDrilldown;
import static io.tesler.vanilla.dto.VanillaDocDTO_.taskStatus;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testCheckbox;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testDate;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testDateTime;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testDateTimeWithSeconds;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testDictionary;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testFileId;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testFileName;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testFractional;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testInput;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testMoney;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testMonthYear;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testMultivalue;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testNumber;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testPercent;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testPickList;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testSourceFileId;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testSourceFileName;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testText;
import static io.tesler.vanilla.dto.VanillaDocDTO_.testVirtualNumber;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.vanilla.dto.VanillaDocDTO;
import org.springframework.stereotype.Service;

@Service
public class VanillaDocFieldMetaBuilder extends FieldMetaBuilder<VanillaDocDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<VanillaDocDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		// TODO implementation
		fields.setDictionaryTypeWithAllValues(testDictionary, DictionaryType.TASK_CATEGORY);
		fields.setDrilldown(
				drllDwnWthSrchSpc,
				DrillDownType.INNER,
				"screen/vanilla/view/vanilla/legalResidentVanilla/" + rowId.toString()
						+ "/?filters=eyJsZWdhbFJlc2lkZW50VmFuaWxsYUlkVGFza1ZhbmlsbGEiOiAibmFtZS5jb250YWlucz04In0="
		);
		fields.setDrilldown(
				testMultivalue, DrillDownType.INNER,
				"screen/doc/view/docAssocListPopup/bcExample/" + rowId
		);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<VanillaDocDTO> fields, InnerBcDescription bcDescription, Long parRowId) {
		fields.setEnabled(
				testInput,
				testText,
				testDate,
				testDateTime,
				testDateTimeWithSeconds,
				testMonthYear,
				testNumber,
				testFractional,
				testPercent,
				testMoney,
				testCheckbox,
				testDictionary,
				testPickList,
				testMultivalue,
				testFileName,
				testFileId,
				testSourceFileName,
				testSourceFileId,
				name,
				result,
				forceTaskStatus,
				planDate,
				taskStatus,
				errorName,
				errorType,
				errorCategory,
				richTextEditor,
				ephemeral,
				hidden,
				maskedPhone,
				maskedPostalCode
		);
		fields.setDisabled(
				disableTaskStatus,
				disablePlanDate,
				forceName,
				testVirtualNumber
		);
		fields.setDictionaryTypeWithAllValues(taskStatus, DictionaryType.TASK_CATEGORY);
		fields.setDictionaryTypeWithAllValues(forceTaskStatus, DictionaryType.TASK_CATEGORY);
		fields.setDictionaryTypeWithAllValues(disableTaskStatus, DictionaryType.TASK_CATEGORY);

		fields.setDictionaryTypeWithAllValues(errorType, DictionaryType.TASK_TYPE);
		fields.setDictionaryTypeWithAllValues(errorCategory, DictionaryType.TASK_CATEGORY);

		fields.setForceActive(forceTaskStatus);

		fields.setRequired(errorName);
		fields.setForceActive(errorType);
		fields.setForceActive(errorCategory);

		fields.setEphemeral(ephemeral);

		fields.setHidden(hidden);

		fields.enableFilter(
				testNumber,
				testFractional,
				testInput,
				testDate,
				testDateTime,
				testDateTimeWithSeconds,
				testMonthYear,
				testCheckbox,
				testDictionary,
				testPickList,
				testVirtualNumber
		);

		fields.setDrilldown(rowMetaDrilldown, DrillDownType.INNER, "screen/doc/view/errors");
	}

}
