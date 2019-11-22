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

package io.tesler.core.util.session;

import io.tesler.model.core.entity.BaseEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;

/**
 * Класс сохраняющий значения сущности для последующего восстановления при ее создании
 *
 * @param <E> сущность для которой будет восстановлено значение
 */
public final class CreationState<E extends BaseEntity> {

	private final List<Restorer<E, ?>> restorers = new ArrayList<>();

	@SafeVarargs
	public CreationState(final Restorer<E, ?>... restorers) {
		Collections.addAll(this.restorers, restorers);
	}

	/**
	 * Восстанавливает значения для указанной сущности
	 *
	 * @param entity сущность для которой будут восстановлены значения
	 */
	public void restore(final E entity) {
		restorers.forEach(restorer -> restorer.restore(entity));
	}

	/**
	 * Класс сохраняющий значение сущности для последующего восстановления
	 *
	 * @param <E> сущность для которой будет восстановлено значение
	 * @param <V> значение для восстановления
	 */
	@RequiredArgsConstructor
	public static final class Restorer<E extends BaseEntity, V> {

		/**
		 * Метод устанавливающий значение в сущность
		 */
		private final BiConsumer<E, V> consumer;

		/**
		 * Значение для восстановления
		 */
		private final V value;

		/**
		 * Восстанавливает значение для указанной сущности
		 *
		 * @param entity сущность для которой будет восстановлено значение
		 */
		void restore(final E entity) {
			consumer.accept(entity, value);
		}

	}

}
