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

package io.tesler.acl.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class ObligationSet implements IObligationSet {

	private final Set<IObligation> obligations = new HashSet<>();

	@Override
	public Set<IObligation> getObligations() {
		return Collections.unmodifiableSet(obligations);
	}

	@Override
	public IObligationSet addObligation(IObligation obligation) {
		obligations.add(obligation);
		return this;
	}

	@Override
	public IObligationSet merge(IObligationSet other) {
		Optional.ofNullable(other)
				.map(IObligationSet::getObligations)
				.map(obligations::addAll);
		return this;
	}

	@Override
	public boolean isEmpty() {
		return obligations.isEmpty();
	}

	@Override
	public List<String> getValues(IAttributeType attributeType) {
		return obligations.stream().filter(
				o -> attributeType.equals(o.getAttributeType())
		).flatMap(o -> o.getValues().stream()).collect(Collectors.toList());
	}

	@Override
	public boolean isApplicable(IAttributeType attributeType) {
		return obligations.stream().anyMatch(o -> attributeType.equals(o.getAttributeType()));
	}

}
