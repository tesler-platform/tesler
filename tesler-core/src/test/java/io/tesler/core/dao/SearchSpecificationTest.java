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

import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.test.util.TestResponseDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchSpecificationTest {

	Map<String, String> map;

	@BeforeEach
	void setUp() {
		map = new HashMap<>();
		map.put("_page", "1");
		map.put("_limit", "5");
		map.put("string.contains", "str");

	}

	@Test
	void searchSpecTest() {
		FilterParameters fp = FilterParameters.fromMap(map);
		Assertions.assertEquals(SearchParameterPOJO.mapSearchParamsToPOJO(TestResponseDto.class, fp).size(), 1);
	}

}
