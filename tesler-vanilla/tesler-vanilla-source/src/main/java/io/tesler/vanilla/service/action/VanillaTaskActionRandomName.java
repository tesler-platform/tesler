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

import static lombok.AccessLevel.PRIVATE;

import io.tesler.core.crudma.bc.BusinessComponent;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public final class VanillaTaskActionRandomName {

	public static boolean isAvailable1(BusinessComponent bc) {
		return bc.getId() != null && magicCondition(bc.getIdAsLong(), 2, 3);
	}

	public static boolean isAvailable2(BusinessComponent bc) {
		return bc.getId() != null && magicCondition(bc.getIdAsLong(), 2, 3);
	}

	public static boolean isAvailable3(BusinessComponent bc) {
		return bc.getId() != null && magicCondition(bc.getIdAsLong(), 2, 3);
	}

	public static boolean isAvailable4(BusinessComponent bc) {
		return bc.getId() != null && magicCondition(bc.getIdAsLong(), 3);
	}

	public static boolean isAvailable5(BusinessComponent bc) {
		return bc.getId() != null && magicCondition(bc.getIdAsLong(), 3);
	}

	private static boolean magicCondition(long id, int... x) {
		for (int i : x) {
			if ((id - i) % 3 == 0) {
				return true;
			}
		}
		return false;
	}

}
