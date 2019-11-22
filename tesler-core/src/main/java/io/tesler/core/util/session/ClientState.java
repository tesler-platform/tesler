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

package io.tesler.core.util.session;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;


@Component
public class ClientState {

	private static final String CLIENT_ID = "ClientId";

	private static final String NO_CLIENT = "NO_CLIENT";

	private static final String CLIENT_STATE = "clientState";

	public <T> T get(Object key) {
		return (T) Optional.ofNullable(getState()).map(s -> s.get(key)).orElse(null);
	}

	public <T> void set(Object key, T value) {
		Optional.ofNullable(getState()).ifPresent(s -> s.put(key, value));
	}

	public void clear() {
		Optional.ofNullable(getState()).ifPresent(Map::clear);
	}


	private Map<Object, Object> getState() {
		HttpServletRequest request = WebHelper.getCurrentRequest().orElse(null);
		ClientStorage storage = getStorage(request);
		if (storage == null) {
			return null;
		}
		return storage.getClientState(getClientId(request));
	}

	private String getClientId(HttpServletRequest request) {
		String clientId = request.getHeader(CLIENT_ID);
		if (clientId == null) {
			clientId = NO_CLIENT;
		}
		return clientId;
	}

	private ClientStorage getStorage(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		ClientStorage state = (ClientStorage) session.getAttribute(CLIENT_STATE);
		if (state == null) {
			state = new ClientStorage();
			session.setAttribute(CLIENT_STATE, state);
		}
		return state;
	}


	static class ClientStorage implements Serializable {

		Map<String, Map<Object, Object>> storage = new ConcurrentHashMap<>();

		private Map<Object, Object> getClientState(String clientId) {
			return storage.computeIfAbsent(clientId, s -> new ConcurrentHashMap<>());
		}

	}

}
