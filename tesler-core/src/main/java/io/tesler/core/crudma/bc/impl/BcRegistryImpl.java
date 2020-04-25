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

package io.tesler.core.crudma.bc.impl;

import com.google.common.collect.Lists;
import io.tesler.api.service.tx.DeploymentTransactionSupport;
import io.tesler.core.crudma.bc.BcDescriptionBuilder;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BcOverrider;
import io.tesler.core.crudma.bc.BcOverrider.BcOverride;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.BcSupplier;
import io.tesler.core.crudma.bc.RefreshableBcSupplier;
import io.tesler.core.exception.ClientException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@DependsOn(DeploymentTransactionSupport.SERVICE_NAME)
@Service
public class BcRegistryImpl implements BcRegistry {

	private final List<BcSupplier> bcSuppliers;

	private final List<BcOverrider> bcOverriders;

	private Map<String, BcDescription> bcDescriptionMap;

	public BcRegistryImpl(Optional<List<BcSupplier>> bcSuppliers, Optional<List<BcOverrider>> bcOverriders) {
		this.bcSuppliers = bcSuppliers.orElse(Collections.emptyList());
		this.bcOverriders = bcOverriders.orElse(Collections.emptyList());
		build();
	}

	@Override
	public void refresh() {
		bcSuppliers.stream()
				.filter(RefreshableBcSupplier.class::isInstance)
				.map(RefreshableBcSupplier.class::cast)
				.forEach(RefreshableBcSupplier::refresh);
		build();
	}

	private void build() {
		Map<String, BcDescription> bcDescriptionMap = new HashMap<>();
		for (final BcSupplier bcSupplier : bcSuppliers) {
			for (final String bcName : bcSupplier.getAllBcNames()) {
				bcDescriptionMap.put(bcName, bcSupplier.getBcDescription(bcName));
			}
		}
		for (final BcOverrider bcOverrider : bcOverriders) {
			for (final BcOverride bcOverride : bcOverrider.getBcOverrides()) {
				overrideBc(bcDescriptionMap, bcOverride.getBcIdentifiers(), bcOverride.getServiceClass());
			}
		}
		this.bcDescriptionMap = Collections.unmodifiableMap(bcDescriptionMap);
	}

	@Override
	public <T> Stream<T> select(Predicate<BcDescription> predicate, Function<BcDescription, T> transformer) {
		return bcDescriptionMap.values().stream()
				.filter(predicate)
				.map(transformer);
	}

	@Override
	public Collection<String> getAllBcNames() {
		return bcDescriptionMap.keySet();
	}

	@Override
	public BcDescription getBcDescription(final String bcName) {
		final BcDescription bcDescription = bcDescriptionMap.get(bcName);
		if (bcDescription == null) {
			throw new ClientException(String.format("BC не найден [%s]", bcName));
		}
		return bcDescription;
	}

	@Override
	public String getUrlFromBc(final String bcName) {
		if (bcName == null) {
			return null;
		}
		return getBcHierarchy(bcName).stream()
				.map(BcDescription::getName)
				.collect(Collectors.joining("/:id/"));
	}

	@Override
	public List<BcDescription> getBcHierarchy(final String bcName) {
		final List<BcDescription> reverseHierarchy = new ArrayList<>();

		BcDescription bcDescription = getBcDescription(bcName);
		reverseHierarchy.add(bcDescription);
		while (bcDescription.getParentName() != null) {
			bcDescription = getBcDescription(bcDescription.getParentName());
			reverseHierarchy.add(bcDescription);
		}

		return Lists.reverse(reverseHierarchy);
	}

	private void overrideBc(
			final Map<String, BcDescription> bcDescriptionMap,
			final List<BcIdentifier> bcIdentifiers,
			final Class<?> serviceClass) {
		for (final BcIdentifier bcIdentifier : bcIdentifiers) {
			bcDescriptionMap.put(
					bcIdentifier.getName(),
					BcDescriptionBuilder.build(
							bcIdentifier.getName(),
							bcIdentifier.getParentName(),
							serviceClass,
							bcDescriptionMap.get(bcIdentifier.getName()).isRefresh()
					)
			);
		}
	}

}
