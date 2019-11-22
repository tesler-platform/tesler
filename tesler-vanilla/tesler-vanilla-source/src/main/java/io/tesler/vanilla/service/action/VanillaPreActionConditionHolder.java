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

package io.tesler.vanilla.service.action;

import io.tesler.core.service.action.PreActionCondition;
import io.tesler.core.service.action.PreActionConditionHolderDataResponse;
import io.tesler.core.service.action.PreActionEventChecker;
import io.tesler.vanilla.dto.VanillaDocDTO;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class VanillaPreActionConditionHolder implements PreActionConditionHolderDataResponse<VanillaDocDTO> {

	private static final Map<String, Class<? extends PreActionEventChecker<VanillaDocDTO>>> CONDITIONS = ImmutableMap
			.<String, Class<? extends PreActionEventChecker<VanillaDocDTO>>>builder()
			.put(VanillaPreActionCondition.name, VanillaDateChanged.class)
			.build();

	private final ApplicationContext applicationContext;

	@Override
	public PreActionEventChecker<VanillaDocDTO> getChecker(PreActionCondition preActionCondition) {
		if (CONDITIONS.containsKey(preActionCondition.getName())) {
			return applicationContext.getBean(CONDITIONS.get(preActionCondition.getName()));
		}
		return null;
	}

}
