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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;


@Component("connectionRegistry")
public class ConnectionRegistry {

	private Map<ConnectionInformation, ConnectionInfo> connections = new ConcurrentHashMap<>();

	public Collection<ConnectionInfo> getConnections() {
		return connections.values();
	}

	public void register(ConnectionInformation connectionInformation, ConnectionInfo connectionInfo) {
		connections.put(connectionInformation, connectionInfo);
	}

	public ConnectionInfo getConnectionInfo(ConnectionInformation connectionInformation) {
		return connections.get(connectionInformation);
	}

	public void remove(ConnectionInformation connectionInformation) {
		connections.remove(connectionInformation);
	}

}
