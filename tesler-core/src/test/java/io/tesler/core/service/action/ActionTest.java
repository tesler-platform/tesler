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

package io.tesler.core.service.action;

import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.core.test.util.TestResponseDto;
import org.apache.commons.collections.map.SingletonMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ActionTest {

	@Test
	void associateForTwoDifferentAssocsWithSameParent() throws Exception {

		Actions<TestResponseDto, BcDescription> actions = Actions.<TestResponseDto, BcDescription>builder()
				.save()
				.withoutIcon()
				.add()
				.associate()
				.withCustomParameter(new SingletonMap("bcKey", "innerBcExample"))
				.withoutIcon()
				.withoutAutoSaveBefore()
				.add()
				.build();

		Assertions.assertEquals(2, actions.actionDefinitions.size());
		Assertions.assertEquals(ActionScope.RECORD, actions.actionDefinitions.get(0).getActionScope());
		Assertions.assertEquals("save", actions.actionDefinitions.get(0).getKey());
		Assertions.assertNull(actions.actionDefinitions.get(0).getCustomParameters());

		Assertions.assertEquals(ActionScope.BC, actions.actionDefinitions.get(1).getActionScope());
		Assertions.assertEquals("associate", actions.actionDefinitions.get(1).getKey());
		Assertions.assertEquals("innerBcExample", actions.actionDefinitions.get(1).getCustomParameters().get("bcKey"));
	}

}
