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

package io.tesler.core.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.controller.param.FilterParameters;

import java.lang.reflect.Field;

import io.tesler.core.controller.param.SearchOperation;
import io.tesler.core.dao.impl.MetadataUtils;
import io.tesler.core.exception.ClientException;
import io.tesler.core.test.util.TestResponseDto;
import io.tesler.core.util.SpringBeanUtils;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;
import io.tesler.core.util.filter.provider.impl.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchSpecificationTest {

	Map<String, String> successMap;

	Map<String, String> exceptionMap;

	StringValueProvider stringValueProvider;

	BooleanValueProvider booleanValueProvider;

	DateValueProvider dateValueProvider;

	DateTimeValueProvider dateTimeValueProvider;

	LongValueProvider longValueProvider;

	BigDecimalValueProvider bigDecimalValueProvider;

	MultisourceValueProvider multisourceValueProvider;

	MultiFieldValueProvider multiFieldValueProvider;

	LovValueProvider lovValueProvider;

	private final List<ClassifyDataProvider> providers = new ArrayList<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		DictionaryCache.instance.set(mock(DictionaryCache.class));
		BeanFactory mockedBeanFactory = mock(BeanFactory.class);
		SpringBeanUtils springBeanUtils = new SpringBeanUtils();
		springBeanUtils.setBeanFactory(mockedBeanFactory);
		when(SpringBeanUtils.getBean("teslerObjectMapper")).thenReturn(new ObjectMapper());
		setProviders();
		setSuccessMap();
		setExceptionMap();
	}

	@Test
	void searchSpecTest() {
		FilterParameters fp = FilterParameters.fromMap(successMap);
		Assertions.assertEquals(MetadataUtils.mapSearchParamsToPOJO(TestResponseDto.class, fp, providers).size(), 31);
	}

	@Test
	void searchSpecFilterExceptionTest() {
		FilterParameters fp = FilterParameters.fromMap(exceptionMap);
		fp.forEach(
				filterParameter -> {
					Field field = ReflectionUtils.findField(TestResponseDto.class, filterParameter.getName());
					assert field != null;
					SearchParameter searchParam = field.getDeclaredAnnotation(SearchParameter.class);
					assert searchParam != null;
					ClassifyDataProvider provider = providers.stream().filter(classifyDataProvider ->
							searchParam.provider().equals(classifyDataProvider.getClass())).findFirst().orElse(null);
					assert provider != null;
					if (provider.getClass().equals(LovValueProvider.class)) {
						Assertions.assertThrows(ServerException.class, () -> provider.getClassifyDataParameters(field, filterParameter, searchParam, providers));
					} else {
						Assertions.assertThrows(ClientException.class, () -> provider.getClassifyDataParameters(field, filterParameter, searchParam, providers));
					}
				}
		);
	}

	@Test
	void searchSpecClassFieldNotFoundExceptionTest() {
		FilterParameters fp = FilterParameters.fromList(
				Collections.singletonList(new FilterParameter("number", SearchOperation.CONTAINS, "str"))
		);
		Assertions.assertEquals(MetadataUtils.mapSearchParamsToPOJO(TestResponseDto.class, fp, providers).size(), 0);
	}

	@Test
	void searchSpecSearchParameterNotFoundExceptionTest() {
		FilterParameters fp = FilterParameters.fromList(
				Collections.singletonList(new FilterParameter("name", SearchOperation.GREATER_OR_EQUAL_THAN, "33"))
		);
		Assertions.assertEquals(MetadataUtils.mapSearchParamsToPOJO(TestResponseDto.class, fp, providers).size(), 0);
	}

	@AfterEach
	void tearDown() {
		DictionaryCache.instance.set(null);
	}

	private void setSuccessMap() {
		successMap = new HashMap<>();
		successMap.put("_page", "1");
		successMap.put("_limit", "5");
		successMap.put("suppressProcessString.contains", "str");
		successMap.put("string.contains", "str");
		successMap.put("string.specified", "true");
		successMap.put("string.equalsOneOf", "[\"str1\",\"str2\"]");
		successMap.put("string.containsOneOf", "[\"str1\",\"str2\"]");
		successMap.put("aBoolean.equals", "true");
		successMap.put("aLong.greaterOrEqualThan", "13");
		successMap.put("aLong.lessOrEqualThan", "33");
		successMap.put("aLong.equalsOneOf", "[\"13\",\"33\"]");
		successMap.put("aLong.containsOneOf", "[\"13\",\"33\"]");
		successMap.put("bigDecimal.equals", "0");
		successMap.put("date.greaterOrEqualThan", "2020-07-17T12:21:09");
		successMap.put("date.lessOrEqualThan", "2020-07-31T12:21:09");
		successMap.put("date.equalsOneOf", "[\"2020-07-17T12:21:09\",\"2020-07-31T12:21:09\"]");
		successMap.put("date.containsOneOf", "[\"2020-07-17T12:21:09\",\"2020-07-31T12:21:09\"]");
		successMap.put("strictDate.greaterOrEqualThan", "2020-07-17T12:21:09");
		successMap.put("strictDate.lessOrEqualThan", "2020-07-31T12:21:09");
		successMap.put("date.equals", "2020-07-17T12:21:09");
		successMap.put("dateTime.greaterOrEqualThan", "2020-07-10T12:23:45");
		successMap.put("dateTime.lessOrEqualThan", "2020-08-24T12:23:45");
		successMap.put("dateTime.equals", "2020-08-24T12:23:45");
		successMap.put("dateTime.specifiedBooleanSql", "2020-08-24T12:23:45");
		successMap.put("strictDateTime.greaterOrEqualThan", "2020-07-10T12:23:45");
		successMap.put("strictDateTime.lessOrEqualThan", "2020-08-24T12:23:45");
		successMap.put("multiSource.equals", "str");
		successMap.put("multivalueField.equalsOneOf", "[\"1\", \"2\"]");
		successMap.put("multivalueField.containsOneOf", "[\"1\", \"2\"]");
		successMap.put("lov.equalsOneOf", "[\"Task\", \"Operation\"]");
		successMap.put("lov.containsOneOf", "[\"Task\", \"Operation\"]");
		successMap.put("lov.equals", "[\"Task\"]");
	}

	private void setExceptionMap() {
		exceptionMap = new HashMap<>();
		exceptionMap.put("_page", "1");
		exceptionMap.put("_limit", "5");
		exceptionMap.put("aBoolean.containsOneOf", "true");
		exceptionMap.put("aBoolean.equalsOneOf", "false");
		exceptionMap.put("bigDecimal.containsOneOf", "5");
		exceptionMap.put("bigDecimal.equalsOneOf", "5");
		exceptionMap.put("dateTime.containsOneOf", "[\"2020-07-17T12:21:09\",\"2020-07-31T12:21:09\"]");
		exceptionMap.put("dateTime.equalsOneOf", "[\"2020-07-17T12:21:09\",\"2020-07-31T12:21:09\"]");
		exceptionMap.put("emptyNameMultiValueField.equalsOneOf", "[\"1\", \"2\"]");
		exceptionMap.put("multivalueField.equals", "1");
		exceptionMap.put("lovWithoutAnnotation.containsOneOf", "[\"Task\", \"Operation\"]");
	}

	private void setProviders() {
		stringValueProvider = new StringValueProvider();
		booleanValueProvider = new BooleanValueProvider();
		dateValueProvider = new DateValueProvider();
		dateTimeValueProvider = new DateTimeValueProvider();
		longValueProvider = new LongValueProvider();
		bigDecimalValueProvider = new BigDecimalValueProvider();
		multisourceValueProvider = new MultisourceValueProvider();
		multiFieldValueProvider = new MultiFieldValueProvider();
		lovValueProvider = new LovValueProvider();
		providers.addAll(
				Arrays.asList(
						stringValueProvider,
						booleanValueProvider,
						dateValueProvider,
						dateTimeValueProvider,
						longValueProvider,
						bigDecimalValueProvider,
						multisourceValueProvider,
						multiFieldValueProvider,
						lovValueProvider
				)
		);
	}

}
