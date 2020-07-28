/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2020 Tesler Contributors
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

package io.tesler.core.util.filter.provider.impl;

import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.dao.ClassifyDataParameter;
import io.tesler.core.dto.LovUtils;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.core.controller.param.SearchOperation.CONTAINS_ONE_OF;
import static io.tesler.core.controller.param.SearchOperation.EQUALS_ONE_OF;

@Component
@EqualsAndHashCode(callSuper = false)
public class LovValueProvider extends AbstractClassifyDataProvider implements ClassifyDataProvider {

	@Override
	protected List<ClassifyDataParameter> getProviderParameterValues(Field dtoField, ClassifyDataParameter dataParameter,
			FilterParameter filterParam, SearchParameter searchParam,
			List<ClassifyDataProvider> providers) {
		List<ClassifyDataParameter> result;
		IDictionaryType type = LovUtils.getType(dtoField);
		if (type == null) {
			throw new ServerException(
					errorMessage("error.missing_lov_annotation", dtoField.getName()));
		}
		if (CONTAINS_ONE_OF.equals(dataParameter.getOperator()) || EQUALS_ONE_OF.equals(dataParameter.getOperator())) {
			dataParameter.setValue(filterParam.getStringValuesAsList().stream()
					.map(type::lookupName)
					.collect(Collectors.toList()));
		} else {
			dataParameter.setValue(type.lookupName(filterParam.getStringValue()));
		}
		result = Collections.singletonList(dataParameter);
		return result;
	}

}
