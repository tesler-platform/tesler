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

package io.tesler.core.dao.impl;

import io.tesler.api.data.Period;
import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.controller.param.SortParameter;
import io.tesler.core.controller.param.SortParameters;
import io.tesler.core.dao.ClassifyDataParameter;
import io.tesler.core.dto.LovUtils;
import io.tesler.core.util.filter.MultisourceSearchParameter;
import io.tesler.core.util.filter.SearchParameter;
import io.tesler.core.util.filter.provider.ClassifyDataProvider;
import io.tesler.core.util.filter.provider.impl.BooleanValueProvider;
import io.tesler.core.util.filter.provider.impl.MultisourceValueProvider;
import io.tesler.model.core.entity.BaseEntity;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.tesler.api.data.dictionary.DictionaryCache.dictionary;
import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.core.controller.param.SortType.ASC;
import static io.tesler.core.controller.param.SortType.DESC;


@Slf4j
@UtilityClass
public class MetadataUtils {

	public List<ClassifyDataParameter> mapSearchParamsToPOJO(Class dtoClazz, FilterParameters filterParameters, List<ClassifyDataProvider> providers) {

		List<ClassifyDataParameter> result = new ArrayList<>();

		filterParameters.forEach(filterParam -> {
					try {
						Field dtoField = Optional.ofNullable(ReflectionUtils.findField(dtoClazz, filterParam.getName()))
								.orElseThrow(
										() -> new IllegalArgumentException(
												errorMessage(
														"error.class_field_not_found",
														filterParam.getName(),
														dtoClazz.getName()
												)
										)
								);
						MultisourceSearchParameter multisourceParameter = dtoField
								.getDeclaredAnnotation(MultisourceSearchParameter.class);
						if (multisourceParameter != null) {
							providers.stream().filter(p -> p.getClass().equals(multisourceParameter.provider()))
									.findFirst()
									.ifPresent(
											dataProvider -> result.addAll(dataProvider.getClassifyDataParameters(dtoField, filterParam, null, providers))
									);
						} else {
							SearchParameter searchParam = Optional.ofNullable(dtoField.getDeclaredAnnotation(SearchParameter.class))
									.orElseThrow(
											() -> new IllegalArgumentException(
													errorMessage(
															"error.missing_search_parameter_annotation",
															filterParam.getName()
													)
											)
									);
							providers.stream().filter(p -> p.getClass().equals(searchParam.provider()))
									.findFirst()
									.ifPresent(
											dataProvider -> result.addAll(dataProvider.getClassifyDataParameters(dtoField, filterParam, searchParam, providers))
									);
						}
					} catch (Exception e) {
						log.warn(errorMessage("error.failed_to_parse_filter_param", filterParam), e);
					}

				}
		);
		return result;
	}

	public static boolean mayBeNull(Root<?> root, Path path) {
		Bindable model = path.getModel();
		BindableType type = model.getBindableType();
		if (type != BindableType.SINGULAR_ATTRIBUTE) {
			return true;
		}
		// джойн
		if (path.getParentPath() != root) {
			return true;
		}
		return !model.getBindableJavaType().isPrimitive();
	}

	public static Comparable requireComparable(Object value) {
		if (value instanceof Comparable) {
			return (Comparable) value;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static String requireString(Object value) {
		if (value instanceof String) {
			return (String) value;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static JoinType getJoinType(From from, String attrName) {
		JoinType joinType = JoinType.INNER;
		Bindable model = from.getModel();
		if (model.getBindableType() == BindableType.ENTITY_TYPE) {
			ManagedType managedType = (ManagedType) model;
			if (managedType.getAttribute(attrName).isAssociation()) {
				joinType = JoinType.LEFT;
			}
		}
		return joinType;
	}

	public static JoinType getJoinType(FetchParent fetch, String attrName) {
		// todo: похоже так всегда и бывает
		if (fetch instanceof From) {
			return getJoinType((From) fetch, attrName);
		}
		return JoinType.LEFT;
	}

	@SuppressWarnings("unchecked")
	public static Join joinEntity(From from, String attrName) {
		Set<Join> joins = from.getJoins();
		for (Join join : joins) {
			if (join.getAttribute().getName().equals(attrName)) {
				return join;
			}
		}
		return from.join(attrName, getJoinType(from, attrName));
	}

	public static Path getFieldPath(String field, Root<?> root) {
		Path result;
		if (field.contains(".")) {
			String[] fieldArr = field.split("\\.");
			From partialFrom = root;
			for (int i = 0; i < fieldArr.length - 1; i++) {
				partialFrom = joinEntity(partialFrom, fieldArr[i]);
			}
			result = partialFrom.get(fieldArr[fieldArr.length - 1]);
		} else {
			result = root.get(field);
		}
		return result;
	}

	public static Predicate createPredicate(Root<?> root, ClassifyDataParameter criteria, CriteriaBuilder cb) {
		try {
			Object value = criteria.getValue();

			Path field = getFieldPath(criteria.getField(), root);

			switch (criteria.getOperator()) {
				case EQUALS:
					if (value instanceof String) {
						return cb.equal(cb.upper(field), requireString(value).toUpperCase());
					} else {
						return cb.equal(field, value);
					}
				case CONTAINS:
					return cb.like(cb.upper(field), "%" + requireString(value).toUpperCase() + "%");
				case GREATER_THAN:
					return cb.greaterThan(field, requireComparable(value));
				case LESS_THAN:
					return cb.lessThan(field, requireComparable(value));
				case GREATER_OR_EQUAL_THAN:
					return cb.greaterThanOrEqualTo(field, requireComparable(value));
				case LESS_OR_EQUAL_THAN:
					return cb.lessThanOrEqualTo(field, requireComparable(value));
				case INTERVALS:
					return cb.or(((List<Period>) value).stream()
							.map(object ->
									cb.and(
											cb.greaterThanOrEqualTo(field, requireComparable(object.getStart())),
											cb.lessThanOrEqualTo(field, requireComparable(object.getEnd()))
									))
							.toArray(Predicate[]::new));
				case SPECIFIED:
					boolean isSpecified = BooleanUtils.isTrue((Boolean) value);
					if (BooleanValueProvider.class.equals(criteria.getProvider())) {
						return isSpecified ?
								cb.equal(field, true) :
								mayBeNull(root, field) ?
										cb.or(cb.isNull(field), cb.equal(field, false)) :
										cb.equal(field, false);
					} else {
						return isSpecified ?
								cb.isNotNull(field) :
								cb.isNull(field);
					}
				case EQUALS_ONE_OF:
					if (((List<Object>) value).stream().allMatch(((s) -> s instanceof String))) {
						return cb.or(((List<Object>) value).stream()
								.map(object -> cb.equal(cb.upper(field), requireString(object).toUpperCase()))
								.toArray(Predicate[]::new));
					} else {
						return cb
								.or(((List<Object>) value).stream().map(object -> cb.equal(field, object)).toArray(Predicate[]::new));
					}
				case CONTAINS_ONE_OF:
					return cb.or(((List<Object>) value)
							.stream()
							.map(object -> cb
									.like(cb.upper(field), "%".concat(requireString(object).toUpperCase()).concat("%")))
							.toArray(Predicate[]::new));
				default:
					throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			log.warn("error when try to parse search expr: "
					+ criteria.getField() + "." + criteria.getOperator() + "=" + criteria.getValue(), e);
			return null;
		}
	}

	public static <T> void addSorting(final Class dtoClazz, final Root<?> root, final CriteriaQuery<T> query,
			CriteriaBuilder builder, final SortParameters sort) {
		List<Order> orderList = new ArrayList<>();
		if (!query.getOrderList().isEmpty()) {
			orderList.addAll(query.getOrderList());
		}
		List<SortParameter> sortedParams = sort.getParameters().stream()
				.sorted(Comparator.comparingInt(SortParameter::getPriority)).collect(Collectors.toList());
		for (SortParameter p : sortedParams) {
			try {
				String field = getSortField(dtoClazz, p);
				Path fieldPath = getFieldPath(field, root);
				IDictionaryType lovType = getLovType(dtoClazz, p);
				Expression<?> order;
				if (lovType != null) {
					Collection<SimpleDictionary> dictDTOS = dictionary().getAll(lovType);
					CriteriaBuilder.Case<String> selectCase = builder.selectCase();
					dictDTOS.forEach(dictDTO -> selectCase.when(
							builder.equal(fieldPath, new LOV(dictDTO.getKey())), dictDTO.getValue()
					));
					order = selectCase.otherwise("");
				} else {
					order = fieldPath;
				}
				if (ASC.equals(p.getType())) {
					orderList.add(builder.asc(order));
				} else if (DESC.equals(p.getType())) {
					orderList.add(builder.desc(order));
				}
			} catch (Exception e) {
				log.warn("Не удалось распарсить параметр сортировки для класса " + dtoClazz.getName(), e);
			}
		}
		if (BaseEntity.class.isAssignableFrom(root.getJavaType())) {
			orderList.add(builder.desc(root.get("id")));
		}
		query.orderBy(orderList);
	}

	private static String getSortField(Class dtoClazz, SortParameter parameter) {
		String field;
		if (dtoClazz == null) {
			field = parameter.getName();
		} else {
			Field dtoField = ReflectionUtils.findField(dtoClazz, parameter.getName());
			if (dtoField == null) {
				throw new IllegalArgumentException(
						"Не найдено поле " + parameter.getName() + " в классе " + dtoClazz.getName());
			}
			SearchParameter fieldParameter = dtoField.getDeclaredAnnotation(io.tesler.core.util.filter.SearchParameter.class);
			if (fieldParameter != null && !"".equals(fieldParameter.name())) {
				field = fieldParameter.name();
			} else {
				field = parameter.getName();
			}
		}
		return field;
	}

	private static IDictionaryType getLovType(Class dtoClazz, SortParameter parameter) {
		if (dtoClazz != null) {
			Field dtoField = ReflectionUtils.findField(dtoClazz, parameter.getName());
			if (dtoField == null) {
				throw new IllegalArgumentException(
						"Не найдено поле " + parameter.getName() + " в классе " + dtoClazz.getName());
			}
			return LovUtils.getType(dtoField);
		}
		return null;
	}

	public static Predicate getPredicateFromSearchParams(CriteriaBuilder cb, Root<?> root, Class dtoClazz,
			FilterParameters searchParams, List<ClassifyDataProvider> providers) {

		if (searchParams == null) {
			return cb.and();
		}
		List<ClassifyDataParameter> criteriaStrings = mapSearchParamsToPOJO(dtoClazz, searchParams, providers);
		return getAllSpecifications(cb, root, criteriaStrings);
	}

	public static Predicate getAllSpecifications(CriteriaBuilder cb, Root<?> root,
			List<ClassifyDataParameter> criteriaStrings) {
		return cb.and(criteriaStrings.stream()
				.map(criteria -> getSingleSpecification(cb, root, criteria))
				.filter(Objects::nonNull).toArray(Predicate[]::new));
	}

	private static Predicate getSingleSpecification(CriteriaBuilder cb, Root<?> root, ClassifyDataParameter criteria) {
		if (MultisourceValueProvider.class.equals(criteria.getProvider())) {
			List criteriaValue = (List) criteria.getValue();
			List<Predicate> predicates = new ArrayList<>();
			for (Object innerList : criteriaValue) {
				predicates.add(getAllSpecifications(cb, root, (List) innerList));
			}
			return cb.or(predicates.stream().filter(Objects::nonNull).toArray(Predicate[]::new));
		} else {
			return createPredicate(root, criteria, cb);
		}
	}

}
