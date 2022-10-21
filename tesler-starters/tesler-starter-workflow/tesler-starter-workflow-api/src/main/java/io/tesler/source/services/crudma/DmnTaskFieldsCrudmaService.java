/*-
 * #%L
 * IO Tesler - Workflow API
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

package io.tesler.source.services.crudma;


import io.tesler.api.data.ResultPage;
import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.api.data.dto.rowmeta.FieldDTO;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.ExtremeBcDescription;
import io.tesler.core.crudma.impl.AbstractCrudmaService;
import io.tesler.core.dto.LovUtils;
import io.tesler.core.dto.rowmeta.MetaDTO;
import io.tesler.core.util.JsonUtils;
import io.tesler.core.util.ListPaging;
import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.TaskField;
import io.tesler.source.dto.DmnTaskFieldsDto;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DmnTaskFieldsCrudmaService extends AbstractCrudmaService<ExtremeBcDescription> {

	private final Map<Class<?>, String> TYPES = ImmutableMap.<Class<?>, String>builder()
			.put(String.class, "string")
			.put(Boolean.class, "boolean")
			.put(Boolean.TYPE, "boolean")
			.put(Integer.class, "integer")
			.put(Integer.TYPE, "integer")
			.put(Long.class, "long")
			.put(Long.TYPE, "long")
			.put(Double.class, "double")
			.put(Double.TYPE, "double")
			.put(BigDecimal.class, "double")
			.put(LocalDateTime.class, "date")
			.build();

	private final List<DmnTaskFieldsDto> TASK_FIELDS;

	private final List<FieldDTO> FIELD_DTO_LIST = ImmutableList.<FieldDTO>builder()
			.add(FieldDTO.disabledFilterableField("id"))
			.add(FieldDTO.disabledFilterableField("title"))
			.add(FieldDTO.disabledFilterableField("key"))
			.add(FieldDTO.disabledFilterableField("type"))
			.add(FieldDTO.disabledField("values"))
			.build();

	public DmnTaskFieldsCrudmaService(
			final WorkflowSettings<?> workflowSettings,
			final DictionaryCache dictionaryCache,
			final JpaDao jpaDao) {
		this.TASK_FIELDS = buildTaskFields(workflowSettings, dictionaryCache, jpaDao);
	}

	@Override
	public ResultPage<? extends DataResponseDTO> getAll(final BusinessComponent<ExtremeBcDescription> bc) {
		return ListPaging.getResultPage(TASK_FIELDS, bc.getParameters());
	}

	@Override
	public long count(final BusinessComponent<ExtremeBcDescription> bc) {
		return TASK_FIELDS.size();
	}

	@Override
	public MetaDTO getMeta(final BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(FIELD_DTO_LIST);
	}

	@Override
	public MetaDTO getMetaEmpty(BusinessComponent<ExtremeBcDescription> bc) {
		return buildMeta(Collections.emptyList());
	}

	private List<DmnTaskFieldsDto> buildTaskFields(
			final WorkflowSettings<?> workflowSettings,
			final DictionaryCache dictionaryCache,
			final JpaDao jpaDao) {
		final List<DmnTaskFieldsDto> taskFields = new ArrayList<>();
		for (final TaskField taskField : jpaDao.getList(TaskField.class)) {
			final Field field = FieldUtils.getField(workflowSettings.getDtoClass(), taskField.getKey(), true);

			if (field == null) {
				log.error("No field with name " + taskField.getKey() + " in class");
				continue;
			}

			try {
				taskFields.add(new DmnTaskFieldsDto(
						String.valueOf(taskFields.size()),
						taskField.getTitle(),
						taskField.getKey(),
						getDmnType(field.getType()),
						getValues(dictionaryCache, field)
				));
			} catch (Exception e) {
				log.warn(e.getLocalizedMessage(), e);
			}
		}
		return ImmutableList.copyOf(taskFields);
	}

	private String getDmnType(final Class<?> classType) {
		final String dmnType = TYPES.get(classType);
		if (dmnType == null) {
			throw new IllegalArgumentException("Поддержка типа " + classType.getName() + " не реализована DMN движком.");
		}
		return dmnType;
	}

	private String getValues(final DictionaryCache dictionaryCache, final Field field) {
		IDictionaryType dictionaryType = LovUtils.getType(field);
		if (dictionaryType == null) {
			return null;
		}
		return JsonUtils.writeValue(
				dictionaryCache.getAll(dictionaryType).stream()
						.map(SimpleDictionary::getValue)
						.toArray(String[]::new)
		);
	}

}
