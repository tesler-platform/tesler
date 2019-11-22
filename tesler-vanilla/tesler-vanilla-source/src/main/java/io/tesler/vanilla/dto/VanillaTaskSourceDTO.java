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

package io.tesler.vanilla.dto;

import io.tesler.api.data.dictionary.DictionaryType;
import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.vanilla.entity.VanillaSourceDict;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VanillaTaskSourceDTO extends DataResponseDTO {

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
	 * id сущности на которую ссылаемся
	 */
	private Long entityToId;

	/**
	 * Тип источника, пока это Источники, Связанные объекты
	 */
	private String sourceType;

	/**
	 * Тип связи
	 */
	private String linkType;

	public VanillaTaskSourceDTO(VanillaSourceDict sourceDict) {
		this.id = sourceDict.getId().toString();
		this.entityFromId = sourceDict.getEntityFromId();
		this.entityToId = sourceDict.getEntityToId();
		this.sourceType = DictionaryType.TYPE_OBJECT.lookupValue(sourceDict.getSourceType());
		this.entityNameFrom = sourceDict.getEntityNameFrom();
		this.entityNameTo = sourceDict.getEntityNameTo();
		this.linkType = sourceDict.getLinkType();
	}

}
