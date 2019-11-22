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

package io.tesler.core.controller.param.resolvers;

import static io.tesler.api.data.dictionary.DictionaryCache.dictionary;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.util.tz.TimeZoneSpecification;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


public class TimeZoneParameterArgumentResolver extends AbstractParameterArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return TimeZoneSpecification.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		if (!supportsParameter(parameter)) {
			return PageSpecification.DEFAULT;
		}
		return new TimeZoneSpecification(getTimezone(webRequest.getParameterMap()));
	}


	private LOV getTimezone(Map<String, ?> queryParametersMap) {
		String timezone = getParameterValue(queryParametersMap.get("_timezone"));
		String tzOffset = getParameterValue(queryParametersMap.get("_tzoffset"));

		if (isNotBlank(timezone)) {
			return new LOV(timezone);
		}

		if (tzOffset == null) {
			return null;
		}

		Set<String> zones = new LinkedHashSet<>();
		dictionary().getAll(DictionaryType.TIMEZONE).forEach(dict -> zones.add(dict.getKey()));
		zones.addAll(ZoneId.getAvailableZoneIds());

		for (String zoneId : zones) {
			if (isSuitableTZ(zoneId, NumberUtils.toInt(tzOffset, Integer.MAX_VALUE))) {
				return new LOV(zoneId);
			}
		}

		return null;
	}

	private boolean isSuitableTZ(String zoneId, Integer tzOffset) {
		ZoneOffset offset = ZoneId.of(zoneId).getRules().getOffset(Instant.now());
		return tzOffset.equals(offset.getTotalSeconds());
	}

}
