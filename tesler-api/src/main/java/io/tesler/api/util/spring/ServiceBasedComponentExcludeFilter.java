/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.util.spring;

import io.tesler.api.util.ServiceUtils;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;


@EqualsAndHashCode(of = "filters")
public class ServiceBasedComponentExcludeFilter implements TypeFilter {

	private final Set<TypeFilter> filters;

	public ServiceBasedComponentExcludeFilter() {
		filters = ImmutableSet.<TypeFilter>builder().addAll(
				ServiceUtils.loadServices(ComponentExcludeFilter.class, this)
		).build();
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		for (TypeFilter filter : filters) {
			if (filter.match(metadataReader, metadataReaderFactory)) {
				return true;
			}
		}
		return false;
	}

}
