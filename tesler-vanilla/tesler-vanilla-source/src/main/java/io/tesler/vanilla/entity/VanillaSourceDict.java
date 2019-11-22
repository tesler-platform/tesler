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

package io.tesler.vanilla.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Унифицированная сущность связей "Источники", используется в НФ и задачах
 * На UI сейчас это:
 * НФ - Источники, связанные записи
 * Задачи - Источники, в будущем "связанные объекты" в задаче (CBR-312)
 */

@Entity
@Getter
@Setter
@Table(name = "SOURCE_DICT_VANILLA")
public class VanillaSourceDict extends BaseEntity {

	/**
	 * Сущность которая ссылается
	 */
	private String entityNameFrom;

	/**
	 * id сущности которая ссылается
	 */
	private Long entityFromId;

	/**
	 * Сущность на которую ссылаемся
	 */
	private String entityNameTo;

	/**
	 * Тип источника, пока это Источники, Связанные объекты
	 */
	private LOV sourceType;

	/**
	 * id сущности на которую ссылаемся
	 */
	private Long entityToId;

	/**
	 * Тип связи
	 */
	private String linkType;

}
