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

package io.tesler.core.dict;

import io.tesler.api.data.dictionary.LOV;
import lombok.experimental.UtilityClass;


public class WorkflowDictionaries {

	@UtilityClass
	public static final class WfPostFunction {

		public static final LOV ASSIGN_PREVIOUS = new LOV("AssignPrevious");

	}

	@UtilityClass
	public static final class ConditionGroupType {

		public static final LOV CONDITION = new LOV("CONDITION");

		public static final LOV POST_FUNCTION = new LOV("POST_FUNCTION");

		public static final LOV VALIDATION = new LOV("VALIDATION");

	}

	@UtilityClass
	public static final class WfCondition {

		public static final LOV ALWAYS_HIDDEN = new LOV("AlwaysHidden");

		public static final LOV DMN = new LOV("DMN");

	}

}
