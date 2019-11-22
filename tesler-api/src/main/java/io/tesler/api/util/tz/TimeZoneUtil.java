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

package io.tesler.api.util.tz;

import io.tesler.api.data.dto.TZAware;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;


public class TimeZoneUtil {

	public static final String SUFFIX = "_tzaware";

	public static boolean isTzAware(JsonGenerator generator) {
		JsonStreamContext context = generator.getOutputContext();
		if (isTzAware(context)) {
			return true;
		}
		return context != null && isTzAware(context.getParent());
	}

	public static boolean isTzAware(JsonStreamContext context) {
		if (context == null) {
			return false;
		}
		Object currentValue = context.getCurrentValue();
		if (currentValue instanceof FieldDTO) {
			return ((FieldDTO) currentValue).isTzAware();
		}
		return false;
	}

	public static boolean isTzAware(BeanProperty property) {
		if (property == null) {
			return false;
		}
		TZAware annotation = property.getAnnotation(TZAware.class);
		return annotation != null || hasTzAwareSuffix(property.getName());
	}

	public static boolean hasTzAwareSuffix(String string) {
		return StringUtils.isNotBlank(string) && string.toLowerCase().endsWith(SUFFIX);
	}

	public static LocalDateTime toLocalDateTime(final Date date) {
		return toLocalDateTime(date.toInstant());
	}

	public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
		return toLocalDateTime(zonedDateTime.toInstant());
	}

	public static LocalDateTime toLocalDateTime(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
		return toLocalDateTime(offsetDateTime.toInstant());
	}

	public static LocalDateTime switchZone(LocalDateTime localDateTime, ZoneId from, ZoneId to) {
		return LocalDateTime.ofInstant(toInstant(localDateTime, from), to);
	}

	public static Instant toInstant(LocalDateTime localDateTime, ZoneId zoneId) {
		return toZonedDateTime(localDateTime, zoneId).toInstant();
	}

	public static Instant toInstant(LocalDateTime localDateTime) {
		return toInstant(localDateTime, ZoneId.systemDefault());
	}

	public static Instant toInstant(Date date) {
		return date.toInstant();
	}

	public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
		return localDateTime.atZone(zoneId);
	}

	public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
		return toZonedDateTime(localDateTime, ZoneId.systemDefault());
	}

	public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
		return toZonedDateTime(localDateTime, zoneId).toOffsetDateTime();
	}

	public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
		return toOffsetDateTime(localDateTime, ZoneId.systemDefault());
	}

	public static TemporalAdjuster fromTimeZone(ZoneId zoneId) {
		return fromTimeZone(true, zoneId);
	}

	public static TemporalAdjuster fromTimeZone(boolean tzAware, ZoneId zoneId) {
		return temporal -> tzAware ? fromTimeZone((LocalDateTime) temporal, zoneId) : temporal;
	}

	public static LocalDateTime fromTimeZone(LocalDateTime localDateTime, ZoneId zoneId) {
		return switchZone(localDateTime, zoneId, ZoneId.systemDefault());
	}

	public static TemporalAdjuster toTimeZone(ZoneId zoneId) {
		return toTimeZone(true, zoneId);
	}

	public static TemporalAdjuster toTimeZone(boolean tzAware, ZoneId zoneId) {
		return temporal -> tzAware ? toTimeZone((LocalDateTime) temporal, zoneId) : temporal;
	}

	public static LocalDateTime toTimeZone(LocalDateTime localDateTime, ZoneId zoneId) {
		return switchZone(localDateTime, ZoneId.systemDefault(), zoneId);
	}

	public static TimeZone getSessionTimeZone() {
		return LocaleContextHolder.getTimeZone();
	}

	public static ZoneId getSessionZoneId() {
		return getSessionTimeZone().toZoneId();
	}


}
