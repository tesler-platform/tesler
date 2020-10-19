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

package io.tesler.core.crudma.state.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.state.BcState;
import io.tesler.core.crudma.state.BcStateAware;
import io.tesler.core.util.session.WebHelper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link BcStateAware} that Uses HTTP servlet as state provider.
 * Uses a "ClientId" HTTP header as store key and HttpSession attribute as store
 *
 * @see BcStateAware
 * @see BcState
 * @see HttpSession
 */
@Component
public class BcStateAwareImpl implements BcStateAware {

	private static final String BC_STATE = "bcState";

	private static final String CLIENT_ID = "ClientId";

	private static final String NO_CLIENT = "NO_CLIENT";

	@Override
	public void clear() {
		ClientStorage storage = getStorage();
		Optional.ofNullable(getState(storage)).ifPresent(Map::clear);
		flushStorage(storage);
	}

	@Override
	public void set(BusinessComponent bc, BcState bcState) {
		ClientStorage storage = getStorage();
		Optional.ofNullable(getState(storage)).ifPresent(state -> state.put(BcKey.of(bc), bcState));
		flushStorage(storage);
	}

	@Override
	public BcState getState(BusinessComponent bc) {
		ClientStorage storage = getStorage();
		return Optional.ofNullable(getState(storage)).map(m -> m.get(BcKey.of(bc))).orElse(null);
	}

	@Override
	public boolean isPersisted(BusinessComponent bc) {
		ClientStorage storage = getStorage();
		return Optional.ofNullable(getState(storage)).map(m -> m.get(BcKey.of(bc))).map(BcState::isPersisted).orElse(true);
	}

	private Map<BcKey, BcState> getState(ClientStorage storage) {
		HttpServletRequest request = WebHelper.getCurrentRequest().orElse(null);
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

	private ClientStorage getStorage() {
		HttpSession session = getHttpSession();
		if (session == null) {
			return null;
		}
		ClientStorage state = (ClientStorage) session.getAttribute(BC_STATE);
		if (state == null) {
			state = new ClientStorage();
			session.setAttribute(BC_STATE, state);
		}
		return state;
	}

	private void flushStorage(ClientStorage storage) {
		HttpSession session = getHttpSession();
		if (session != null) {
			session.setAttribute(BC_STATE, storage);
		}
	}

	private HttpSession getHttpSession() {
		HttpServletRequest request = WebHelper.getCurrentRequest().orElse(null);
		if (request == null) {
			return null;
		}
		return request.getSession(false);
	}

	@EqualsAndHashCode
	@AllArgsConstructor
	private static class BcKey implements Serializable {

		private final String id;

		private final String name;

		private static BcKey of(BusinessComponent bc) {
			return new BcKey(bc.getId(), bc.getName());
		}

	}

	static class ClientStorage implements Serializable {

		Map<String, Map<BcKey, BcState>> storage = new ConcurrentHashMap<>();

		private Map<BcKey, BcState> getClientState(String clientId) {
			return storage.computeIfAbsent(clientId, s -> new ConcurrentHashMap<>());
		}

	}

}
