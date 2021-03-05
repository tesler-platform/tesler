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

package io.tesler.core.crudma.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.core.util.DateTimeUtil.asStartOfDay;
import static io.tesler.core.util.DateTimeUtil.isSameDay;

import io.tesler.api.data.ResultPage;
import io.tesler.api.exception.ServerException;
import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.data.HistoricityDto;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.ActionType;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.VersionMismatchException;
import io.tesler.core.service.HistoricityKey;
import io.tesler.core.service.HistoricityKey.KeyAttribute;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.HistoricityFieldMetaBuilder;
import io.tesler.core.util.InstrumentationAwareReflectionUtils;
import io.tesler.model.core.entity.HistoricityEntity;
import io.tesler.model.core.entity.HistoricityEntity_;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.Collections;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;


public abstract class HistoricityResponseService<T extends HistoricityDto, E extends HistoricityEntity> extends
		VersionAwareResponseService<T, E> {

	private final Class<? extends HistoricityKey<E, T>> historicityKeyClass;

	public HistoricityResponseService(
			final Class<T> typeOfDTO,
			final Class<E> typeOfEntity,
			final Class<? extends HistoricityKey<E, T>> historicityKeyClass,
			final Class<? extends HistoricityFieldMetaBuilder<T>> metaBuilder) {
		super(typeOfDTO, typeOfEntity, null, metaBuilder);
		this.historicityKeyClass = historicityKeyClass;
	}

	private HistoricityKey<E, T> getHistoricityKey() {
		return applicationContext.getBean(historicityKeyClass);
	}

	@Override
	public final ResultPage<T> getList(final BusinessComponent bc) {
		final ResultPage<T> resultPage = super.getList(bc);
		if (resultPage.getResult().size() > 1) {
			throw new ServerException(errorMessage("error.duplicate_key"));
		}
		return resultPage;
	}

	@Override
	protected final Specification<E> getParentSpecification(final BusinessComponent bc) {
		return (root, query, cb) -> {
			final LocalDateTime redDate = bc.getParameters().getDateTo().with(asStartOfDay());
			return cb.and(
					getKeySpecification(bc).toPredicate(root, query, cb),
					cb.lessThanOrEqualTo(root.get(HistoricityEntity_.startDate), redDate),
					cb.or(
							root.get(HistoricityEntity_.endDate).isNull(),
							cb.greaterThanOrEqualTo(root.get(HistoricityEntity_.endDate), redDate)
					)
			);
		};
	}

	private Specification<E> getKeySpecification(final BusinessComponent bc) {
		return (root, query, cb) -> cb.and(getHistoricityKey().getAttributes().stream()
				.map(keyAttribute -> cb.equal(
						root.get(keyAttribute.getAttribute()),
						keyAttribute.getValueSupplier().get(bc)
				))
				.toArray(Predicate[]::new)
		);
	}

	@Override
	protected final CreateResult<T> doCreateEntity(final E entity, final BusinessComponent bc) {
		if (super.getList(bc).getResult().size() > 0) {
			throw new VersionMismatchException();
		}
		startEntity(
				entity,
				bc.getParameters().getDateTo(),
				findNextEntry(bc)
						.map(HistoricityEntity::getStartDate)
						.map(date -> date.minusDays(1).with(asEndOfDay()))
						.orElse(null)
		);
		fillKey(bc, entity);
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@SneakyThrows
	private void fillKey(final BusinessComponent bc, final E entity) {
		for (final KeyAttribute<E, T, ?> attribute : getHistoricityKey().getAttributes()) {
			PropertyUtils.setSimpleProperty(entity, attribute.getAttribute().getName(), attribute.getValueSupplier().get(bc));
		}
	}

	@Override
	public final ActionResultDTO<T> deleteEntity(final BusinessComponent bc) {
		final E entity = isExist(bc.getIdAsLong());
		if (isSameDay(bc.getParameters().getDateTo(), entity.getStartDate())) {
			baseDAO.delete(entity);
		} else {
			closeEntity(entity, bc.getParameters().getDateTo());
		}
		return new ActionResultDTO<>();
	}

	@Override
	protected final ActionResultDTO<T> doUpdateEntity(final E entity, final T data, final BusinessComponent bc) {
		final E entityForUpdate = getEntityForUpdate(entity, data, bc);
		update(entityForUpdate, data, bc);
		return new ActionResultDTO<>(entityToDto(bc, entityForUpdate)).setAction(PostAction.refreshBc(bc));
	}

	private E getEntityForUpdate(final E entity, final T data, final BusinessComponent bc) {
		if (isKeyChanged(data)) {
			return copyEntity(entity);
		} else if (isSameDay(bc.getParameters().getDateTo(), entity.getStartDate())) {
			return entity;
		} else {
			final E copy = copyEntity(entity);
			startEntity(copy, bc.getParameters().getDateTo(), entity.getEndDate());
			closeEntity(entity, bc.getParameters().getDateTo());
			return copy;
		}
	}

	@SneakyThrows
	protected E copyEntity(final E entity) {
		final E copy = typeOfEntity.newInstance();
		BeanUtils.copyProperties(
				entity,
				copy,
				InstrumentationAwareReflectionUtils.getAllNonSyntheticFieldsList(HistoricityEntity.class)
						.stream()
						.map(Field::getName)
						.toArray(String[]::new)
		);
		baseDAO.save(copy);
		return copy;
	}

	private boolean isKeyChanged(final T dto) {
		for (final KeyAttribute<E, T, ?> attribute : getHistoricityKey().getAttributes()) {
			for (final DtoField<T, ?> dtoField : attribute.getDtoFields()) {
				if (dto.isFieldChanged(dtoField)) {
					return true;
				}
			}
		}
		return false;
	}

	protected abstract void update(E e, T dto, BusinessComponent bc);

	protected ActionResultDTO<T> copy(BusinessComponent bc, T dto) {
		return null;
	}

	@Override
	public final Actions<T> getActions() {
		return Actions.<T>builder()
				.addAll(actions())
				.create().available(bc -> bc.getId() == null && isActionCreateAvailable(bc)).add()
				.save().available(this::isActionSaveAvailable).add()
				.delete().available(this::isActionDeleteAvailable).add()
				.action(ActionType.COPY).available(this::isActionCopyAvailable).invoker(this::copy).add()
				.build();
	}

	protected Actions<T> actions() {
		return new Actions<>(Collections.emptyList(), Collections.emptyList());
	}

	protected boolean isActionCreateAvailable(final BusinessComponent bc) {
		return true;
	}

	protected boolean isActionSaveAvailable(final BusinessComponent bc) {
		return true;
	}

	protected boolean isActionDeleteAvailable(final BusinessComponent bc) {
		return false;
	}

	protected boolean isActionCopyAvailable(final BusinessComponent bc) {
		return false;
	}

	private Optional<E> findNextEntry(final BusinessComponent bc) {
		return Optional.ofNullable(
				baseDAO.getFirstResultOrNull(typeOfEntity, (root, query, cb) -> {
					query.orderBy(cb.asc(root.get(HistoricityEntity_.startDate)));
					final LocalDateTime redDate = bc.getParameters().getDateTo().with(asEndOfDay());
					return cb.and(
							getKeySpecification(bc).toPredicate(root, query, cb),
							cb.greaterThan(root.get(HistoricityEntity_.startDate), redDate)
					);
				})
		);
	}

	private void startEntity(final E entity, final LocalDateTime startDate, final LocalDateTime endDate) {
		entity.setStartDate(startDate.with(asStartOfDay()));
		entity.setEndDate(endDate);
	}

	private void closeEntity(final E entity, final LocalDateTime endDate) {
		entity.setEndDate(endDate.minusDays(1).with(asEndOfDay()));
	}

	private TemporalAdjuster asEndOfDay() {
		return temporal -> ((LocalDateTime) temporal).withHour(23).withMinute(59).withSecond(59).withNano(0);
	}

}
