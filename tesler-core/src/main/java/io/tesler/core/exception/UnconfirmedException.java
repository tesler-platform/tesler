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

package io.tesler.core.exception;

import io.tesler.core.dto.PreInvokeEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class UnconfirmedException extends RuntimeException {

	private List<PreInvokeEvent> preInvokeEvents = new ArrayList<>();

	public UnconfirmedException addPreInvokeEvent(PreInvokeEvent preInvokeEvent) {
		this.preInvokeEvents.add(preInvokeEvent);
		return this;
	}

	public UnconfirmedException addPreInvokeEvents(List<PreInvokeEvent> preInvokeEvents) {
		this.preInvokeEvents.addAll(preInvokeEvents);
		return this;
	}

}
