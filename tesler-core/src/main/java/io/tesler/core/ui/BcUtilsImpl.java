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

package io.tesler.core.ui;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import io.tesler.core.bc.InnerBcTypeAware;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BcRegistry;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.ui.cache.BcFieldCache;
import io.tesler.core.ui.cache.DtoFieldCache;
import io.tesler.core.ui.cache.ViewFieldCache;
import io.tesler.core.ui.cache.WidgetFieldCache;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgets_;
import io.tesler.model.ui.entity.Widget_;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BcUtilsImpl implements BcUtils {

	private final InnerBcTypeAware innerBcTypeAware;

	private final JpaDao jpaDao;

	private final BcRegistry bcRegistry;

	private final SessionService sessionService;

	private final BcFieldCache bcFieldCache;

	private final DtoFieldCache dtoFieldCache;

	private final ViewFieldCache viewFieldCache;

	private final WidgetFieldCache widgetFieldCache;



	public void invalidateFieldCache() {
		bcFieldCache.bcFields.invalidateAll();
		widgetFieldCache.widgetFields.invalidateAll();
		viewFieldCache.viewFields.invalidateAll();
	}

	public void invalidateFieldCacheByView(final String viewName) {
		viewFieldCache.viewFields.invalidate(viewName);
	}

	public void invalidateFieldCacheByWidget(final Long widgetId) {
		widgetFieldCache.widgetFields.invalidate(widgetId);
		jpaDao.getList(
				ViewWidgets.class,
				String.class,
				(root, cb) -> root.get(ViewWidgets_.viewName),
				(root, query, cb) -> {
					query.distinct(true);
					return cb.equal(root.get(ViewWidgets_.widget).get(Widget_.id), widgetId);
				}
		).forEach(this::invalidateFieldCacheByView);
	}

	@SneakyThrows
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFields(final Class<D> dtoClass) {
		return dtoFieldCache.dtoFieldsCache.get((Class) dtoClass);
	}

	@SneakyThrows
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "{#root.methodName, #bc.name}")
	public Set<String> getBcFieldsForCurrentScreen(final BcIdentifier bc) {
		final Set<String> viewFields = new HashSet<>();
		for (final String viewName : sessionService.getCurrentScreenViews()) {
			final Set<BcField> fields = viewFieldCache.viewFields.get(viewName)
					.getOrDefault(bc.getName(), Collections.emptySet());
			for (final BcField field : fields) {
				viewFields.add(field.getName());
			}
		}
		return viewFields;
	}

	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "{#root.methodName, #bc.name}")
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFieldsForCurrentScreen(final BcIdentifier bc) {
		final Set<String> viewFields = getBcFieldsForCurrentScreen(bc);
		return getDtoFields(bc).stream()
				.filter(field -> viewFields.contains(field.getName()))
				.map(field -> (DtoField<D, ?>) field)
				.collect(Collectors.toSet());
	}

	private <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFields(final BcIdentifier bcIdentifier) {
		final BcDescription bcDescription = bcRegistry.getBcDescription(bcIdentifier.getName());
		if (bcDescription instanceof InnerBcDescription) {
			try {
				final InnerBcDescription innerBcDescription = (InnerBcDescription) bcDescription;
				final Class dtoClass = innerBcTypeAware.getTypeOfDto(innerBcDescription);
				return dtoFieldCache.dtoFieldsCache.get(dtoClass);
			} catch (ExecutionException e) {
				return Collections.emptySet();
			}
		}
		return Collections.emptySet();
	}

}
