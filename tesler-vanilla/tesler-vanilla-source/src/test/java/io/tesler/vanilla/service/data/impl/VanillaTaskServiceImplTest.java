/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.data.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static org.assertj.core.api.Assertions.assertThat;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.ValidatorsProvider;
import io.tesler.core.service.BcSpecificationBuilder;
import io.tesler.vanilla.VanillaServiceAssociation;
import io.tesler.vanilla.dto.VanillaResidentDTO;
import io.tesler.vanilla.dto.VanillaTaskDTO;
import io.tesler.vanilla.entity.VanillaCounterparty;
import io.tesler.vanilla.entity.VanillaTask;
import io.tesler.vanilla.service.action.VanillaTaskActionDownloadFile;
import io.tesler.vanilla.service.data.VanillaTaskService;
import io.tesler.vanilla.testing.BaseResponseServiceTest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(
		name = "child",
		classes = {
				VanillaTaskServiceImpl.class,
				VanillaTaskActionDownloadFile.class,
				VanillaResidentDTO.class
		}
)
public class VanillaTaskServiceImplTest extends BaseResponseServiceTest<VanillaTaskService> {

	@Autowired
	protected ValidatorsProvider validatorsProvider;

	@Override
	protected Class<VanillaTaskService> getServiceClass() {
		return VanillaTaskService.class;
	}

	@Test
	void testList() {
		VanillaTaskService service = getService();
		assertThat(service).isNotNull();
		VanillaTask task = new VanillaTask();
		baseDAO.save(task);
		VanillaCounterparty counterparty = new VanillaCounterparty();
		baseDAO.save(counterparty);
		BusinessComponent bc = createBc(
				VanillaServiceAssociation.taskVanilla.getBcDescription(),
				null,
				counterparty.getId().toString()
		);
		addResultSet(
				VanillaTask.class,
				service.unwrap(BcSpecificationBuilder.class)
						.buildBcSpecification(bc),
				Arrays.asList(task)
		);
		List<VanillaTaskDTO> result = service.getList(bc).getResult();
		assertThat(result).isNotNull().isNotEmpty().hasSize(1);
		result = service.getList(
				bc.withParentId(String.valueOf(idSequence.incrementAndGet()))
		).getResult();
		assertThat(result).isNotNull().isEmpty();
	}

	@Test
	public void testCreate() {
		Long cntrpId = baseDAO.save(new VanillaCounterparty());
		VanillaTaskService service = getService();
		assertThat(service).isNotNull();
		VanillaTaskDTO dto = service.createEntity(
				createBc(
						VanillaServiceAssociation.taskVanilla.getBcDescription(),
						null,
						cntrpId.toString()
				)
		).getRecord();
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isNotNull();
	}

	@Test
	public void testKppValidation() {
		Validator validator = validatorsProvider.getValidator(VanillaResidentDTO.class);
		VanillaResidentDTO counterparty = new VanillaResidentDTO();
		counterparty.setKpp("1234");
		Set<ConstraintViolation<VanillaResidentDTO>> violations = validator.validate(counterparty);
		assertThat(violations.iterator().next().getMessage()).isEqualTo(errorMessage("error.validation.kpp"));
		assertThat(violations).isNotEmpty();
		counterparty.setKpp("aaaaaaaaa");
		violations = validator.validate(counterparty);
		assertThat(violations).isNotEmpty();
		counterparty.setKpp("999900888");
		violations = validator.validate(counterparty);
		assertThat(violations).isNotEmpty();
		counterparty.setKpp("999901888");
		violations = validator.validate(counterparty);
		assertThat(violations).isEmpty();
	}

}
