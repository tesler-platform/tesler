/*-
 * #%L
 * IO Tesler - Dictionary Model
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

package io.tesler.model.dictionary.entity;

import io.tesler.model.core.api.Translation;
import io.tesler.model.core.api.TranslationId;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Entity for simple dictionaries
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DICTIONARY_ITEM_TR")
public class DictionaryItemTranslation implements Translation<DictionaryItem, DictionaryItemTranslation> {

	@EmbeddedId
	private TranslationId translationId;

	@ManyToOne
	@MapsId("id")
	@JoinColumn(name = "id")
	private DictionaryItem primaryEntity;

	@Column(length = 500)
	private String value;

	@Override
	public DictionaryItemTranslation copyTranslation() {
		DictionaryItemTranslation copy = new DictionaryItemTranslation();
		copy.setValue(getValue());
		return copy;
	}

}
