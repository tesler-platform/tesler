/*-
 * #%L
 * IO Tesler - DTO Constant Generator
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

package io.tesler.constgen;

import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Constant implements Comparable<Constant> {

	public Constant(final String name, final TypeName type) {
		this.name = name;
		this.type = type;
		this.initializer = "null";
	}

	private final String name;

	private final TypeName type;

	private final String initializer;

	@Override
	public int compareTo(Constant o) {
		return this.name.compareTo(o.name);
	}

}
