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
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.dao.impl.MetadataUtils;
import io.tesler.core.test.util.TestResponseDto;
import io.tesler.core.util.SpringBeanUtils;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;
import io.tesler.core.util.filter.provider.impl.BigDecimalValueProvider;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.core.util.filter.provider.impl.DateTimeValueProvider;
import io.tesler.core.util.filter.provider.impl.DateValueProvider;
import io.tesler.core.util.filter.provider.impl.LongValueProvider;
import io.tesler.core.util.filter.provider.impl.MultiFieldValueProvider;
import io.tesler.core.util.filter.provider.impl.MultisourceValueProvider;
import io.tesler.core.util.filter.provider.impl.StringValueProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchSpecificationTest {

	Map<String, String> map;

	StringValueProvider stringValueProvider;

	BooleanValueProvider booleanValueProvider;

	DateValueProvider dateValueProvider;

	DateTimeValueProvider dateTimeValueProvider;

	LongValueProvider longValueProvider;

	BigDecimalValueProvider bigDecimalValueProvider;

	MultisourceValueProvider multisourceValueProvider;

	MultiFieldValueProvider multiFieldValueProvider;

	private final List<ClassifyDataProvider> providers = new ArrayList<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		BeanFactory mockedBeanFactory = mock(BeanFactory.class);
		SpringBeanUtils springBeanUtils = new SpringBeanUtils();
		springBeanUtils.setBeanFactory(mockedBeanFactory);
		when(SpringBeanUtils.getBean("teslerObjectMapper")).thenReturn(new ObjectMapper());
		stringValueProvider = new StringValueProvider();
		booleanValueProvider = new BooleanValueProvider();
		dateValueProvider = new DateValueProvider();
		dateTimeValueProvider = new DateTimeValueProvider();
		longValueProvider = new LongValueProvider();
		bigDecimalValueProvider = new BigDecimalValueProvider();
		multisourceValueProvider = new MultisourceValueProvider();
		multiFieldValueProvider = new MultiFieldValueProvider();
		providers.addAll(
				Arrays.asList(
						stringValueProvider,
						booleanValueProvider,
						dateValueProvider,
						dateTimeValueProvider,
						longValueProvider,
						bigDecimalValueProvider,
						multisourceValueProvider,
						multiFieldValueProvider
				)
		);

		map = new HashMap<>();
		map.put("_page", "1");
		map.put("_limit", "5");
		map.put("string.contains", "str");
		map.put("aBoolean.equals", "true");
		map.put("aLong.greaterOrEqualThan", "13");
		map.put("aLong.lessOrEqualThan", "33");
		map.put("bigDecimal.equals", "0");
		map.put("date.greaterOrEqualThan", "2020-07-17T12:21:09");
		map.put("date.lessOrEqualThan", "2020-07-31T12:21:09");
		map.put("dateTime.greaterOrEqualThan", "2020-07-10T12:23:45");
		map.put("dateTime.lessOrEqualThan", "2020-08-24T12:23:45");
		map.put("multiSource.equals", "str");
		map.put("multivalueField.equalsOneOf", "[\"1\"]");
	}

	@Test
	void searchSpecTest() {
		FilterParameters fp = FilterParameters.fromMap(map);
		Assertions.assertEquals(MetadataUtils.mapSearchParamsToPOJO(TestResponseDto.class, fp, providers).size(), 11);
	}

}
