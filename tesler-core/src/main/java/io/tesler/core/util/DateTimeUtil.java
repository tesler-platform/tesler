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

import static lombok.AccessLevel.PRIVATE;

import io.tesler.api.exception.ServerException;
import io.tesler.api.util.tz.TimeZoneUtil;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class DateTimeUtil {

	public static final Pattern ISO_PATTERN = Pattern
			.compile("^(?<year>-?(?:[1-9][0-9]*)?[0-9]{4})-(?<month>1[0-2]|0[1-9])-" +
					"(?<day>3[01]|0[1-9]|[12][0-9])T(?<hour>2[0-3]|[01][0-9]):" +
					"(?<minute>[0-5][0-9]):(?<second>[0-5][0-9])(?<ms>\\.[0-9]+)?" +
					"(?<timezone>Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])?$");

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	public static String dateTimeToString(final LocalDateTime dateTime, final String pattern) {
		try {
			return dateTime.format(DateTimeFormatter.ofPattern(pattern));
		} catch (DateTimeException e) {
			log.warn(String.format("Ошибка обработки даты %s. Формат: %s", dateTime, pattern), e);
			throw new ServerException(String.format("Ошибка обработки даты %s. Формат: %s", dateTime, pattern));
		}
	}

	// todo: удалить
	public static String dateTimeToString(final LocalDateTime dateTime) {
		return dateTimeToString(dateTime, TIMESTAMP_FORMAT);
	}

	public static LocalDateTime stringToDateTime(final String dateTime) {
		if (dateTime == null) {
			return null;
		}
		// суть в следующем: мы в базе храним таймстэмпы в московском
		// часовом поясе, а из вне строки могут прилетать как угодно
		// поэтому считаем что если часовой пояс в строке не указан,
		// то это локальное время и мы его не пересчитываем, в противном
		// случае прикодим к локальному часовому поясу
		Matcher matcher = ISO_PATTERN.matcher(dateTime);
		if (!matcher.matches()) {
			log.warn(String.format("Дата %s не соответствует формату %s", dateTime, ISO_PATTERN.pattern()));
			throw new ServerException(String.format("Дата %s не соответствует формату %s", dateTime, ISO_PATTERN.pattern()));
		}
		if (matcher.group("timezone") == null) {
			return LocalDateTime.parse(dateTime);
		}
		return toLocalDateTime(OffsetDateTime.parse(dateTime).toInstant());
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(toInstant(localDateTime));
	}

	public static LocalDateTime toLocalDateTime(final Date date) {
		return toLocalDateTime(date.toInstant());
	}

	public static LocalDateTime toLocalDateTime(final Calendar cal) {
		return toLocalDateTime(cal.toInstant());
	}

	public static LocalDateTime toLocalDateTime(XMLGregorianCalendar calendar) {
		return toLocalDateTime(calendar.toGregorianCalendar());
	}

	public static LocalDateTime toLocalDateTime(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
		return toLocalDateTime(offsetDateTime.toInstant());
	}

	public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
		return toLocalDateTime(zonedDateTime.toInstant());
	}

	public static LocalDateTime toLocalDateTime(final java.sql.Date date) {
		return date.toLocalDate().atStartOfDay();
	}

	public static Instant toInstant(LocalDateTime localDateTime, ZoneId zoneId) {
		return toZonedDateTime(localDateTime, zoneId).toInstant();
	}

	public static Instant toInstant(LocalDateTime localDateTime) {
		return toInstant(localDateTime, ZoneId.systemDefault());
	}

	public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
		return localDateTime.atZone(zoneId);
	}

	public static LocalDateTime now() {
		return LocalDateTime.now();
	}

	public static LocalDateTime sessionNow() {
		return now().with(toSession());
	}

	public static TemporalAdjuster toSession() {
		return toSession(true);
	}

	public static TemporalAdjuster toSession(boolean tzAware) {
		return temporal -> tzAware ? toSession((LocalDateTime) temporal) : temporal;
	}

	public static LocalDateTime toSession(LocalDateTime localDateTime) {
		return switchZone(localDateTime, ZoneId.systemDefault(), TimeZoneUtil.getSessionZoneId());
	}

	public static LocalDateTime fromSession(LocalDateTime localDateTime) {
		return switchZone(localDateTime, TimeZoneUtil.getSessionZoneId(), ZoneId.systemDefault());
	}

	public static TemporalAdjuster fromSession() {
		return fromSession(true);
	}

	public static TemporalAdjuster fromSession(boolean tzAware) {
		return temporal -> tzAware ? fromSession((LocalDateTime) temporal) : temporal;
	}

	public static TemporalAdjuster asEndOfDay() {
		return temporal -> ((LocalDateTime) temporal).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
	}

	public static TemporalAdjuster asStartOfDay() {
		return temporal -> ((LocalDateTime) temporal).withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	public static LocalDateTime switchZone(LocalDateTime localDateTime, ZoneId from, ZoneId to) {
		return LocalDateTime.ofInstant(toInstant(localDateTime, from), to);
	}

	public static String toOracleSql(LocalDateTime localDateTime) {
		// todo: нужна ли такая точность?
		String stringValue = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"));
		return "TO_TIMESTAMP('" + stringValue + "', 'YYYY-MM-DD HH24:MI:SS.FF9')";
	}

	public static int getNumberMonthByName(String key) {
		TemporalAccessor accessorMonth = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH)
				.parse(key.substring(0, 3));
		return accessorMonth.get(ChronoField.MONTH_OF_YEAR);
	}

	public static TemporalAdjuster asStartOfQ1() {
		return temporal -> ((LocalDateTime) temporal).withMonth(1).withDayOfMonth(1).with(asStartOfDay());
	}

	public static TemporalAdjuster asEndOfQ1() {
		return temporal -> ((LocalDateTime) temporal).withMonth(3).withDayOfMonth(31).with(asEndOfDay());
	}

	public static TemporalAdjuster asStartOfQ2() {
		return temporal -> ((LocalDateTime) temporal).withMonth(4).withDayOfMonth(1).with(asStartOfDay());
	}

	public static TemporalAdjuster asEndOfQ2() {
		return temporal -> ((LocalDateTime) temporal).withMonth(6).withDayOfMonth(30).with(asEndOfDay());
	}

	public static TemporalAdjuster asStartOfQ3() {
		return temporal -> ((LocalDateTime) temporal).withMonth(7).withDayOfMonth(1).with(asStartOfDay());
	}

	public static TemporalAdjuster asEndOfQ3() {
		return temporal -> ((LocalDateTime) temporal).withMonth(9).withDayOfMonth(30).with(asEndOfDay());
	}

	public static TemporalAdjuster asStartOfQ4() {
		return temporal -> ((LocalDateTime) temporal).withMonth(10).withDayOfMonth(1).with(asStartOfDay());
	}

	public static TemporalAdjuster asEndOfQ4() {
		return temporal -> ((LocalDateTime) temporal).withMonth(12).withDayOfMonth(31).with(asEndOfDay());
	}

	public static TemporalAdjuster asStartOfFirstHalfYear() {
		return temporal -> temporal.with(asStartOfQ1());
	}

	public static TemporalAdjuster asEndOfFirstHalfYear() {
		return temporal -> temporal.with(asEndOfQ2());
	}

	public static TemporalAdjuster asStartOfSecondHalfYear() {
		return temporal -> temporal.with(asStartOfQ3());
	}

	public static TemporalAdjuster asEndOfSecondHalfYear() {
		return temporal -> temporal.with(asEndOfQ4());
	}

	public static boolean isMonthYearAfterDate(String month, String year, LocalDateTime createdDateTime) {
		LocalDateTime checkDay = createdDateTime.with(DateTimeUtil.asStartOfDay());
		LocalDateTime constructedDateTime = checkDay;
		if (StringUtils.isNotBlank(year)) {
			constructedDateTime = constructedDateTime.withYear(Integer.parseInt(year));
		}
		if (StringUtils.isNotBlank(month)) {
			constructedDateTime = constructedDateTime.withMonth(getNumberMonthByName(month));
		}
		return constructedDateTime.isAfter(checkDay);
	}

	public static boolean isSameDay(final LocalDateTime dateTime1, final LocalDateTime dateTime2) {
		if (dateTime1 == null || dateTime2 == null) {
			return false;
		}
		return dateTime1.toLocalDate().isEqual(dateTime2.toLocalDate());
	}

}
