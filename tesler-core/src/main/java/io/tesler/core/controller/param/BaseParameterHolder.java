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

package io.tesler.core.controller.param;

import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;


@ToString
public class BaseParameterHolder<T extends QueryParameter> implements ParameterHolder<T> {

	@Getter
	private final List<T> parameters;

	private final ParameterBuilder<T> builder;

	public BaseParameterHolder(List<T> parameters, ParameterBuilder<T> builder) {
		this.parameters = parameters;
		this.builder = builder;
	}

	@Override
	public final ParameterBuilder<T> getBuilder() {
		return builder;
	}

	@Override
	public final Iterator<T> iterator() {
		return parameters.iterator();
	}

	@Override
	public final boolean isEmpty() {
		return CollectionUtils.isEmpty(parameters);
	}

}
