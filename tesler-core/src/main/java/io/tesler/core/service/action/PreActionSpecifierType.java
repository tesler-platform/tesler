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

import io.tesler.core.dto.rowmeta.PreAction;


public class PreActionSpecifierType {

	public static final PreActionSpecifier WITHOUT_PREACTION = bc -> null;

	public static final PreActionSpecifier PREACTION_CONFIRMATION = bc -> PreAction.confirm();

	public static final PreActionSpecifier PREACTION_INFORMATION = bc -> PreAction.info();

	public static final PreActionSpecifier PREACTION_ERROR = bc -> PreAction.error();

}
