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

package io.tesler.core.crudma.impl.sql.utils;

import static io.tesler.core.controller.param.SearchOperation.CONTAINS;
import static io.tesler.core.controller.param.SearchOperation.EQUALS;
import static io.tesler.core.controller.param.SearchOperation.EQUALS_ONE_OF;
import static io.tesler.core.controller.param.SearchOperation.GREATER_OR_EQUAL_THAN;
import static io.tesler.core.controller.param.SearchOperation.GREATER_THAN;
import static io.tesler.core.controller.param.SearchOperation.LESS_OR_EQUAL_THAN;
import static io.tesler.core.controller.param.SearchOperation.LESS_THAN;
import static io.tesler.core.controller.param.SearchOperation.SPECIFIED;
import static io.tesler.core.controller.param.SearchOperation.SPECIFIED_BOOLEAN_SQL;
import static io.tesler.core.util.DateTimeUtil.asEndOfDay;
import static io.tesler.core.util.DateTimeUtil.asStartOfDay;
import static io.tesler.core.util.DateTimeUtil.fromSession;
import static lombok.AccessLevel.PRIVATE;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.util.tz.TimeZoneUtil;
import io.tesler.core.controller.param.BindParameter;
import io.tesler.core.controller.param.BindsParameters;
import io.tesler.core.controller.param.FilterParameter;
import io.tesler.core.controller.param.FilterParameters;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.controller.param.SearchOperation;
import io.tesler.core.controller.param.SortParameter;
import io.tesler.core.controller.param.SortParameters;
import io.tesler.core.crudma.bc.impl.SqlBcDescription;
import io.tesler.core.dto.data.SqlBcEditFieldDTO_;
import io.tesler.core.exception.ClientException;
import io.tesler.core.util.TypeConverter;
import io.tesler.core.util.session.SessionService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.orm.jpa.vendor.Database;

@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public final class SqlBcQuery {

	public static final String FIELD_ID = "id";

	public static final Map<String, SqlFieldType> EXTRA_FIELDS = new ImmutableMap.Builder<String, SqlFieldType>()
			.put(SqlBcEditFieldDTO_.edit_string1.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string2.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string3.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string4.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string5.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string6.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string7.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string8.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string9.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_string10.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_lov1.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_lov2.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_lov3.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_lov4.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_lov5.getName(), SqlFieldType.STRING)
			.put(SqlBcEditFieldDTO_.edit_number1.getName(), SqlFieldType.BIG_DECIMAL)
			.put(SqlBcEditFieldDTO_.edit_number2.getName(), SqlFieldType.BIG_DECIMAL)
			.put(SqlBcEditFieldDTO_.edit_number3.getName(), SqlFieldType.BIG_DECIMAL)
			.put(SqlBcEditFieldDTO_.edit_number4.getName(), SqlFieldType.BIG_DECIMAL)
			.put(SqlBcEditFieldDTO_.edit_number5.getName(), SqlFieldType.BIG_DECIMAL)
			.put(SqlBcEditFieldDTO_.edit_date1.getName(), SqlFieldType.TIME)
			.put(SqlBcEditFieldDTO_.edit_date2.getName(), SqlFieldType.TIME)
			.put(SqlBcEditFieldDTO_.edit_date3.getName(), SqlFieldType.TIME)
			.put(SqlBcEditFieldDTO_.edit_date4.getName(), SqlFieldType.TIME)
			.put(SqlBcEditFieldDTO_.edit_date5.getName(), SqlFieldType.TIME)
			.build();

	private static final Map<Database, String> PAGING_QUERY_MAP = new ImmutableMap.Builder<Database, String>()
			.put(Database.POSTGRESQL, "select row_.*, ROW_NUMBER() OVER () rownum_ from ("
					+ "select * from (%s) as q1 %s"
					+ ") row_ %s LIMIT :to OFFSET :from")
			.put(Database.ORACLE, "select s.* from (select row_.*, rownum rownum_ from ("
					+ "select * from (%s)%s"
					+ ") row_ where rownum <= :to %s) s where rownum_ > :from")
			.build();

	private static final Set<String> BOOLEAN_FILTER_TRUE_VALUES = Sets.newHashSet("TRUE", "T", "YES", "Y", "ON");

	private static final Set<String> BOOLEAN_FILTER_FALSE_VALUES = Sets.newHashSet("FALSE", "F", "NO", "N", "OFF");

	private final String query;

	private final String order;

	private final String filter;

	private final SqlParameterSource parameterSource;

	private final String bcName;

	private final Database database;

	public static Function<String, Object> getValueMapper(SqlBcDescription.Field field) {
		switch (field.getType()) {
			case STRING:
				return TypeConverter::toString;
			case BIG_DECIMAL:
				return TypeConverter::toBigDecimal;
			case BOOLEAN:
				return TypeConverter::toBoolean;
			case BYTE:
				return TypeConverter::toByte;
			case SHORT:
				return TypeConverter::toShort;
			case INTEGER:
				return TypeConverter::toInteger;
			case LONG:
				return TypeConverter::toLong;
			case FLOAT:
				return TypeConverter::toFloat;
			case DOUBLE:
				return TypeConverter::toDouble;
			case DATE:
			case TIMESTAMP:
				return field.isTzAware() ? TypeConverter::toSqlTimestampTzAware : TypeConverter::toSqlTimestamp;
			case TIME:
				return field.isTzAware() ? TypeConverter::toLocalDateTimeTzAware : TypeConverter::toLocalDateTime;
			default:
				throw new IllegalArgumentException("Unsupported type: " + field.getType());
		}

	}

	public static SqlBcQuery build(SessionService sessionService, SqlBcDescription bcDescription, String id,
			String parentId, QueryParameters queryParameters, Database database) {
		return new SqlBcQuery.Builder(sessionService, bcDescription, id, parentId, queryParameters, database).build();
	}

	public String pageQuery() {
		return String.format(
				PAGING_QUERY_MAP.get(database),
				getInnerSelect(query, bcName),
				StringUtils.isNotBlank(order) ? " order by " + order : "",
				filter
		);
	}

	public String countQuery() {
		return String.format("select count(*) from (%s) sqlbc where 1 = 1 %s", query, filter);
	}

	public String idQuery() {
		return String.format("select * from (%s) sqlbc where id = :id", getInnerSelect(query, bcName));
	}

	private String getInnerSelect(String query, String bcName) {
		StringBuilder builder = new StringBuilder();
		builder.append("select sqlbc.*");
		for (String extra : EXTRA_FIELDS.keySet()) {
			builder.append(" ,");
			builder.append("f.").append(extra);
		}
		builder.append(" from (").append(query).append(") sqlbc ");
		builder.append("left join sql_bc_edit_field f ");
		builder.append("on f.parent_id = sqlbc.id and f.bc_name = ");
		builder.append("\'").append(bcName).append("\'");
		return builder.toString();
	}

	public SqlParameterSource parameterSource() {
		return parameterSource;
	}

	private static final class Builder {

		private final SessionService sessionService;

		private final String query;

		private final StringBuilder order = new StringBuilder();

		private final StringBuilder filter = new StringBuilder();

		private final MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		private final String bcName;

		private final Database database;

		private Builder(SessionService sessionService, SqlBcDescription bcDescription, String id, String parentId,
				QueryParameters queryParameters, Database database) {
			this.sessionService = sessionService;
			query = bcDescription.getQuery();
			bcName = bcDescription.getName();
			this.database = database;
			fillParameterSource(id, parentId, queryParameters);
			fillOrder(bcDescription, queryParameters.getSort());
			fillFilter(bcDescription, queryParameters.getFilter());
			fillBinds(bcDescription, queryParameters.getBinds());
		}

		public SqlBcQuery build() {
			return new SqlBcQuery(query, order.toString(), filter.toString(), parameterSource, bcName, database);
		}

		private void fillParameterSource(String id, String parentId, QueryParameters queryParameters) {
			int from = queryParameters.getPageNumber() * queryParameters.getPageSize();
			int to = from + queryParameters.getPageSize() + 1;
			String userRole = Optional.ofNullable(sessionService.getSessionUserRole()).map(LOV::getKey).orElse("");
			parameterSource
					.addValue("userid", sessionService.getSessionUser().getId())
					.addValue("userrole", userRole)
					.addValue("userdeptid", sessionService.getCurrentScreenDepartment().getId())
					.addValue("isfilterabledata", queryParameters.isFilterableData() ? "Y" : "N")
					.addValue("parentid", parentId)
					.addValue("datefrom", Timestamp.valueOf(queryParameters.getDateFrom()))
					.addValue("dateto", Timestamp.valueOf(queryParameters.getDateTo()))
					.addValue("datefrom_tzware", Timestamp.valueOf(queryParameters.getDateFrom().with(fromSession())))
					.addValue("dateto_tzware", Timestamp.valueOf(queryParameters.getDateTo().with(fromSession())))
					.addValue("timezone", TimeZoneUtil.getSessionZoneId())
					.addValue("language", LocaleContextHolder.getLocale().getLanguage())
					.addValue("from", from)
					.addValue("to", to)
					.addValue("id", id);
		}

		private void fillBinds(SqlBcDescription bcDescription, BindsParameters bindParameters) {
			List<BindParameter> parameters = bindParameters.getParameters();
			bcDescription.getBinds().forEach(bind -> {
				Optional<BindParameter> parameterOptional = parameters.stream()
						.filter(p -> p.getSqlParameter().equals(bind.getBindName()))
						.findFirst();
				if (bind.isExistInQuery(query)) {
					if (parameterOptional.isPresent()) {
						fillFoundBind(parameterOptional.get());
					} else {
						parameterSource.addValue(bind.getBindName(), null);
					}
				}
			});
		}

		private void fillFoundBind(BindParameter parameter) {
			SearchOperation operation = parameter.getOperation();
			if (operation == null) {
				parameterSource.addValue(parameter.getName(), parameter.getStringValue());
				return;
			}
			switch (operation) {
				case EQUALS:
				case GREATER_THAN:
				case LESS_THAN:
				case GREATER_OR_EQUAL_THAN:
				case LESS_OR_EQUAL_THAN:
				case CONTAINS:
					parameterSource.addValue(parameter.getSqlParameter(), parameter.getStringValue());
					break;
				case EQUALS_ONE_OF:
				case CONTAINS_ONE_OF:
					parameterSource.addValue(parameter.getSqlParameter(), parameter.getStringValuesAsString());
					break;
				case SPECIFIED:
				case SPECIFIED_BOOLEAN_SQL:
					parameterSource.addValue(parameter.getSqlParameter(), parameter.getBooleanValue());
					break;
				default:
					log.error("Unknown operation " + operation);
			}
		}

		private void fillOrder(SqlBcDescription bcDescription, SortParameters sort) {
			if (sort != null && !sort.getParameters().isEmpty()) {
				List<SortParameter> sortedParams = sort.getParameters().stream()
						.sorted(Comparator.comparingInt(SortParameter::getPriority))
						.collect(Collectors.toList());
				sortedParams.forEach(parameter ->
						order.append(parameter.getName()).append(' ')
								.append(parameter.getType().name()).append(',')
				);
			} else if (StringUtils.isNotBlank(bcDescription.getDefaultOrder())) {
				order.append(bcDescription.getDefaultOrder()).append(",");
			}
			// добавляем сортировку по ID для стабильной пагинации
			order.append("id desc");
		}

		private void fillFilter(SqlBcDescription bcDescription, FilterParameters searchParameters) {
			int parameterNumber = 0;
			for (FilterParameter parameter : searchParameters) {
				SearchOperation operation = parameter.getOperation();
				SqlBcDescription.Field field = getField(bcDescription, parameter.getName());

				filter.append(" and ( ");
				if (CONTAINS == operation) {
					String parameterName = createParameterName(++parameterNumber);
					filter.append("upper(\"").append(field.getColumnName()).append("\") ");
					filter.append("like upper(").append(":").append(parameterName).append(") ");
					parameterSource.addValue(parameterName, "%" + parameter.getStringValue() + "%");
				} else if (EQUALS_ONE_OF == operation) {
					filter.append(" ( ");
					String prefix = field.getColumnName() + " IN (";
					String postfix = ") )";
					List<String> stringArrayList = new ArrayList<>();
					switch (field.getType()) {
						case TIMESTAMP:
							Iterator<LocalDateTime> dateValueAsList = parameter.getDateValueAsList().iterator();
							while (dateValueAsList.hasNext()) {
								filter.append("\"").append(field.getColumnName()).append("\"");

								LocalDateTime value = dateValueAsList.next();
								Timestamp startValue = Timestamp
										.valueOf(value.with(asStartOfDay()).with(fromSession(field.isTzAware())));
								Timestamp endValue = Timestamp
										.valueOf(value.with(asEndOfDay()).with(fromSession(field.isTzAware())));

								String parameterNameStart = createParameterName(++parameterNumber);
								filter.append(" >= :").append(parameterNameStart).append(" ");
								parameterSource.addValue(parameterNameStart, startValue);

								String parameterNameEnd = createParameterName(++parameterNumber);
								filter.append("AND ").append("\"").append(field.getColumnName()).append("\"")
										.append(" <= :")
										.append(parameterNameEnd).append(" ");
								parameterSource.addValue(parameterNameEnd, endValue);

								if (dateValueAsList.hasNext()) {
									filter.append(" ) OR (");
								} else {
									filter.append(" ) ");
								}
							}
							break;
						case STRING:
							for (String value : (parameter.getStringValuesAsList())) {
								String parameterName = createParameterName(++parameterNumber);
								stringArrayList.add(":" + parameterName);
								parameterSource.addValue(parameterName, value);
							}
							filter.append(prefix).append(StringUtils.join(stringArrayList, ",")).append(postfix);
							break;
						case BIG_DECIMAL:
							for (BigDecimal value : (parameter.getBigDecimalValuesAsList())) {
								String parameterName = createParameterName(++parameterNumber);
								stringArrayList.add(":" + parameterName);
								parameterSource.addValue(parameterName, value);
							}
							filter.append(prefix).append(StringUtils.join(stringArrayList, ",")).append(postfix);
							break;
						default:
							break;
					}
				} else {
					filter.append("\"").append(field.getColumnName()).append("\"");
					if (SPECIFIED == operation) {
						filter.append(
								BooleanUtils.isNotFalse(parameter.getBooleanValue()) ? " is not null " : " is null ");
					} else if (SPECIFIED_BOOLEAN_SQL == operation) {
						filter.append(BooleanUtils.isTrue(parameter.getBooleanValue()) ?
								String.format(
										" IS NOT NULL AND upper(%s) IN (%s) ",
										field.getColumnName(),
										"'" + String.join("','", BOOLEAN_FILTER_TRUE_VALUES) + "'"
								) :
								String.format(
										" IS NULL OR upper(%s) IN (%s) ",
										field.getColumnName(),
										"'" + String.join("','", BOOLEAN_FILTER_FALSE_VALUES) + "'"
								)
						);
					} else {
						if (EQUALS == operation) {
							filter.append(" = ");
						} else if (GREATER_THAN == operation) {
							filter.append(" > ");
						} else if (GREATER_OR_EQUAL_THAN == operation) {
							filter.append(" >= ");
						} else if (LESS_THAN == operation) {
							filter.append(" < ");
						} else if (LESS_OR_EQUAL_THAN == operation) {
							filter.append(" <= ");
						}
						String parameterName = createParameterName(++parameterNumber);
						filter.append(":").append(parameterName).append(" ");
						Function<String, Object> mapper = getValueMapper(field);
						parameterSource.addValue(parameterName, mapper.apply(parameter.getStringValue()));
					}
				}
				filter.append(" ) ");
			}
		}

		private String createParameterName(int number) {
			return "p_" + number;
		}

		private SqlBcDescription.Field getField(SqlBcDescription bcDescription, String fieldName) {
			return bcDescription.getFields().stream()
					.filter(f -> f.getFieldName().equals(fieldName))
					.findFirst()
					.orElseThrow(() -> new ClientException(String.format("Поле %s не существует", fieldName)));
		}

	}

}
