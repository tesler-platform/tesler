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

package io.tesler.core.test.util;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.core.dto.multivalue.MultivalueField;
import io.tesler.core.util.filter.MultisourceSearchParameter;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.impl.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TestResponseDto extends DataResponseDTO {

	@Size(min = 10, message = "Minimal length is 10")
	private String validatedField;

	private Number number;

	@SearchParameter
	private String string;

	@SearchParameter(provider = BooleanValueProvider.class)
	private Boolean aBoolean;

	@SearchParameter(name = "multiValueFieldKey", provider = MultiFieldValueProvider.class)
	private MultivalueField multivalueField;

	@SearchParameter(provider = MultiFieldValueProvider.class)
	private MultivalueField emptyNameMultiValueField;

	@SearchParameter(provider = LongValueProvider.class)
	private Long aLong;

	@SearchParameter(provider = DateTimeValueProvider.class)
	private LocalDateTime dateTime;

	@SearchParameter(strict = true, provider = DateTimeValueProvider.class)
	private LocalDateTime strictDateTime;

	@SearchParameter(provider = DateValueProvider.class)
	private LocalDateTime date;

	@SearchParameter(strict = true, provider = DateValueProvider.class)
	private LocalDateTime strictDate;

	@SearchParameter(provider = BigDecimalValueProvider.class)
	private BigDecimal bigDecimal;

	@SearchParameter(suppressProcess = true)
	private String suppressProcessString;

	@MultisourceSearchParameter({
			@SearchParameter(name = "multisource1"),
			@SearchParameter(name = "multisource2"),
			@SearchParameter(name = "multisource3"),
			@SearchParameter(name = "multisource4")
	})
	private String multiSource;

	@SearchParameter(provider = LovValueProvider.class)
	@Lov(DictionaryType.TASK_TYPE)
	private String lov;

	@SearchParameter(provider = LovValueProvider.class)
	private String lovWithoutAnnotation;
}
