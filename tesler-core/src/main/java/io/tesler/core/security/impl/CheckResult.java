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

package io.tesler.core.security.impl;

import io.tesler.api.security.CheckDecision;
import io.tesler.api.security.ICheckResult;
import io.tesler.api.security.obligations.IObligationSet;
import io.tesler.core.security.impl.obligations.ObligationSet;
import java.util.Comparator;
import java.util.stream.Stream;


public class CheckResult implements ICheckResult {

	private final IObligationSet obligations = new ObligationSet();

	private CheckDecision decision;

	public CheckResult(CheckDecision decision) {
		this.decision = decision;
	}

	@Override
	public CheckDecision getDecision() {
		return decision;
	}

	@Override
	public IObligationSet getObligationSet() {
		return obligations;
	}

	@Override
	public ICheckResult merge(ICheckResult other) {
		decision = Stream.of(decision, other.getDecision())
				.min(Comparator.comparing(CheckDecision::ordinal)).get();
		obligations.merge(other.getObligationSet());
		return this;
	}

}
