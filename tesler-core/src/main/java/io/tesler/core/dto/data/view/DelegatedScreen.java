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

package io.tesler.core.dto.data.view;

import io.tesler.api.data.dto.LocaleAware;
import io.tesler.model.core.entity.Department;
import io.tesler.model.ui.entity.Screen;
import lombok.Getter;

@Getter
public class DelegatedScreen {

	private final String name;

	@LocaleAware
	private final String text;

	private final String url;

	private final Long deptId;

	public DelegatedScreen(final Department department, final Screen screen) {
		this.name = screen.getName();
		this.text = screen.getTitle() + " " + department.getShortName();
		this.url = "/screen/" + screen.getName();
		this.deptId = department.getId();
	}

}
