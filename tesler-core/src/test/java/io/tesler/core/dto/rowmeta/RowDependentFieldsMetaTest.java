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

package io.tesler.core.dto.rowmeta;

import com.google.common.collect.ImmutableList;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.constgen.DtoField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class RowDependentFieldsMetaTest {

	@InjectMocks
	RowDependentFieldsMeta<DataResponseDTO> rowDependentFieldsMeta;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testSetPlaceholder() {
		rowDependentFieldsMeta = new RowDependentFieldsMeta<>();
		DtoField<DataResponseDTO, FieldDTO> test = new DtoField<>("test");
		FieldDTO field = ImmutableList.<FieldDTO>builder()
				.add(FieldDTO.enabledField("test"))
				.build()
				.get(0);
		assertThat(field.getPlaceholder()).isNull();
		rowDependentFieldsMeta.add(field);
		rowDependentFieldsMeta.setPlaceholder(test, "placeholder");
		assertThat(field.getPlaceholder()).isEqualTo("placeholder");
	}
}
