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

import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dto.AssociateDTO;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.exception.ServerException;
import io.tesler.constgen.DtoField;
import io.tesler.core.config.cache.CacheConfig;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dao.BaseDAO;
import io.tesler.core.dao.impl.SearchSpecDao;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.PreInvokeEvent;
import io.tesler.core.dto.rowmeta.*;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.exception.EntityNotFoundException;
import io.tesler.core.exception.UnconfirmedException;
import io.tesler.core.service.BcSpecificationBuilder;
import io.tesler.core.service.DTOMapper;
import io.tesler.core.service.IOutwardReportEngineService;
import io.tesler.core.service.ResponseService;
import io.tesler.core.service.action.*;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.core.service.rowmeta.RowMetaType;
import io.tesler.core.service.spec.BcSpecificationHolder;
import io.tesler.core.service.spec.LinkSpecificationHolder;
import io.tesler.core.service.spec.SecuritySpecificationHolder;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.BaseEntity_;
import io.tesler.model.ui.entity.SearchSpec;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.tesler.api.data.dao.SpecificationUtils.*;
import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Transactional
@RequiredArgsConstructor
public abstract class AbstractResponseService<T extends DataResponseDTO, E extends BaseEntity> implements
		ResponseService<T, E> {

	@Getter
	protected final Class<T> typeOfDTO;

	@Getter
	protected final Class<E> typeOfEntity;

	protected final SingularAttribute<? super E, ? extends BaseEntity> parentSpec;

	private final Class<? extends FieldMetaBuilder<T, InnerBcDescription>> metaBuilder;

	@Autowired
	private BcSpecificationBuilder specificationBuilder;

	protected Class<? extends SecuritySpecificationHolder<E>> securitySpecificationHolder = null;

	protected Class<? extends BcSpecificationHolder<E>> bcSpecificationHolder = null;

	protected Class<? extends LinkSpecificationHolder<E>> linkSpecificationHolder = null;

	protected Class<? extends PreActionConditionHolderDataResponse<T>> preActionConditionHolderDataResponse = null;

	protected Class<? extends PreActionConditionHolderAssoc> preActionConditionHolderAssoc = null;

	@Autowired
	protected BaseDAO baseDAO;

	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	private DTOMapper dtoMapper;

	@Autowired
	private SearchSpecDao ssDao;

	@Autowired
	@Lazy
	private Optional<IOutwardReportEngineService> outwardReportEngineService;

	public static <T> T cast(Object o, Class<T> clazz) {
		return clazz.isInstance(o) ? clazz.cast(o) : null;
	}

	/**
	 * Saving the value of the DTO field (when it changes) in the entity field (using the custom DTO-getter).
	 *
	 * @param <D> type of DTO field value to be saved in the entity field
	 * @param <V> type of entity field to the value is to be saved
	 * @param dto DTO-object, which value to be saved to the entity field
	 * @param dtoField the DTO-object field, which value to be saved to the entity field
	 * @param entitySetter method for saving a value (when it changes) to an entity
	 * @param dtoGetter method for retrieving a value (when it changes) from the DTO
	 * @param mapper converts the saving value into the corresponding entity field type
	 */
	public final <D, V> void setMappedIfChanged(
			final T dto, final DtoField<T, D> dtoField,
			final Consumer<V> entitySetter, final Supplier<D> dtoGetter, final Function<D, V> mapper) {
		if (dto.isFieldChanged(dtoField)) {
			entitySetter.accept(mapper.apply(dtoGetter.get()));
		}
	}

	/**
	 * Saving the value of the DTO field (when it changes) in the entity field (using the custom DTO-getter).
	 *
	 * @param <V> type of entity field to the value is to be saved
	 * @param dto DTO-object, which value to be saved to the entity field
	 * @param dtoField the DTO-object field, which value to be saved to the entity field
	 * @param entitySetter method for saving a value (when it changes) to an entity
	 * @param dtoGetter method for retrieving a value (when it changes) from the DTO
	 */
	public final <V> void setIfChanged(
			final T dto, final DtoField<T, V> dtoField,
			final Consumer<V> entitySetter, final Supplier<V> dtoGetter) {
		setMappedIfChanged(dto, dtoField, entitySetter, dtoGetter, Function.identity());
	}

	/**
	 * Saving the value of the DTO field (when it changes) in the entity field.
	 *
	 * @param <D> type of DTO field value to be saved in the entity field
	 * @param <V> type of entity field to the value is to be saved
	 * @param dto DTO-object, which value to be saved to the entity field
	 * @param dtoField the DTO-object field, which value to be saved to the entity field
	 * @param entitySetter method for saving a value (when it changes) to an entity
	 * @param mapper converts the saving value into the corresponding entity field type
	 */
	public final <D, V> void setMappedIfChanged(
			final T dto, final DtoField<T, D> dtoField,
			final Consumer<V> entitySetter, final Function<D, V> mapper) {
		setMappedIfChanged(dto, dtoField, entitySetter, () -> dtoField.getValue(dto), mapper);
	}

	/**
	 * Saving the value of the DTO field (when it changes) in the entity field.
	 *
	 * @param <V> type of entity field to the value is to be saved
	 * @param dto DTO-object, which value to be saved to the entity field
	 * @param dtoField the DTO-object field, which value to be saved to the entity field
	 * @param entitySetter method for saving a value (when it changes) to an entity
	 */
	public final <V> void setIfChanged(final T dto, final DtoField<T, V> dtoField, final Consumer<V> entitySetter) {
		setMappedIfChanged(dto, dtoField, entitySetter, Function.identity());
	}

	@Override
	public <V> V unwrap(Class<V> cls) {
		if (cls.isInstance(specificationBuilder)) {
			return (V) specificationBuilder;
		}
		if (cls.isInstance(this)) {
			return (V) this;
		}
		throw new IllegalArgumentException(cls.getName());
	}

	@Override
	public boolean isDeferredCreationSupported(BusinessComponent<InnerBcDescription> bc) {
		return true;
	}

	@Override
	public boolean hasPersister() {
		return !typeOfEntity.isInterface() && !Modifier.isAbstract(typeOfEntity.getModifiers());
	}

	@Override
	public E getOneAsEntity(BusinessComponent<InnerBcDescription> bc) {
		Specification<E> getOneSpecification = Specification.where(specificationBuilder.buildBcSpecification(bc, getParentSpecification(bc), getSpecification(bc)))
				.and((root, cq, cb) -> cb.equal(root.get(BaseEntity_.id), bc.getIdAsLong()));
		E entity = baseDAO.getFirstResultOrNull(typeOfEntity, getOneSpecification);
		if (entity == null) {
			throw new EntityNotFoundException(typeOfEntity.getSimpleName(), bc.getIdAsLong());
		}
		return entity;
	}

	@Override
	@Cacheable(cacheResolver = CacheConfig.TESLER_CACHE_RESOLVER, 
			cacheNames = CacheConfig.REQUEST_CACHE,
			key = "{#root.targetClass, #root.methodName, #bc.name, #bc.id}"
	)
	public T getOne(BusinessComponent<InnerBcDescription> bc) {
		return doGetOne(bc);
	}

	protected T doGetOne(BusinessComponent<InnerBcDescription> bc) {
		return entityToDto(bc, getOneAsEntity(bc));
	}

	public ActionResultDTO<T> deleteEntity(BusinessComponent<InnerBcDescription> bc) {
		baseDAO.delete(getOneAsEntity(bc));
		return new ActionResultDTO<>();
	}

	@Override
	public ResultPage<T> getList(BusinessComponent<InnerBcDescription> bc) {
		return getList(baseDAO, bc);
	}

	protected ResultPage<T> getList(BaseDAO dao, BusinessComponent<InnerBcDescription> bc) {
		return getList(dao, bc, typeOfEntity, typeOfDTO);
	}

	protected final ResultPage<T> getList(BaseDAO dao, BusinessComponent<InnerBcDescription> bc, Class<E> typeOfEntity, Class<T> typeOfDTO) {
		return entitiesToDtos(
				bc,
				dao.getList(
						typeOfEntity,
						typeOfDTO,
						specificationBuilder.buildBcSpecification(bc, getParentSpecification(bc), getSpecification(bc)),
						bc.getParameters(),
						getFetchGraph(bc)
				)
		);
	}

	protected final ResultPage<E> getPageEntities(BusinessComponent<InnerBcDescription> bc, QueryParameters queryParameters) {
		return baseDAO.getList(typeOfEntity, typeOfDTO, getParentSpecification(bc), queryParameters);
	}

	protected String getFetchGraphName(BusinessComponent<InnerBcDescription> bc) {
		return bc.getName();
	}

	protected EntityGraph<? super E> getFetchGraph(BusinessComponent<InnerBcDescription> bc) {
		String graphName = getFetchGraphName(bc);
		if (graphName == null) {
			return null;
		}
		return baseDAO.getEntityGraph(typeOfEntity, graphName);
	}

	@Override
	public ActionsDTO getAvailableActions(RowMetaType metaType, DataResponseDTO data, BusinessComponent<InnerBcDescription> bc) {
		return getActions().toDto(bc);
	}

	@Override
	public ActionResultDTO<T> onCancel(BusinessComponent<InnerBcDescription> bc) {
		return new ActionResultDTO<T>().setAction(PostAction.postDelete());
	}

	@Override
	public ActionResultDTO<T> invokeAction(BusinessComponent<InnerBcDescription> bc, String actionName, DataResponseDTO data) {
		ActionDescription<T, InnerBcDescription> action = getActions().getAction(actionName);
		if (action == null || !action.isAvailable(bc)) {
			throw new BusinessException().addPopup(
					errorMessage("error.action_unavailable", actionName)
			);
		}
		preInvoke(bc, action.withPreActionEvents(bc), data, null);
		T record = null;
		if (nonNull(bc.getId())) {
			// Data is changed and we need to apply these changes
			// Lock must be set here
			if (action.isAutoSaveBefore() && nonNull(data) && data.hasChangedFields()) {
				record = updateEntity(bc, data).getRecord();
			} else {
				// No changes comes,
				// but action requires lock
				if (action.isUpdateRequired() && hasPersister()) {
					loadEntity(bc, data);
				}
				// WARNING! Don't touch cache here!
				// getOne() method may not be invoked
				record = doGetOne(bc);
			}
		}
		return action.invoke(bc, Optional.ofNullable(record).orElse((T)data));
	}


	private void preInvoke(BusinessComponent<InnerBcDescription> bc, List<PreActionEvent> preActionEvents, DataResponseDTO data, AssociateDTO associateDTO) {
		List<String> preInvokeParameters = bc.getPreInvokeParameters();
		List<PreInvokeEvent> preInvokeEvents = new ArrayList<>();
		if (nonNull(preActionEvents)) {
			preActionEvents.forEach(preActionEvent -> {
				if (nonNull(preActionEvent) && !preInvokeParameters.contains(preActionEvent.getKey()) &&
						(data == null ? getCheckerAssoc(preActionEvent.getPreActionCondition())
								.check(new AssocPreActionEventParameters(associateDTO, bc, preInvokeParameters))
								: getCheckerData(preActionEvent.getPreActionCondition())
								.check(new DataResponsePreActionEventParameters(data, bc, preInvokeParameters)))) {
					preInvokeEvents.add(PreInvokeEvent.of(
							preActionEvent.getKey(),
							preActionEvent.getType().getKey(),
							preActionEvent.getMessage()
					));
				}
			});
		}
		if (!preInvokeEvents.isEmpty()) {
			throw new UnconfirmedException().addPreInvokeEvents(preInvokeEvents);
		}
	}

	private PreActionEventChecker<T> getCheckerData(PreActionCondition preActionCondition) {
		if (nonNull(preActionConditionHolderDataResponse)) {
			PreActionEventChecker<T> checker = applicationContext
					.getBean(preActionConditionHolderDataResponse).getChecker(preActionCondition);
			if (nonNull(checker)) {
				return checker;
			}
			throw new ServerException(
					"PreActionHolder in " + getClass().getSimpleName() + "doesn't have checker for " + preActionCondition
							.getName() + "preAction");
		}
		throw new ServerException(
				"PreActionConditionHolder is null for " + preActionCondition.getName() + " preaction in " + getClass()
						.getSimpleName() + " service");
	}

	private PreActionEventChecker<AssociateDTO> getCheckerAssoc(PreActionCondition preActionCondition) {
		if (nonNull(preActionConditionHolderAssoc)) {
			PreActionEventChecker<AssociateDTO> checker = applicationContext.getBean(preActionConditionHolderAssoc)
					.getChecker(preActionCondition);
			if (nonNull(checker)) {
				return checker;
			}
			throw new ServerException(
					"PreActionHolder in " + getClass().getSimpleName() + "doesn't have checker for " + preActionCondition
							.getName() + "preAction");
		}
		throw new ServerException(
				"PreActionConditionHolder is null for " + preActionCondition.getName() + " preaction in " + getClass()
						.getSimpleName() + " service");
	}

	@Override
	public long count(BusinessComponent<InnerBcDescription> bc) {
		return count(baseDAO, bc);
	}

	protected long count(BaseDAO dao, BusinessComponent<InnerBcDescription> bc) {
		return count(dao, bc, typeOfEntity, typeOfDTO);
	}

	protected final long count(BaseDAO dao, BusinessComponent<InnerBcDescription> bc, Class<E> typeOfEntity, Class<T> typeOfDTO) {
		return dao.getCount(
				typeOfEntity,
				typeOfDTO,
				(root, cq, cb) -> {
					Predicate pr = specificationBuilder.buildBcSpecification(bc, getParentSpecification(bc), getSpecification(bc)).toPredicate(root, cq, cb);
					cq.orderBy();
					return pr;
				},
				bc.getParameters()
		);
	}

	@Override
	public void validate(BusinessComponent<InnerBcDescription> bc, DataResponseDTO data) {
		T entityDto = entityToDto(bc, getOneAsEntity(bc));
		updateDataDto(data, entityDto);
		ActionDescription<T, InnerBcDescription> save = getActions().getAction(ActionType.SAVE.getType());
		if (nonNull(save)) {
			popup(save.validate(bc, data, entityDto));
			List<PreActionEvent> preActionEvents = save.withPreActionEvents(bc);
			preInvoke(bc, nonNull(preActionEvents) ? preActionEvents : getPreActionsForSave(),
					data, null
			);
		}
	}

	private void popup(List<String> messages) {
		if (nonNull(messages) && !messages.isEmpty()) {
			throw new BusinessException().addPopup(messages);
		}
	}

	public Actions<T, InnerBcDescription> getActions() {
		return Actions.<T, InnerBcDescription>builder()
				.action("drillDown", "Посмотреть форму")
				.available(this::isDrillDownActionAvailable).invoker(this::actionOpenUrl).add(false)
				.build();
	}

	protected List<PreActionEvent> getPreActionsForSave() {
		return Collections.emptyList();
	}

	private boolean isDrillDownActionAvailable(BusinessComponent<InnerBcDescription> bc) {
		return outwardReportEngineService.map(service -> service.isOutwardsReportAvailable(bc)).orElse(false);
	}

	private ActionResultDTO<T> actionOpenUrl(final BusinessComponent<InnerBcDescription> bc, final T data) {
		final ActionResultDTO<T> result = new ActionResultDTO<>(data);
		outwardReportEngineService.ifPresent(service ->
				result.setAction(
						PostAction.drillDown(
								DrillDownType.RELATIVE_NEW,
								service.getOutwardReportFormattedUrl(bc, bc.getParameters()),
								service.getOutwardReportName(bc)
						)
				)
		);
		return result;
	}

	protected ResultPage<T> entitiesToDtos(BusinessComponent<InnerBcDescription> bc, ResultPage<E> entities) {
		return ResultPage.of(entities, e -> entityToDto(bc, e));
	}

	protected T entityToDto(final BusinessComponent<InnerBcDescription> bc, final E entity) {
		return dtoMapper.entityToDto(bc, entity, typeOfDTO);
	}

	private void updateDataDto(DataResponseDTO data, T entityDto) {
		T updatedDto = cast(data, typeOfDTO);
		Stream.of(entityDto.getClass().getDeclaredFields())
				.filter(field -> !data.isFieldChanged(field.getName()))
				.forEach(field -> {
					field.setAccessible(true);
					try {
						field.set(updatedDto, getValue(field.getName(), entityDto));
					} catch (IllegalAccessException e) {
						log.error(e.getLocalizedMessage());
					}
				});
	}

	private Object getValue(String fieldName, T data) {
		if (isNull(data)) {
			return null;
		}
		AtomicReference<Object> value = new AtomicReference<>();
		Stream.of(data.getClass().getDeclaredFields())
				.filter(field -> field.getName().equals(fieldName))
				.findFirst()
				.ifPresent(field -> {
					field.setAccessible(true);
					try {
						value.set(field.get(data));
					} catch (IllegalAccessException e) {
						log.error(e.getLocalizedMessage());
					}
				});
		return value.get();
	}

	/**
	 * deprecated, the hasNext formation logic has been moved to the DAO layer
	 * Left for custom DAOs that return List instead of ResultPage
	 */
	@Deprecated
	protected ResultPage<E> entityListToResultPage(final List<E> entities, final int limit) {
		boolean hasNext;
		int size = entities.size();
		if (size == (limit + 1)) {
			entities.remove(size - 1);
			hasNext = true;
		} else {
			hasNext = false;
		}
		return new ResultPage<>(entities, hasNext);
	}

	protected ResultPage<T> dtoListToResultPage(final List<T> dtos, final int limit) {
		boolean hasNext;
		int size = dtos.size();
		if (size == (limit + 1)) {
			dtos.remove(size - 1);
			hasNext = true;
		} else {
			hasNext = false;
		}
		return new ResultPage<>(dtos, hasNext);
	}

	protected Specification<E> getParentSpecification(BusinessComponent<InnerBcDescription> bc) {
		return (root, cq, cb) -> {
			final Long parentId = bc.getParentIdAsLong();
			if (parentSpec != null && parentId != null) {
				return cb.equal(root.get(parentSpec).get(BaseEntity_.id), parentId);
			} else {
				return cb.and();
			}
		};
	}

	protected final E isExist(final Long id) {
		E entity = baseDAO.findById(typeOfEntity, id);
		if (entity == null) {
			throw new EntityNotFoundException(typeOfEntity.getSimpleName(), id);
		}
		return entity;
	}

	protected E loadEntity(BusinessComponent<InnerBcDescription> bc, DataResponseDTO data) {
		return getOneAsEntity(bc);
	}

	public Class<? extends FieldMetaBuilder<T, InnerBcDescription>> getFieldMetaBuilder() {
		return this.metaBuilder;
	}

	@Override
	public ActionResultDTO<T> updateEntity(BusinessComponent<InnerBcDescription> bc, DataResponseDTO data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ActionResultDTO<T> preview(BusinessComponent<InnerBcDescription> bc, DataResponseDTO data) {
		return updateEntity(bc, data);
	}

	@Override
	public CreateResult<T> createEntity(BusinessComponent<InnerBcDescription> bc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AssociateResultDTO associate(List<AssociateDTO> data, BusinessComponent<InnerBcDescription> bc) {
		ActionDescription<T, InnerBcDescription> associate = getActions().getAction(ActionType.ASSOCIATE.getType());
		if (nonNull(associate)) {
			data.stream().filter(AssociateDTO::getAssociated)
					.forEach(dto -> preInvoke(bc, associate.withPreActionEvents(bc), null, dto));
		}
		return doAssociate(data, bc);
	}

	protected AssociateResultDTO doAssociate(List<AssociateDTO> data, BusinessComponent<InnerBcDescription> bc) {
		throw new UnsupportedOperationException();
	}

	protected Specification<E> getSpecification(BusinessComponent<InnerBcDescription> bc) {
		return and(
				getSecuritySpecification(bc.getDescription()),
				getBcSpecification(bc.getDescription()),
				getLinkSpecification(bc)
		);
	}

	protected Specification<E> getSecuritySpecification(InnerBcDescription bcDescription) {
		if (securitySpecificationHolder == null) {
			return trueSpecification();
		}
		SecuritySpecificationHolder<E> specificationHolder = applicationContext.getBean(securitySpecificationHolder);
		List<SearchSpec> searchSpecs = ssDao.getSecuritySpecifications(bcDescription);
		String ssNames = searchSpecs.stream().map(SearchSpec::getName)
				.map(LOV::getKey).collect(Collectors.joining(", "));
		log.info("Security specifications for " + bcDescription.getName() + " is " + ssNames);
		return searchSpecs.stream().map(
				searchSpec -> specificationHolder.get(specificationHolder.fromLov(searchSpec.getName()))
		).filter(Objects::nonNull).reduce(or()).orElse(trueSpecification());
	}

	protected Specification<E> getBcSpecification(InnerBcDescription bcDescription) {
		if (bcSpecificationHolder == null) {
			return trueSpecification();
		}
		BcSpecificationHolder<E> specificationHolder = applicationContext.getBean(bcSpecificationHolder);
		List<SearchSpec> searchSpecs = ssDao.getBcSpecifications(bcDescription);
		String ssNames = searchSpecs.stream().map(SearchSpec::getName)
				.map(LOV::getKey).collect(Collectors.joining(", "));
		log.info("BC specifications for " + bcDescription.getName() + " is " + ssNames);
		return searchSpecs.stream().map(
				searchSpec -> specificationHolder.get(specificationHolder.fromLov(searchSpec.getName()))
		).filter(Objects::nonNull).reduce(and()).orElse(trueSpecification());
	}

	protected Specification<E> getLinkSpecification(BusinessComponent<InnerBcDescription> bc) {
		if (linkSpecificationHolder == null) {
			return trueSpecification();
		}
		LinkSpecificationHolder<E> specificationHolder = applicationContext.getBean(linkSpecificationHolder);
		List<SearchSpec> searchSpecs = ssDao.getLinkSpecifications(bc.getDescription());
		String ssNames = searchSpecs.stream().map(SearchSpec::getName)
				.map(LOV::getKey).collect(Collectors.joining(", "));
		log.info("LINK specifications for " + bc.getDescription().getName() + " is " + ssNames);
		return searchSpecs.stream().map(searchSpec -> specificationHolder.get(
				specificationHolder.fromLov(searchSpec.getName()), bc.getDescription(), bc.getParentId()
		)).filter(Objects::nonNull).reduce(and()).orElse(trueSpecification());
	}

	@SneakyThrows
	public List<String> getAssociatedSsNames() {
		List<String> result = new ArrayList<>();
		if (securitySpecificationHolder != null) {
			SecuritySpecificationHolder<E> specificationHolder = securitySpecificationHolder.newInstance();
			result.addAll(specificationHolder.allValues().stream().map(Object::toString).collect(Collectors.toList()));
		}
		if (bcSpecificationHolder != null) {
			BcSpecificationHolder<E> specificationHolder = bcSpecificationHolder.newInstance();
			result.addAll(specificationHolder.allValues().stream().map(Object::toString).collect(Collectors.toList()));
		}
		if (linkSpecificationHolder != null) {
			LinkSpecificationHolder<E> specificationHolder = linkSpecificationHolder.newInstance();
			result.addAll(specificationHolder.allValues().stream().map(Object::toString).collect(Collectors.toList()));
		}
		return result;
	}

}
