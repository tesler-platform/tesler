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

package io.tesler.sqlbc.export.sql.transform;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewId implements Transformation {

	private final Supplier<BigDecimal> idSupplier;

	private final Map<BigDecimal, BigDecimal> values = new HashMap<>();

	@Override
	public Object transform(Object value) {
		if (value instanceof BigDecimal) {
			final BigDecimal bigDecimal = (BigDecimal) value;
			if (!values.containsKey(bigDecimal)) {
				values.put(bigDecimal, idSupplier.get());
			}
			return values.get(value);
		}
		return value;
	}

}
