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

import io.tesler.api.service.LocaleService;
import io.tesler.model.core.entity.BaseEntity;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.context.i18n.LocaleContextHolder;


public interface Translatable<P extends BaseEntity, L extends Translation<P, L>> {

	Map<String, L> getTranslations();

	default void addTranslation(L translation) {
		getTranslations().put(translation.getLanguage(), translation);
	}

	default Optional<L> getTranslation() {
		return getTranslation(
				LocaleContextHolder.getLocale().getLanguage(),
				LocaleService.defaultLocale.get().getLanguage()
		);
	}

	default Optional<L> getTranslation(String language, String fallbackLanguage) {
		Map<String, L> translations = getTranslations();
		L translation = translations.get(language);
		if (translation != null) {
			return Optional.of(translation);
		}
		return Optional.ofNullable(fallbackLanguage).map(getTranslations()::get);
	}

	@SuppressWarnings("unchecked")
	default Class<L> getTranslationType() {
		Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(getClass(), Translatable.class);
		return Stream.of(Translatable.class.getTypeParameters())
				.map(typeArguments::get)
				.skip(1)
				.findFirst()
				.map(Class.class::cast)
				.orElseThrow(IllegalStateException::new);
	}


}
