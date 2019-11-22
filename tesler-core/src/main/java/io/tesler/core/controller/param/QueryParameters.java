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

import static io.tesler.core.controller.param.DateStep.DAY;

import io.tesler.api.data.PageSpecification;
import io.tesler.core.controller.param.resolvers.PageParameterArgumentResolver;
import io.tesler.core.util.DateTimeUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
public class QueryParameters {

	public static final String DATE_FROM = "_dateFrom";

	public static final String DATE_TO = "_date";

	public static final String DATE_STEP = "_dateStep";

	public static final String EXPORT = "_export";

	public static final String X_AXIS = "_xAxis";

	public static final String Y_AXIS = "_yAxis";

	public static final String PDQ = "_pdq";

	public static final String IS_FILTERABLE_DATA = "_isFilterableData";

	public static final String PRE_INVOKE_EVENT_KEYS = "_preInvokeEventKeys";

	public static final String DEBUG_MODE = "_debugMode";

	private final Map<String, String> parameters;

	private final LocalDateTime currentDate;

	public QueryParameters(Map<String, String> parameters) {
		this(parameters, DateTimeUtil.now());
	}

	public QueryParameters(Map<String, String> parameters, LocalDateTime currentDate) {
		this.parameters = new HashMap<>(parameters);
		this.currentDate = currentDate;
	}

	public static QueryParameters of(ParameterHolder<?> holder) {
		QueryParameters result = new QueryParameters(new HashMap<>());
		return result.store(holder);
	}

	public static QueryParameters onlyDatesQueryParameters(QueryParameters other) {
		Map<String, String> dates = new HashMap<>();
		dates.put(DATE_FROM, other.getParameter(DATE_FROM));
		dates.put(DATE_TO, other.getParameter(DATE_TO));
		return new QueryParameters(dates, other.currentDate);
	}

	public static QueryParameters emptyQueryParameters() {
		return new QueryParameters(new HashMap<>());
	}

	private static LocalDateTime parseDate(String stringValue, LocalDateTime defaultValue) {
		if (stringValue == null) {
			return defaultValue;
		}
		try {
			return DateTimeUtil.stringToDateTime(stringValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getExportType() {
		return getParameter(EXPORT);
	}

	public String getAxisX() {
		return getParameter(X_AXIS);
	}

	public String getAxisY() {
		return getParameter(Y_AXIS);
	}

	public boolean isFilterableData() {
		return "TRUE".equalsIgnoreCase(getParameter(IS_FILTERABLE_DATA));
	}

	public boolean isDebug() {
		return "TRUE".equalsIgnoreCase(getParameter(DEBUG_MODE));
	}

	public DateStep getDateStep() {
		return DateStep.of(getParameter(DATE_STEP), DAY);
	}

	public LocalDateTime getDateTo() {
		return parseDate(getParameter(DATE_TO), currentDate);
	}

	public LocalDateTime getDateFrom() {
		return parseDate(getParameter(DATE_FROM), currentDate);
	}

	public String getPdqName() {
		return getParameter(PDQ);
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public String setParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public String removeParameter(String name) {
		return parameters.remove(name);
	}

	public boolean removeParameter(String name, String value) {
		return parameters.remove(name, value);
	}

	public boolean removeParameter(QueryParameter parameter) {
		return parameter.apply(parameters::remove);
	}

	public PageSpecification getPage() {
		return PageParameterArgumentResolver.extract(parameters);
	}

	public FilterParameters getFilter() {
		return FilterParameters.fromMap(parameters);
	}

	public QueryParameters store(ParameterHolder<?> holder) {
		removeMatched(holder.getBuilder());
		holder.forEach(this::setParameter);
		return this;
	}

	public <T extends QueryParameter> ParameterHolder<T> of(ParameterBuilder<T> builder) {
		return new BaseParameterHolder<>(builder.buildParameters(parameters), builder);
	}

	public BindsParameters getBinds() {
		return BindsParameters.fromMap(parameters);
	}

	public SortParameters getSort() {
		return SortParameters.fromMap(parameters);
	}

	public List<String> getPreInvokeParameters() {
		return QueryParameter.getListValue(getParameter(PRE_INVOKE_EVENT_KEYS), String.class);
	}

	private void removeMatched(ParameterBuilder builder) {
		parameters.entrySet().removeIf(e -> builder.matches(e.getKey(), e.getValue()));
	}

	public String setParameter(QueryParameter parameter) {
		return parameter.apply(this::setParameter);
	}

	public int getPageNumber() {
		return Optional.ofNullable(getPage())
				.map(PageSpecification::getPageNo)
				.orElse(0);
	}

	public int getPageSize() {
		return Optional.ofNullable(getPage())
				.map(PageSpecification::getPageSize)
				.orElse(0);
	}

}
