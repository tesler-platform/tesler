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

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BcState {

	private static final String BC_STATE = "bcState";

	@Autowired
	private ClientState clientState;

	public void clear() {
		Optional.ofNullable(getState(false)).ifPresent(Map::clear);
	}

	public void set(BusinessComponent bc, DataResponseDTO dto, final CreationState creationState) {
		getState(true).put(BcKey.of(bc), new State(dto, creationState));
	}

	public DataResponseDTO get(BusinessComponent bc) {
		return Optional.ofNullable(getState(false)).map(m -> m.get(BcKey.of(bc))).map(State::getDto).orElse(null);
	}

	public State getState(BusinessComponent bc) {
		return Optional.ofNullable(getState(false)).map(m -> m.get(BcKey.of(bc))).orElse(null);
	}

	public CreationState getCreationState(BusinessComponent bc) {
		return Optional.ofNullable(getState(false)).map(m -> m.get(BcKey.of(bc))).map(State::getCreationState).orElse(null);
	}

	public boolean isNew(BusinessComponent bc) {
		return Optional.ofNullable(getState(false)).map(m -> m.get(BcKey.of(bc))).map(State::getCreationState).isPresent();
	}

	private Map<BcKey, State> getState(boolean create) {
		Map<BcKey, State> state = clientState.get(BC_STATE);
		if (state == null && create) {
			state = new ConcurrentHashMap<>();
			clientState.set(BC_STATE, state);
		}
		return state;
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

	@Getter
	@RequiredArgsConstructor
	public static final class State {

		private final DataResponseDTO dto;

		private final CreationState creationState;

	}

}
