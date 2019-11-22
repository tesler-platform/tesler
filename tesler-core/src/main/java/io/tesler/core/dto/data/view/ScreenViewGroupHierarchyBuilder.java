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

import io.tesler.model.ui.entity.ScreenViewGroup;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScreenViewGroupHierarchyBuilder {

	/**
	 * Вернуть иерархию screen_view_group относительно заданной
	 *
	 * @param svGroup screen_view_group, для которого возвращается иерархия
	 * @return Иерархия screen_view_group в формате текущая/родитель/прародитель/..
	 */
	public String buildHierarchy(ScreenViewGroup svGroup) {
		if (svGroup != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(svGroup.getTitle()).append("/");
			ScreenViewGroup hierarchy = svGroup.getParent();
			while (hierarchy != null) {
				sb.append(hierarchy.getTitle());
				sb.append("/");
				hierarchy = hierarchy.getParent();
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} else {
			return null;
		}
	}

}
