/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.notifications.model.api;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public interface IRecipientResolver<E extends BaseEntity> {

	static <E extends BaseEntity, F extends BaseEntity> IRecipientResolver<F> of(IRecipientResolver<E> provider,
			Function<F, List<E>> extractor) {
		return (entity, event) -> {
			List<User> result = new ArrayList<>();
			for (E extracted : extractor.apply(entity)) {
				result.addAll(provider.getRecipients(extracted, event));
			}
			return result;
		};
	}

	List<User> getRecipients(E entity, LOV event);

}
