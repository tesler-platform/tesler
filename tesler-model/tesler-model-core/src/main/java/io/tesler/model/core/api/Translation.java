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

package io.tesler.model.core.api;

import io.tesler.model.core.entity.BaseEntity;
import java.util.Optional;


public interface Translation<P extends BaseEntity, L extends Translation<P, L>> {

	TranslationId getTranslationId();

	void setTranslationId(TranslationId id);

	P getPrimaryEntity();

	void setPrimaryEntity(P primaryEntity);

	L copyTranslation();

	default String getLanguage() {
		return Optional.ofNullable(getTranslationId()).map(TranslationId::getLanguage).orElse(null);
	}

}
