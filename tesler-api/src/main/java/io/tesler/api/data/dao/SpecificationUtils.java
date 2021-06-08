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

package io.tesler.api.data.dao;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;


@UtilityClass
public class SpecificationUtils {


	public static <T> Specifications<T> trueSpecification() {
		return Specifications.where((root, cq, cb) -> cb.and());
	}

	public static <T> Specifications<T> falseSpecification() {
		return Specifications.where((root, cq, cb) -> cb.or());
	}

	public static <T> BinaryOperator<Specification<T>> and() {
		return (s1, s2) -> Specifications.where(s1).and(s2);
	}

	public static <T> BinaryOperator<Specification<T>> or() {
		return (s1, s2) -> Specifications.where(s1).or(s2);
	}

	@SafeVarargs
	public static <T> Specification<T> and(Specification<T>... specs) {
		return Arrays.stream(specs).filter(Objects::nonNull).reduce(and()).orElse(trueSpecification());
	}

	@SafeVarargs
	public static <T> Specification<T> or(Specification<T>... specs) {
		return Arrays.stream(specs).filter(Objects::nonNull).reduce(or()).orElse(falseSpecification());
	}

}
