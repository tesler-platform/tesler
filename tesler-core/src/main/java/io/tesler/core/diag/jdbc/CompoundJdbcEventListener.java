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
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements only some of the methods, since there are methods,
 * which are executed quite often and therefore provide
 * performance impact, e.g. onAfterResultSetGet
 */
public class CompoundJdbcEventListener extends JdbcEventListener {

	private final List<JdbcEventListener> eventListeners;

	public CompoundJdbcEventListener() {
		this(new ArrayList<>());
	}

	public CompoundJdbcEventListener(List<JdbcEventListener> eventListeners) {
		this.eventListeners = eventListeners;
	}

	public void addListener(JdbcEventListener listener) {
		eventListeners.add(listener);
	}

	@Override
	public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
		eventListeners.forEach(eventListener ->
				eventListener.onAfterGetConnection(connectionInformation, e)
		);
	}

	@Override
	public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
		eventListeners.forEach(eventListener ->
				eventListener.onAfterConnectionClose(connectionInformation, e)
		);
	}

	@Override
	public void onBeforeExecute(StatementInformation statementInformation, String sql) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecute(statementInformation, sql)
		);
	}

	@Override
	public void onBeforeExecute(PreparedStatementInformation statementInformation) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecute(statementInformation)
		);
	}

	@Override
	public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecuteUpdate(statementInformation)
		);
	}

	@Override
	public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecuteUpdate(statementInformation, sql)
		);
	}

	@Override
	public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecuteQuery(statementInformation, sql)
		);
	}

	@Override
	public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecuteQuery(statementInformation)
		);
	}

	@Override
	public void onBeforeExecuteBatch(StatementInformation statementInformation) {
		eventListeners.forEach(eventListener ->
				eventListener.onBeforeExecuteBatch(statementInformation)
		);
	}

}
