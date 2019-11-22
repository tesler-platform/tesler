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

package io.tesler.core.diag;

import static io.tesler.api.system.SystemSettings.systemSettings;
import static io.tesler.core.util.SpringBeanUtils.getBean;

import io.tesler.api.data.dictionary.CoreDictionaries.SystemPref;
import io.tesler.api.service.session.CoreSessionService;
import io.tesler.core.diag.jdbc.JdbcMetricsCollector;
import io.tesler.core.diag.jdbc.ThreadLocalJdbcEventListener;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;


@Slf4j
public abstract class AbstractProfiler {

	protected boolean isEnabled() {
		return Optional.ofNullable(systemSettings())
				.map(s -> s.getBooleanValue(SystemPref.ENABLE_PROFILING))
				.orElse(false);
	}

	protected long getThreshold() {
		return Optional.ofNullable(systemSettings())
				.map(s -> s.getLongValue(SystemPref.PROFILING_TIME_TO_LOG, 1000L))
				.orElse(1000L);
	}

	protected String getSessionUserName() {
		return getBean(CoreSessionService.class).getSessionUserName();
	}

	protected ThreadLocalJdbcEventListener getJdbcEventListener() {
		return getBean(ThreadLocalJdbcEventListener.class);
	}

	protected Object profile(ProceedingJoinPoint pjp) throws Throwable {
		if (!isEnabled()) {
			return pjp.proceed();
		}
		try (JdbcMetricsCollector jdbcMetricsCollector = getJdbcEventListener().getMetricsCollector()) {
			MetricsCollector collector = new MetricsCollector(jdbcMetricsCollector, getSessionUserName());
			Object result = pjp.proceed();
			collector.log(pjp, getThreshold());
			return result;
		}
	}


}
