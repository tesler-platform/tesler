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

package io.tesler.core.ui.field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.core.config.properties.WidgetFieldsIdResolverProperties;
import io.tesler.core.ui.model.json.field.FieldMeta;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

//TODO This test needs work
class PackageScanFieldIdResolverTest {

	@Mock
	WidgetFieldsIdResolverProperties widgetFieldsIdResolverProperties;

	@Mock
	Map<String, JavaType> typeMap;

	@Mock
	JavaType baseType;

	@InjectMocks
	PackageScanFieldIdResolver packageScanFieldIdResolver;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(widgetFieldsIdResolverProperties.getIncludePackages()).thenReturn(new String[0]);
		when(widgetFieldsIdResolverProperties.getExcludeClasses()).thenReturn(new String[0]);
	}

	@Test
	void testInit() {
		packageScanFieldIdResolver.init(baseType);
	}

	@Test
	void testFailedInit() {
		String[] value = {"io.tesler.core.ui.field"};
		baseType = new ObjectMapper().getTypeFactory().constructType(FieldMeta.class);
		when(widgetFieldsIdResolverProperties.getIncludePackages()).thenReturn(value);
		assertThrows(IllegalStateException.class, () -> packageScanFieldIdResolver.init(baseType));
	}

	@Test
	void testIdFromValue() {
		String result = packageScanFieldIdResolver.idFromValue("o");
		Assertions.assertEquals(null, result);
	}

	@Test
	void testIdFromBaseType() {
		String result = packageScanFieldIdResolver.idFromBaseType();
		Assertions.assertEquals(null, result);
	}

	@Test
	void testIdFromValueAndType() {
		String result = packageScanFieldIdResolver.idFromValueAndType("o", null);
		Assertions.assertEquals(null, result);
	}

	@Test
	void testTypeFromId() {
		assertThrows(IOException.class, () -> packageScanFieldIdResolver.typeFromId(null, "someFakeClasss"));
	}

	@Test
	void testGetDescForKnownTypeIds() {
		String result = packageScanFieldIdResolver.getDescForKnownTypeIds();
		Assertions.assertEquals(null, result);
	}

	@Test
	void testGetMechanism() {
		Id result = packageScanFieldIdResolver.getMechanism();
		Assertions.assertEquals(Id.CUSTOM, result);
	}

}
