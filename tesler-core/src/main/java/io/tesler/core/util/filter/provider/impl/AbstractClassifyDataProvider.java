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

import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.controller.param.SearchOperation;
import io.tesler.core.dao.ClassifyDataParameter;
import io.tesler.core.util.DateTimeUtil;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.tesler.core.controller.param.SearchOperation.SPECIFIED;

public abstract class AbstractClassifyDataProvider implements ClassifyDataProvider {

	@Override
	public List<ClassifyDataParameter> getClassifyDataParameters(Field dtoField, FilterParameter filterParam,
			SearchParameter searchParam, List<ClassifyDataProvider> providers) {
		ClassifyDataParameter dataParameter = getBaseClassifyDataParameter(filterParam, searchParam);
		List<ClassifyDataParameter> baseClassifyDataParameterList = getBaseClassifyDataParameterList(dataParameter, filterParam, searchParam);
		if (baseClassifyDataParameterList != null) {
			return baseClassifyDataParameterList;
		}
		return getProviderParameterValues(dtoField, dataParameter, filterParam, searchParam, providers);
	}

	protected abstract List<ClassifyDataParameter> getProviderParameterValues(Field dtoField, ClassifyDataParameter dataParameter,
			FilterParameter filterParam, SearchParameter searchParam,
			List<ClassifyDataProvider> providers);

	private static List<ClassifyDataParameter> getBaseClassifyDataParameterList(ClassifyDataParameter dataParameter,
																				FilterParameter filterParameter, SearchParameter searchParameter) {
		List<ClassifyDataParameter> parameters = new ArrayList<>();
		if (searchParameter.suppressProcess()) {
			return parameters;
		}
		if (SPECIFIED.equals(dataParameter.getOperator())) {
			dataParameter.setValue(filterParameter.getBooleanValue());
			parameters.add(dataParameter);
			return parameters;
		}
		return null;
	}

	private static ClassifyDataParameter getBaseClassifyDataParameter(FilterParameter filterParameter, SearchParameter searchParameter) {
		String dtoFieldName = filterParameter.getName();
		return ClassifyDataParameter.builder()
				.operator(filterParameter.getOperation())
				.provider(searchParameter.provider())
				.field(searchParameter.name().isEmpty()
						? dtoFieldName
						: searchParameter.name())
				.build();
	}

	protected static void setClassifyDateParameterDateValue(ClassifyDataParameter dataParameter, FilterParameter param, boolean tzAware,
															SearchParameter parameter, List<ClassifyDataParameter> result) {
		switch (dataParameter.getOperator()) {
			case GREATER_OR_EQUAL_THAN:
			case LESS_THAN:
				dataParameter.setValue(param.getDateValue()
						.with(DateTimeUtil.asStartOfDay())
						.with(DateTimeUtil.fromSession(tzAware)));
				if (parameter.strict()) {
					dataParameter.setValue(param.getDateValue().with(DateTimeUtil.fromSession(tzAware)));
				}
				break;
			case LESS_OR_EQUAL_THAN:
			case GREATER_THAN:
				dataParameter.setValue(param.getDateValue()
						.with(DateTimeUtil.asEndOfDay())
						.with(DateTimeUtil.fromSession(tzAware)));
				if (parameter.strict()) {
					dataParameter.setValue(param.getDateValue().with(DateTimeUtil.fromSession(tzAware)));
				}
				break;
			case EQUALS:
				LocalDateTime startValue;
				if (DateValueProvider.class.equals(parameter.provider())) {
					startValue = param.getDateValue().with(DateTimeUtil.asStartOfDay());
				} else {
					startValue = param.getDateValue().withSecond(0);
				}
				dataParameter.setValue(startValue.with(DateTimeUtil.fromSession(tzAware)));
				dataParameter.setOperator(SearchOperation.GREATER_OR_EQUAL_THAN);
				ClassifyDataParameter dataParameterAdditional = ClassifyDataParameter
						.builder()
						.operator(SearchOperation.LESS_THAN)
						.field(dataParameter.getField())
						.build();
				if (DateValueProvider.class.equals(parameter.provider())) {
					dataParameterAdditional.setValue(startValue
							.with(DateTimeUtil.asEndOfDay())
							.with(DateTimeUtil.fromSession(tzAware)));
				} else {
					dataParameterAdditional.setValue(
							LocalDateTime.of(
									startValue.toLocalDate(),
									startValue.toLocalTime().plusMinutes(1)
							).with(DateTimeUtil.fromSession(tzAware)));

				}
				result.add(dataParameterAdditional);
				break;
			default:
				dataParameter.setValue(param.getDateValue().with(DateTimeUtil.fromSession(tzAware)));
				break;
		}
	}


}
