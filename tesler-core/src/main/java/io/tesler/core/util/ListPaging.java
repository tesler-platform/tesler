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

package io.tesler.core.util;

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.controller.param.SortParameter;
import io.tesler.core.controller.param.SortParameters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

@UtilityClass
public final class ListPaging {

	public static <T extends DataResponseDTO> ResultPage<T> getResultPage(final List<T> list,
			final QueryParameters queryParameters) {
		Stream<T> stream = list.stream();
		for (FilterParameter parameter : queryParameters.getFilter()) {
			stream = stream.filter(createFilter(parameter));
		}
		if (!queryParameters.getSort().isEmpty()) {
			stream = stream.sorted(createSorted(queryParameters.getSort()));
		}
		final List<T> filteredList = stream.collect(Collectors.toList());

		long from = (long) queryParameters.getPageNumber() * (long) queryParameters.getPageSize();
		long to = from + (long) queryParameters.getPageSize();
		if (to > filteredList.size()) {
			to = filteredList.size();
		}
		return new ResultPage<>(
				filteredList.subList((int) from, (int) to),
				filteredList.size() > to
		);
	}

	private static <T> Predicate<T> createFilter(FilterParameter parameter) {
		switch (parameter.getOperation()) {
			case CONTAINS:
				return new PredicateContains<>(parameter);
			case SPECIFIED:
				return new PredicateSpecified<>(parameter);
			case EQUALS:
				return new PredicateEquals<>(parameter);
			case GREATER_THAN:
				return new PredicateGreaterThan<>(parameter);
			case GREATER_OR_EQUAL_THAN:
				return new PredicateGreaterOrEqualThan<>(parameter);
			case LESS_THAN:
				return new PredicateLessThan<>(parameter);
			case LESS_OR_EQUAL_THAN:
				return new PredicateLessOrEqualThan<>(parameter);
			case EQUALS_ONE_OF:
				return new PredicateEqualsOneOf<>(parameter);
			case CONTAINS_ONE_OF:
				return new PredicateContainsOneOf<>(parameter);
		}
		throw new ServerException(String.format("Операция \"%s\" не поддерживается", parameter.getOperation()));
	}

	private static <T> Comparator<T> createSorted(SortParameters sort) {
		final List<SortParameter> sortedParameters = new ArrayList<>(sort.getParameters());
		sortedParameters.sort(ComparatorUtils.transformedComparator(
				ComparatorUtils.nullHighComparator(ComparatorUtils.<Integer>naturalComparator()),
				SortParameter::getPriority
		));
		Comparator[] comparators = new Comparator[sortedParameters.size()];
		for (int i = 0; i < sortedParameters.size(); i++) {
			comparators[i] = createSorted(sortedParameters.get(i));
		}
		return ComparatorUtils.chainedComparator(comparators);
	}

	private static Comparator createSorted(SortParameter parameter) {
		FieldTransformer fieldTransformer = new FieldTransformer(parameter.getName());
		switch (parameter.getType()) {
			case ASC:
				return ComparatorUtils.transformedComparator(
						ComparatorUtils.nullHighComparator(ComparatorUtils.<Integer>naturalComparator()),
						fieldTransformer
				);
			case DESC:
				return ComparatorUtils.transformedComparator(
						ComparatorUtils.nullHighComparator(
								ComparatorUtils.reversedComparator(ComparatorUtils.<Integer>naturalComparator())
						),
						fieldTransformer
				);
		}
		throw new ServerException(String.format("Сортировка \"%s\" не поддерживается", parameter.getType()));
	}

	private static Object getValue(final Object dto, final String fieldName) {
		try {
			return FieldUtils.getField(dto.getClass(), fieldName, true).get(dto);
		} catch (Exception e) {
			return null;
		}
	}

	@RequiredArgsConstructor
	private static final class FieldTransformer implements Transformer {

		private final String fieldName;

		@Override
		public Object transform(Object input) {
			return getValue(input, fieldName);
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateContains<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value instanceof String && StringUtils.containsIgnoreCase((String) value, parameter.getStringValue());
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateSpecified<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return BooleanUtils.isNotFalse(parameter.getBooleanValue()) ? Objects.nonNull(value) : Objects.isNull(value);
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateEquals<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value != null && Objects.equals(value, parameter.getValue(value.getClass()));
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateGreaterThan<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value instanceof Comparable && ((Comparable) value).compareTo(parameter.getValue(value.getClass())) > 0;
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateGreaterOrEqualThan<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value instanceof Comparable && ((Comparable) value).compareTo(parameter.getValue(value.getClass())) >= 0;
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateLessThan<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value instanceof Comparable && ((Comparable) value).compareTo(parameter.getValue(value.getClass())) < 0;
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateLessOrEqualThan<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			return value instanceof Comparable && ((Comparable) value).compareTo(parameter.getValue(value.getClass())) <= 0;
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateEqualsOneOf<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			for (String stringValue : parameter.getStringValuesAsList()) {
				if (value != null && Objects.equals(value, TypeConverter.to(value.getClass(), stringValue))) {
					return true;
				}
			}
			return false;
		}

	}

	@RequiredArgsConstructor
	private static final class PredicateContainsOneOf<T> implements Predicate<T> {

		private final FilterParameter parameter;

		@Override
		public boolean test(T dto) {
			Object value = getValue(dto, parameter.getName());
			if (value instanceof String) {
				for (String stringValue : parameter.getStringValuesAsList()) {
					if (StringUtils.containsIgnoreCase((String) value, stringValue)) {
						return true;
					}
				}
			}
			return false;
		}

	}

}
