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

import com.google.common.collect.ImmutableMap;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class DiagJdbcEventListener extends JdbcEventListener {

	private static final Map<String, String> SESSION_ID_STATEMENTS = ImmutableMap.<String, String>builder()
			.put("Oracle", "SELECT SYS_CONTEXT('userenv', 'sessionid') AS sessionid FROM DUAL")
			.build();

	private final ConnectionRegistry connectionRegistry;

	private ConnectionInfo getConnectionInfo(ConnectionInformation connectionInformation) {
		return new ConnectionInfo(
				getConnectionId(connectionInformation),
				Thread.currentThread().getId(),
				Thread.currentThread().getStackTrace()
		);
	}

	@Override
	public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
		if (e != null) {
			return;
		}
		connectionRegistry.register(connectionInformation, getConnectionInfo(connectionInformation));
	}

	@SneakyThrows
	private String getConnectionId(ConnectionInformation connectionInformation) {
		Connection connection = connectionInformation.getConnection();
		String sessionIdStatement = SESSION_ID_STATEMENTS.get(connection.getMetaData().getDatabaseProductName());
		if (sessionIdStatement != null) {
			try (PreparedStatement statement = connection.prepareStatement(sessionIdStatement)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getString("sessionid");
					}
				}
			}
		}
		return String.format("%d", connectionInformation.getConnectionId());
	}

	@Override
	public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
		connectionRegistry.remove(connectionInformation);
	}

	@Override
	public void onBeforeExecute(StatementInformation statementInformation, String sql) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecute(PreparedStatementInformation statementInformation) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
		register(statementInformation);
	}

	@Override
	public void onBeforeExecuteBatch(StatementInformation statementInformation) {
		register(statementInformation);
	}

	private void register(StatementInformation statementInformation) {
		Optional.ofNullable(connectionRegistry.getConnectionInfo(statementInformation.getConnectionInformation()))
				.ifPresent(ci -> ci.setLastSqlStatement(statementInformation.getSql()));
	}

}
