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

import static io.tesler.vanilla.dto.VanillaResidentDTO_.drllDwnWthSrchSpc;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.inn;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.kpp;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.legalPersonName;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testCheckbox;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testDate;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testDateTime;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testDateTimeWithSeconds;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testDictionary;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testFractional;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testInput;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testMoney;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testMonthYear;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testNumber;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testPercent;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testPickList;
import static io.tesler.vanilla.dto.VanillaResidentDTO_.testText;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.vanilla.dto.VanillaResidentDTO;
import org.springframework.stereotype.Service;

@Service
public class VanillaResidentFieldMetaBuilder extends FieldMetaBuilder<VanillaResidentDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<VanillaResidentDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		// TODO implementation
		fields.setDictionaryTypeWithAllValues(testDictionary, DictionaryType.TASK_CATEGORY);
		fields.setDrilldown(
				drllDwnWthSrchSpc,
				DrillDownType.INNER,
				"screen/vanilla/view/vanilla/legalResidentVanilla/" + rowId.toString()
						+ "/?filters=eyJsZWdhbFJlc2lkZW50VmFuaWxsYUlkVGFza1ZhbmlsbGEiOiAibmFtZS5jb250YWlucz04In0="
		);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<VanillaResidentDTO> fields, InnerBcDescription bcDescription,
			Long parRowId) {
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
				testPickList
		);
		fields.enableFilter(
				legalPersonName,
				inn,
				kpp
		);
	}

}
