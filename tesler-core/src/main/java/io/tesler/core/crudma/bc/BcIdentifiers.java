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

package io.tesler.core.crudma.bc;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public final class BcIdentifiers implements Iterable<BcIdentifier> {

	private final Set<BcIdentifier> set;

	private BcIdentifiers(BcIdentifier... bcIdentifiers) {
		this.set = Stream.of(bcIdentifiers)
				.map(bcIdentifier -> new SimpleBcIdentifier(bcIdentifier.getName(), bcIdentifier.getParentName()))
				.collect(Collectors.toSet());
	}

	public static BcIdentifiers of(BcIdentifier... bcIdentifiers) {
		return new BcIdentifiers(bcIdentifiers);
	}

	public boolean contains(BcIdentifier bcIdentifier) {
		return set.contains(new SimpleBcIdentifier(bcIdentifier.getName(), bcIdentifier.getParentName()));
	}

	@Override
	public Iterator<BcIdentifier> iterator() {
		return set.iterator();
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private final class SimpleBcIdentifier implements BcIdentifier {

		private final String name;

		private final String parentName;

	}

}
