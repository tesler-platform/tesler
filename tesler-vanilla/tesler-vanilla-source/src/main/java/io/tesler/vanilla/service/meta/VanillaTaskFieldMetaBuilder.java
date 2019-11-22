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

import static io.tesler.api.data.dictionary.DictionaryType.PERIODICAL_TYPE;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.activityType;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.comboConditionTest;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.createDate;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.decimalInputTest;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.editor;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.executor;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.executorId;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.executorName;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.fileId;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.fileName;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.job;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.legalPersonName;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.moneyInputTest;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.name;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.numberInputTest;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.percentInputTest;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.periodicalType;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.planDate;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.priority;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.reportDate;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.reportPeriod;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.result;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.supervisedMonitor;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.taskCategory;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.taskStatus;
import static io.tesler.vanilla.dto.VanillaTaskDTO_.taskType;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.rowmeta.IconCode;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.vanilla.dto.VanillaTaskDTO;
import io.tesler.vanilla.entity.VanillaTask;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VanillaTaskFieldMetaBuilder extends FieldMetaBuilder<VanillaTaskDTO> {

	private final JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<VanillaTaskDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		VanillaTask task = jpaDao.findById(VanillaTask.class, rowId);
		if (task.getPriority() != null && task.getPriority().getKey() != null) {
			switch (task.getPriority().getKey()) {
				case "LOW":
					fields.setDictionaryTypeWithConcreteValues(
							periodicalType,
							PERIODICAL_TYPE,
							"NOT_PERIODICAL",
							"ONE_TIME_PER_YEAR"
					);
					break;
				case "MIDDLE":
					fields.setDictionaryTypeWithConcreteValues(
							periodicalType,
							PERIODICAL_TYPE,
							"ONE_TIME_PER_HALF_YEAR",
							"ONE_TIME_PER_QUARTER"
					);
					break;
				case "HIGH":
					fields.setDictionaryTypeWithConcreteValues(periodicalType, PERIODICAL_TYPE, "ONE_TIME_PER_MONTH", "DAILY");
					break;
				default:
					break;
			}
		}
		fields.setRequired(taskCategory, taskType);
		fields.setDictionaryTypeWithAllValues(activityType, DictionaryType.ACTIVITY_TYPE);
		fields.setDictionaryTypeWithAllValues(taskCategory, DictionaryType.TASK_CATEGORY);
		fields.setDictionaryTypeWithAllValues(taskType, DictionaryType.TASK_TYPE);
		fields.setDictionaryValuesWithIcons(priority, DictionaryType.TASK_PRIORITY, ImmutableMap.of(
				new LOV("LOW"), IconCode.ARROW_DOWN_GREEN,
				new LOV("MIDDLE"), IconCode.ARROW_UP_ORANGE,
				new LOV("HIGH"), IconCode.ARROW_UP_RED
		));
		fields.setDictionaryTypeWithAllValues(reportPeriod, DictionaryType.REPORT_PERIOD);
		fields.setDrilldown(
				name,
				DrillDownType.INNER,
				"screen/vanilla/view/vanilla3/legalResidentVanilla/" + parRowId + "/taskVanilla/" + rowId
		);
		fields.setDrilldown(
				legalPersonName,
				DrillDownType.INNER,
				"screen/counterparty/view/counterparty/counterparty/" + parRowId
		);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<VanillaTaskDTO> fields, InnerBcDescription bcDescription, Long parRowId) {
		fields.setEnabled(
				activityType,
				taskCategory,
				taskType,
				createDate,
				name,
				priority,
				job,
				reportPeriod,
				reportDate,
				supervisedMonitor,
				result,
				periodicalType,
				executor,
				executorId,
				executorName,
				fileId,
				fileName,
				taskStatus,
				comboConditionTest,
				moneyInputTest,
				numberInputTest,
				decimalInputTest,
				percentInputTest,
				editor
		);
		fields.enableFilter(name, legalPersonName,
				taskType, planDate, createDate
		);
		fields.setAllFilterValuesByLovType(taskType, DictionaryType.TASK_TYPE);
		fields.setForceActive(priority);
	}

}
