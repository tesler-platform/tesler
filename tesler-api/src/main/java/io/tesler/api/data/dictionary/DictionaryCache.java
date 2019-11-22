/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.data.dictionary;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public interface DictionaryCache {

	AtomicReference<DictionaryCache> instance = new AtomicReference<>();

	static DictionaryCache dictionary() {
		return instance.get();
	}

	void reload();

	SimpleDictionary get(IDictionaryType type, String key);

	SimpleDictionary get(String type, String key);

	Collection<SimpleDictionary> getAll(IDictionaryType dictionaryType);

	Collection<SimpleDictionary> getAll(String dictionaryType);

	String lookupValue(LOV key, IDictionaryType type);

	String lookupValue(LOV key, String type);

	String lookupValueNullable(LOV key, IDictionaryType type);

	LOV lookupName(String val, IDictionaryType type);

	LOV lookupName(String val, String type);

	boolean containsKey(String key, IDictionaryType type);

	boolean containsKey(LOV key, IDictionaryType type);

	boolean containsValue(String value, IDictionaryType type);

	String getDescription(String key, IDictionaryType type);

	Set<String> types();

}
