/*-
 * #%L
 * IO Tesler - Dictionary Links Implementation
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

package io.tesler.source.service.meta;

import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.InnerFieldMetaBuilder;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService;
import io.tesler.source.dto.CustomizableResponseServiceDto;
import io.tesler.source.dto.CustomizableResponseServiceDto_;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomizableResponseServiceFieldMetaBuilder extends InnerFieldMetaBuilder<CustomizableResponseServiceDto> {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private BcRegistry bcRegistry;

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<CustomizableResponseServiceDto> fields,
			InnerBcDescription bcDescription, Long id,
			Long parentId) {
		fields.setEnabled(CustomizableResponseServiceDto_.serviceName);
		List<String> alreadyCustomizableServices = jpaDao.getList(CustomizableResponseService.class).stream()
				.map(CustomizableResponseService::getServiceName)
				.filter(Objects::nonNull).collect(Collectors.toList());
		String[] notCustomizable = bcRegistry.select(InnerBcDescription.class)
				.map(InnerBcDescription::getServiceClass)
				.distinct()
				.map(Class::getSimpleName)
				.filter(service -> !alreadyCustomizableServices.contains(service)).toArray(String[]::new);
		fields.setDictionaryTypeWithCustomValues(CustomizableResponseServiceDto_.serviceName, notCustomizable);
	}

	@Override
	public void buildIndependentMeta(FieldsMeta<CustomizableResponseServiceDto> fields, InnerBcDescription bcDescription,
			Long parentId) {
		fields.enableFilter(CustomizableResponseServiceDto_.serviceName);
		List<SimpleDictionary> filterServices = bcRegistry.select(InnerBcDescription.class)
				.map(InnerBcDescription::getServiceClass)
				.distinct()
				.map(Class::getSimpleName)
				.map(className -> new SimpleDictionary(className, className))
				.collect(Collectors.toList());
		fields.setConcreteFilterValues(CustomizableResponseServiceDto_.serviceName, filterServices);
	}

}
