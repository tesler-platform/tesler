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

package io.tesler.source.dto;

import static io.tesler.source.dto.WorkflowConditionDto_.condCd;
import static io.tesler.source.dto.WorkflowConditionDto_.dmn;
import static io.tesler.source.dto.WorkflowConditionDto_.seq;

import io.tesler.constgen.DtoField;
import io.tesler.core.dict.WorkflowDictionaryType;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.workflow.entity.WorkflowCondition;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowConditionDtoConstructor extends DtoConstructor<WorkflowCondition, WorkflowConditionDto> {

	private static final String DMN_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<definitions xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\" id=\"taskDecisions\" name=\"Task Decisions\" namespace=\"http://camunda.org/schema/1.0/dmn\">\n"
			+
			"  <decision id=\"postFunctionGroupCondition\" name=\"Условие\">\n" +
			"    <decisionTable hitPolicy=\"FIRST\">\n" +
			"      <output id=\"output_1\" label=\"Результат\" name=\"result\" typeRef=\"boolean\" />\n" +
			"    </decisionTable>\n" +
			"  </decision>\n" +
			"</definitions>";

	public WorkflowConditionDtoConstructor() {
		super(WorkflowCondition.class, WorkflowConditionDto.class);
	}

	@Override
	protected Map<DtoField<? super WorkflowConditionDto, ?>, ValueSupplier<? super WorkflowCondition, ? super WorkflowConditionDto, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super WorkflowConditionDto, ?>, ValueSupplier<? super WorkflowCondition, ? super WorkflowConditionDto, ?>>builder()
				.put(seq, (mapping, entity) -> entity.getSeq())
				.put(condCd, (mapping, entity) -> WorkflowDictionaryType.WF_COND.lookupValue(entity.getCondCd()))
				.put(dmn, (mapping, entity) -> entity.getDmn() == null ? DMN_TEMPLATE : entity.getDmn())
				.build();
	}

}
