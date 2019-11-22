/*-
 * #%L
 * IO Tesler - Workflow Impl
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

package io.tesler.engine.workflow.function;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.model.workflow.entity.WorkflowPostFunction;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public final class UnsupportedPostFunction implements DefaultPostFunction {

	@Override
	public LOV getType() {
		return null;
	}

	@Override
	public List<PostAction> invoke(BcDescription bcDescription, WorkflowableTask task,
			WorkflowPostFunction postFunction) {
		throw new UnsupportedOperationException(String
				.format("PostFunction %s не реализована", postFunction.getActionCd().getKey()));
	}

}
