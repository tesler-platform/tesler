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

package io.tesler.core.crudma.impl.inner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.UniversalDTO;
import io.tesler.api.data.dto.UniversalDTO_;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.AbstractCrudmaService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import io.tesler.core.dto.rowmeta.EngineFieldsMeta;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.dto.rowmeta.RowMetaDTO;
import io.tesler.core.service.ResponseFactory;
import io.tesler.core.service.action.Actions;
import io.tesler.core.service.rowmeta.RowMetaType;
import io.tesler.core.ui.BcUtils;
import io.tesler.core.util.ListPaging;
import io.tesler.model.core.dao.JpaDao;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.beans.BeanGenerator;


@RequiredArgsConstructor
public abstract class UniversalCrudmaService<D extends UniversalDTO, E> extends AbstractCrudmaService {

	@Autowired
	protected JpaDao jpaDao;

	@Autowired
	protected ResponseFactory responseFactory;

	@Autowired
	private BcUtils bcUtils;

	@Autowired
	@Qualifier("teslerObjectMapper")
	private ObjectMapper objectMapper;

	protected abstract Class<D> getDtoClass();

	protected abstract Class<? extends E> getEntityClass(BusinessComponent bc);

	@SneakyThrows
	protected D entityToDto(E entity, Class<? extends D> dtoClass, Set<String> attributes) {
		D dto = dtoClass.newInstance();
		getValues(entity, attributes).forEach(dto::set);
		return dto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActionResultDTO update(BusinessComponent bc, Map<String, Object> data) {
		List<Attribute<?, ?>> attributes = getAttributes(bc);
		E entity = getEntity(bc);
		Class<? extends D> dtoClass = createDTOClass(attributes);
		D dto = (D) responseFactory.getDTOFromMap(data, dtoClass, bc);
		setAttributes(entity, dto, attributes);
		return new ActionResultDTO<>(entityToDto(entity, dtoClass, extractNames(attributes)));
	}

	protected void setAttributes(E entity, D dto, List<Attribute<?, ?>> attributes) {
		Set<String> names = attributes.stream().filter(a -> dto.isFieldChanged(a.getName()))
				.map(Attribute::getName).collect(Collectors.toSet());
		setValues(entity, dto, names);
	}

	@SneakyThrows
	@Override
	public MetaDTO getMeta(BusinessComponent bc) {
		List<Attribute<?, ?>> attributes = getAttributes(bc);
		D data;
		if (bc.getId() != null) {
			data = get(bc);
		} else {
			Class<? extends D> dtoClass = createDTOClass(attributes);
			data = dtoClass.newInstance();
		}
		EngineFieldsMeta<DataResponseDTO> meta = getMeta(bc.getDescription(), RowMetaType.META, data, true);
		Set<String> editable = extractNames(attributes);
		for (FieldDTO fieldDTO : meta) {
			fieldDTO.setDisabled(!editable.contains(fieldDTO.getKey()));
		}
		return new MetaDTO(new RowMetaDTO(getActions().toDto(bc), meta));
	}

	protected EngineFieldsMeta getMeta(BcIdentifier bc, RowMetaType type, D dataDto, boolean visibleOnly) {
		final EngineFieldsMeta fieldsNode = new EngineFieldsMeta(objectMapper);
		Set<String> fields = getBCFields(bc, dataDto, visibleOnly);
		Map<String, Object> values = getValues(dataDto, fields);
		for (final String dtoField : fields) {
			FieldDTO fieldDTO = FieldDTO.disabledField(dtoField);
			fieldDTO.setCurrentValue(values.get(dtoField));
			fieldsNode.add(fieldDTO);
		}
		return fieldsNode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MetaDTO getMetaNew(BusinessComponent bc, CreateResult data) {
		Set<String> attributes = extractNames(getAttributes(bc));
		EngineFieldsMeta<DataResponseDTO> meta = getMeta(
				bc.getDescription(),
				RowMetaType.META_NEW,
				(D) data.getRecord(),
				true
		);
		for (FieldDTO fieldDTO : meta) {
			fieldDTO.setDisabled(!attributes.contains(fieldDTO.getKey()));
		}
		MetaDTO result = new MetaDTO(new RowMetaDTO(getActions().toDto(bc), meta));
		result.setPostActions(data.getPostActions());
		return result;
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent bc) {
		return buildMeta(Collections.emptyList(), getActions().toDto(bc));
	}

	public Actions<D> getActions() {
		return Actions.<D>builder()
				.save().available(this::isSaveAvailable).add()
				.create().available(this::isCreateAvailable).add()
				.delete().available(this::isDeleteAvailable).add()
				.build();
	}

	@Override
	public ActionResultDTO invokeAction(BusinessComponent bc, String actionName, Map<String, Object> data) {
		return super.invokeAction(bc, actionName, data);
	}

	protected boolean isSaveAvailable(BusinessComponent bc) {
		return false;
	}

	protected boolean isCreateAvailable(BusinessComponent bc) {
		return false;
	}

	protected boolean isDeleteAvailable(BusinessComponent bc) {
		return false;
	}

	@Override
	public D get(BusinessComponent bc) {
		List<Attribute<?, ?>> attributes = getAttributes(bc);
		Class<? extends D> dtoClass = createDTOClass(attributes);
		return entityToDto(getEntity(bc), dtoClass, extractNames(attributes));
	}

	protected abstract E getEntity(BusinessComponent bc);

	@Override
	public ResultPage<D> getAll(BusinessComponent bc) {
		List<Attribute<?, ?>> attributes = getAttributes(bc);
		Class<? extends D> dtoClass = createDTOClass(attributes);
		Collection<? extends E> entities = getEntities(bc);
		Set<String> names = extractNames(attributes);
		List<D> result = new ArrayList<>();
		for (E entity : entities) {
			result.add(entityToDto(entity, dtoClass, names));
		}
		if (entities instanceof ResultPage) {
			return ResultPage.of(
					result,
					((ResultPage) entities).isHasNext()
			);
		}
		return ListPaging.getResultPage(result, bc.getParameters());
	}

	protected abstract Collection<? extends E> getEntities(BusinessComponent bc);


	@SuppressWarnings("unchecked")
	protected Class<? extends D> createDTOClass(List<Attribute<?, ?>> attributes) {
		BeanGenerator generator = new BeanGenerator();
		generator.setSuperclass(getDtoClass());
		Set<String> ignored = getIgnoredAttributes();
		Consumer<Attribute> c = a -> generator.addProperty(a.getName(), a.getJavaType());
		attributes.stream().filter(a -> !ignored.contains(a.getName())).forEach(c);
		return (Class<? extends D>) generator.createClass();
	}

	protected List<Attribute<?, ?>> getAttributes(BusinessComponent bc) {
		return getEntityAttributes(getEntityClass(bc));
	}

	@SuppressWarnings("unchecked")
	protected List<Attribute<?, ?>> getEntityAttributes(Class<? extends E> cls) {
		EntityType<? extends E> entityType = jpaDao.getEntityType(cls);
		return entityType.getAttributes().stream()
				.map(attribute -> (Attribute<? super E, ?>) attribute)
				.filter(attribute -> !attribute.isAssociation() && !attribute.isCollection())
				.collect(Collectors.toList());
	}

	protected Set<String> getIgnoredAttributes() {
		Set<String> result = new HashSet<>();
		result.add(UniversalDTO_.id.getName());
		result.add(UniversalDTO_.vstamp.getName());
		return result;
	}

	protected List<Attribute<?, ?>> getAttributes(Class<? extends E> cls, Predicate<Attribute<?, ?>> predicate) {
		return getEntityAttributes(cls).stream()
				.filter(predicate)
				.collect(Collectors.toList());
	}

	@SneakyThrows
	protected Map<String, Object> getValues(Object entity, Set<String> attributes) {
		Map<String, Object> result = new HashMap<>();
		getPropertyDescriptors(entity)
				.filter(pd -> attributes.contains(pd.getName()))
				.forEach(pd -> result.put(pd.getName(), getValue(entity, pd)));
		return result;
	}


	@SneakyThrows
	protected void setValues(Object entity, UniversalDTO dto, Set<String> attributes) {
		getPropertyDescriptors(entity)
				.filter(pd -> attributes.contains(pd.getName()))
				.forEach(pd -> writeValue(entity, dto.get(pd.getName()), pd));
	}

	protected Set<String> extractNames(List<Attribute<?, ?>> attributes) {
		return attributes.stream().map(Attribute::getName).collect(Collectors.toSet());
	}

	@SneakyThrows
	private Object getValue(Object entity, PropertyDescriptor pd) {
		return pd.getReadMethod().invoke(entity);
	}

	@SneakyThrows
	private void writeValue(Object entity, Object value, PropertyDescriptor pd) {
		pd.getWriteMethod().invoke(entity, value);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private Set<String> getBCFields(BcIdentifier bc, D dataDTO, boolean visibleOnly) {
		if (visibleOnly) {
			return bcUtils.getBcFieldsForCurrentScreen(bc);
		}
		return getPropertyDescriptors(dataDTO)
				.map(PropertyDescriptor::getName)
				.collect(Collectors.toSet());
	}

	@SneakyThrows
	private Stream<PropertyDescriptor> getPropertyDescriptors(Object object) {
		return Stream.of(Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors())
				.filter(pd -> pd.getWriteMethod() != null && pd.getWriteMethod() != null);
	}

}
