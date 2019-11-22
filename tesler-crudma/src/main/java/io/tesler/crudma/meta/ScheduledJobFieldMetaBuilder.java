/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.meta;

import static io.tesler.crudma.dto.ScheduledJobDTO_.active;
import static io.tesler.crudma.dto.ScheduledJobDTO_.cronExpression;
import static io.tesler.crudma.dto.ScheduledJobDTO_.lastLaunchDate;
import static io.tesler.crudma.dto.ScheduledJobDTO_.launchStatusCd;
import static io.tesler.crudma.dto.ScheduledJobDTO_.serviceName;

import io.tesler.api.data.dictionary.CoreDictionaries.LaunchStatus;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.crudma.dto.ScheduledJobDTO;
import io.tesler.model.core.dao.JpaDao;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScheduledJobFieldMetaBuilder extends FieldMetaBuilder<ScheduledJobDTO> {

	private final DictionaryCache dictionaryCache;

	@Autowired
	JpaDao jpaDao;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<ScheduledJobDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		buildRowDependentCommonMeta(fields, bcDescription, parRowId);
	}

	private void buildRowDependentCommonMeta(RowDependentFieldsMeta<ScheduledJobDTO> fields,
			InnerBcDescription bcDescription, Long parRowId) {
		fields.setEnabled(serviceName, cronExpression, active);
		fields.setRequired(serviceName, cronExpression);
		fields.setDictionaryTypeWithAllValues(serviceName, DictionaryType.SCHEDULED_SERVICES);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<ScheduledJobDTO> fields, InnerBcDescription bcDescription,
			Long parRowId) {
		fields.enableFilter(serviceName, active, lastLaunchDate, launchStatusCd);
		fields.setAllFilterValuesByLovType(serviceName, DictionaryType.SCHEDULED_SERVICES);
		fields.setConcreteFilterValues(launchStatusCd, getApplicableStatuses());
	}

	private List<SimpleDictionary> getApplicableStatuses() {
		ArrayList<SimpleDictionary> dictDTOS = new ArrayList<>();
		dictDTOS.add(dictionaryCache.get(DictionaryType.LAUNCH_STATUS, LaunchStatus.SUCCESS.getKey()));
		dictDTOS.add(dictionaryCache.get(DictionaryType.LAUNCH_STATUS, LaunchStatus.FAILED.getKey()));
		return dictDTOS;
	}

}
