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
import io.tesler.core.dto.DTOUtils;
import io.tesler.core.ui.field.IRequiredFieldsSupplier;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.util.session.SessionService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgets_;
import io.tesler.model.ui.entity.Widget;
import io.tesler.model.ui.entity.Widget_;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BcUtils {

	private final InnerBcTypeAware innerBcTypeAware;

	private final JpaDao jpaDao;

	private final BcRegistry bcRegistry;

	private final SessionService sessionService;

	private final Optional<List<IRequiredFieldsSupplier>> requiredFieldsSuppliers;

	private final LoadingCache<String, Set<BcField>> bcFields = CacheBuilder
			.newBuilder()
			.build(new BcFieldCacheLoader());

	private final LoadingCache<Long, Set<BcField>> widgetFields = CacheBuilder
			.newBuilder()
			.build(new WidgetFieldCacheLoader());

	private final LoadingCache<String, Map<String, Set<BcField>>> viewFields = CacheBuilder
			.newBuilder()
			.build(new ViewFieldCacheLoader());

	private LoadingCache<Class<? extends DataResponseDTO>, Set<DtoField<DataResponseDTO, ?>>> dtoFieldsCache = CacheBuilder
			.newBuilder()
			.build(new DtoFieldCacheLoader());

	public void invalidateFieldCache() {
		bcFields.invalidateAll();
		widgetFields.invalidateAll();
		viewFields.invalidateAll();
	}

	public void invalidateFieldCacheByView(final String viewName) {
		viewFields.invalidate(viewName);
	}

	public void invalidateFieldCacheByWidget(final Long widgetId) {
		widgetFields.invalidate(widgetId);
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

	public void invalidateFieldCacheByBc(final String bc) {
		bcFields.invalidate(bc);
		jpaDao.getList(
				Widget.class,
				Long.class,
				(root, cb) -> root.get(Widget_.id),
				(root, query, cb) -> cb.equal(root.get(Widget_.bc), bc)
		).forEach(this::invalidateFieldCacheByWidget);
	}

	/**
	 * Returns a set of dto fields ({@link DtoField}) for the given dto class
	 */
	@SneakyThrows
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFields(final Class<D> dtoClass) {
		return dtoFieldsCache.get((Class) dtoClass);
	}

	/**
	 * Returns a set of dto fields ({@link DtoField}) for the given business component
	 */
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFields(final BcIdentifier bcIdentifier) {
		final BcDescription bcDescription = bcRegistry.getBcDescription(bcIdentifier.getName());
		if (bcDescription instanceof InnerBcDescription) {
			try {
				final InnerBcDescription innerBcDescription = (InnerBcDescription) bcDescription;
				final Class dtoClass = innerBcTypeAware.getTypeOfDto(innerBcDescription);
				return dtoFieldsCache.get(dtoClass);
			} catch (ExecutionException e) {
				return Collections.emptySet();
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Returns a set of required fields for the given business component on the current screen
	 */
	@SneakyThrows
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "{#root.methodName, #bc.name}")
	public Set<String> getBcFieldsForCurrentScreen(final BcIdentifier bc) {
		final Set<String> viewFields = new HashSet<>();
		for (final String viewName : sessionService.getCurrentScreenViews()) {
			final Set<BcField> fields = this.viewFields.get(viewName).getOrDefault(bc.getName(), Collections.emptySet());
			for (final BcField field : fields) {
				viewFields.add(field.getName());
			}
		}
		return viewFields;
	}

	/**
	 * Returns a set of required dto fields ({@link DtoField}) for the given business component on the current screen
	 */
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "{#root.methodName, #bc.name}")
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFieldsForCurrentScreen(final BcIdentifier bc) {
		final Set<String> viewFields = getBcFieldsForCurrentScreen(bc);
		return getDtoFields(bc).stream()
				.filter(field -> viewFields.contains(field.getName()))
				.map(field -> (DtoField<D, ?>) field)
				.collect(Collectors.toSet());
	}

	/**
	 * Returns a set of required dto fields ({@link DtoField}) for the given business component on the current screen
	 */
	@Cacheable(cacheNames = {CacheConfig.REQUEST_CACHE}, key = "{#root.methodName, #bc.name, #dtoClass}")
	public <D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFieldsForCurrentScreen(
			final BcIdentifier bc,
			final Class<? extends DataResponseDTO> dtoClass) {
		final Set<String> viewFields = getBcFieldsForCurrentScreen(bc);
		return getDtoFields(dtoClass).stream()
				.filter(field -> viewFields.contains(field.getName()))
				.map(field -> (DtoField<D, ?>) field)
				.collect(Collectors.toSet());
	}

	public Set<String> getBcByDto(final Class<? extends DataResponseDTO> dtoClass) {
		return bcRegistry.select(InnerBcDescription.class)
				.filter(bcDescription -> Objects.equals(
						innerBcTypeAware.getTypeOfDto(bcDescription),
						dtoClass
				))
				.map(BcDescription::getName)
				.collect(Collectors.toSet());
	}

	private class DtoFieldCacheLoader<D extends DataResponseDTO> extends
			CacheLoader<Class<D>, Set<DtoField<D, ?>>> {

		@Override
		@SneakyThrows
		public Set<DtoField<D, ?>> load(final Class<D> dtoClass) {
			return DTOUtils.getAllFields(dtoClass);
		}

	}

	private final class BcFieldCacheLoader extends CacheLoader<String, Set<BcField>> {

		@Override
		public Set<BcField> load(final String bc) {
			final Set<BcField> fields = new HashSet<>();
			requiredFieldsSuppliers.ifPresent(suppliers -> suppliers
					.forEach(supplier -> fields.addAll(supplier.getRequiredFields(bc))));
			return fields;
		}

	}

	private final class WidgetFieldCacheLoader extends CacheLoader<Long, Set<BcField>> {

		@Override
		@SneakyThrows
		public Set<BcField> load(final Long widgetId) {
			final Widget widget = jpaDao.findById(Widget.class, widgetId);
			final Set<BcField> fields = new HashSet<>(WidgetUtils.extractAllFields(widget));
			final Set<String> bcNames = fields.stream().map(BcField::getBc).filter(Objects::nonNull)
					.collect(Collectors.toSet());
			for (String bcName : bcNames) {
				fields.addAll(bcFields.get(bcName));
			}
			return fields;
		}

	}

	private final class ViewFieldCacheLoader extends CacheLoader<String, Map<String, Set<BcField>>> {

		@Override
		@SneakyThrows
		public Map<String, Set<BcField>> load(final String viewName) {
			final List<Long> widgetIds = jpaDao.getList(
					ViewWidgets.class,
					Long.class,
					(root, cb) -> root.get(ViewWidgets_.widget).get(Widget_.id),
					(root, query, cb) -> cb.equal(root.get(ViewWidgets_.viewName), viewName)
			);
			final Set<BcField> fields = new HashSet<>();
			for (final Long widgetId : widgetIds) {
				fields.addAll(widgetFields.get(widgetId));
			}
			return fields.stream().collect(Collectors.groupingBy(BcField::getBc, Collectors.toSet()));
		}

	}

}
