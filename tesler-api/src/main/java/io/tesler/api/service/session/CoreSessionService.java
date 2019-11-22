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

package io.tesler.api.service.session;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.security.core.Authentication;


public interface CoreSessionService {

	String SERVICE_NAME = "coreSessionService";

	String getSessionId();

	TeslerUserDetails getSessionUserDetails(boolean raiseError);

	TeslerUserDetails getAuthenticationDetails(Authentication authentication);

	Long getSessionUserId();

	String getSessionUserName();

	TimeZone getTimeZone(TimeZone defaultValue);

	ZoneId getZoneId(ZoneId defaultValue);

	Locale getLocale(Locale defaultValue);

}
