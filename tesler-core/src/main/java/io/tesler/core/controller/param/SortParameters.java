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

import io.tesler.core.controller.param.SortParameter.Builder;
import java.util.List;
import java.util.Map;


public class SortParameters extends BaseParameterHolder<SortParameter> {

	private SortParameters(List<SortParameter> parameters) {
		super(parameters, Builder.getInstance());
	}

	public static SortParameters fromMap(Map<String, String> map) {
		List<SortParameter> parameters = Builder.getInstance().buildParameters(map);
		return new SortParameters(parameters);
	}

	public static SortParameters fromList(List<SortParameter> list) {
		return new SortParameters(list);
	}

}
