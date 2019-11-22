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

package io.tesler.core.service.spec;

import io.tesler.api.data.dictionary.LOV;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;


public abstract class SpecificationHolder<entity> {


	@Getter
	protected Class<? extends SpecificationHeader<entity>> specificationHeader;

	protected SpecificationHolder() {
		specificationHeader = null;
	}

	public SpecificationHeader<entity> fromLov(LOV lov) {
		return lov == null || lov.getKey() == null || !specificationHeader.isEnum() ?
				null :
				Stream.of(specificationHeader.getEnumConstants())
						.filter(header -> header.toLOV().equals(lov))
						.findFirst().orElse(null);
	}

	public List<SpecificationHeader<entity>> allValues() {
		return !specificationHeader.isEnum() ?
				Collections.emptyList() :
				new ArrayList<>(Arrays.asList(specificationHeader.getEnumConstants()));
	}

}
