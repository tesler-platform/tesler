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

package io.tesler.core.diag.jdbc;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import java.sql.SQLException;
import java.util.Deque;
import java.util.LinkedList;
import org.springframework.stereotype.Component;

/**
 * Implements only some of the methods, since there are methods,
 * which are executed quite often and therefore provide
 * performance impact, e.g. onAfterResultSetGet
 */
@Component
public class ThreadLocalJdbcEventListener extends JdbcEventListener {

	private final ThreadLocal<Deque<JdbcEventListener>> eventListeners = ThreadLocal.withInitial(LinkedList::new);

	@Override
	public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
		eventListeners.get().forEach(eventListener ->
				eventListener.onAfterGetConnection(connectionInformation, e)
		);
	}

	public void addListener(JdbcEventListener eventListener) {
		eventListeners.get().addLast(eventListener);
	}

	public void removeListener(JdbcEventListener eventListener) {
		eventListeners.get().removeLastOccurrence(eventListener);
	}

	public JdbcMetricsCollector getMetricsCollector() {
		JdbcMetricsCollector collector = new JdbcMetricsCollector() {
			@Override
			public void close() {
				ThreadLocalJdbcEventListener.this.removeListener(this);
			}
		};
		this.addListener(collector);
		return collector;
	}

}
