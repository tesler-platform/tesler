/*-
 * #%L
 * IO Tesler - Source
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

package io.tesler.crudma.meta;

import static io.tesler.crudma.dto.BcPropertiesDTO_.filter;
import static io.tesler.crudma.dto.BcPropertiesDTO_.limit;
import static io.tesler.crudma.dto.BcPropertiesDTO_.reportPeriod;
import static io.tesler.crudma.dto.BcPropertiesDTO_.sort;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import io.tesler.crudma.dto.BcPropertiesDTO;
import org.springframework.stereotype.Service;

@Service
public class BcPropertiesMetaBuilder extends FieldMetaBuilder<BcPropertiesDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<BcPropertiesDTO> fields, InnerBcDescription bcDescription,
			Long rowId, Long parRowId) {
		fields.setEnabled(
				reportPeriod,
				limit,
				filter,
				sort
		);

	}

	@Override
	public void buildIndependentMeta(FieldsMeta fields, InnerBcDescription bcDescription, Long parRowId) {
	}

}
