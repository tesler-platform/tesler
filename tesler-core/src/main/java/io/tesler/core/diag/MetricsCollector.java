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

import io.tesler.core.diag.jdbc.JdbcMetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;


@Slf4j
class MetricsCollector {

	private final JdbcMetricsCollector jdbcMetricsCollector;

	private final String sessionUserName;

	private final long startTimeMs;

	MetricsCollector(JdbcMetricsCollector jdbcMetricsCollector, String sessionUserName) {
		this.jdbcMetricsCollector = jdbcMetricsCollector;
		this.sessionUserName = sessionUserName;
		startTimeMs = System.currentTimeMillis();
	}

	public void log(ProceedingJoinPoint pjp, long threshold) {
		long elapsedTime = System.currentTimeMillis() - startTimeMs;
		if (elapsedTime < threshold) {
			return;
		}
		StringBuilder args = new StringBuilder();
		int i = 1;
		for (Object arg : pjp.getArgs()) {
			args.append("\t").append(i++).append(" - ").append(arg);
		}
		log.info("Quanta Profiler - execution time: " + elapsedTime +
				" ms, connection time: " + (jdbcMetricsCollector.getConnectionTimeNs() / 1000000) + " ms at:" +
				"\tUser: " + sessionUserName +
				"\tPointcut: " + pjp.getSignature().toShortString() +
				"\tTarget: " + pjp.getTarget().getClass() +
				"\tParameters: " + args.toString());
	}

}
