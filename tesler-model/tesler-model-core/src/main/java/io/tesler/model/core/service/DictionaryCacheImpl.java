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

package io.tesler.model.core.service;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.DictionaryCacheLoader;
import io.tesler.api.data.dictionary.IDictionaryType;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.api.service.LocaleService;
import io.tesler.api.service.tx.DeploymentTransactionSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Initiator of the cache of simple dictionaries.
 * The idea is simple, when you start the application and raise the context of the spring, we have a cache of simple directories,
 * access to this cache and not to the base throughout the entire operation of the application.
 * Key - the name of the dictionary (DICTIONARY_TYPE), value - the dictionary of a simple structure String - String
 * <p>
 * General structure of a simple dictionary:
 * KEY (LIC) - independent language key
 * VALUE- value
 */
@Slf4j
@DependsOn(DeploymentTransactionSupport.SERVICE_NAME)
@Service
public class DictionaryCacheImpl implements DictionaryCache {

	private final LocaleService localeService;

	private final AtomicReference<Cache> cache;

	private List<DictionaryCacheLoader> loaders;

	public DictionaryCacheImpl(LocaleService localeService, Optional<List<DictionaryCacheLoader>> loaders) {
		this.localeService = localeService;
		this.loaders = loaders.orElseGet(ArrayList::new);
		this.cache = new AtomicReference<>(loadCache());
		DictionaryCache.instance.set(this);
	}

	@Override
	public void reload() {
		cache.set(loadCache());
	}

	private Cache loadCache() {
		return new Cache(localeService, loaders).load();
	}

	/**
	 * @param type dictionary type
	 * @param key dictionary key
	 * @return SimpleDictionary
	 */
	@Override
	public SimpleDictionary get(IDictionaryType type, String key) {
		return getDictByKey(type.getName(), key);
	}

	@Override
	public SimpleDictionary get(String type, String key) {
		return getDictByKey(type, key);
	}

	/**
	 * @param dictionaryType dictionary type
	 * @return SimpleDictionary
	 */
	@Override
	public Collection<SimpleDictionary> getAll(IDictionaryType dictionaryType) {
		return getAll(dictionaryType.getName());
	}

	@Override
	public Collection<SimpleDictionary> getAll(String dictionaryType) {
		return Optional.ofNullable(cache.get().byKey(getLanguage()))
				.map(m -> m.get(dictionaryType))
				.map(Map::values).orElse(Collections.emptyList());
	}

	/**
	 * Finding dictionary values by key
	 *
	 * @param key key
	 * @param type dictionary type
	 * @return Russian-language value of the dictionary
	 */
	@Override
	public String lookupValue(LOV key, IDictionaryType type) {
		if (key == null || type == null) {
			return null;
		}
		return lookupValue(key, type.getName());
	}

	@Override
	public String lookupValue(LOV key, String type) {
		if (key == null || type == null) {
			return null;
		}
		SimpleDictionary dto = getDictByKey(type, key.getKey());
		if (dto == null) {
			return key.getKey();
		}
		return dto.getValue();
	}

	@Override
	public String lookupValueNullable(LOV key, IDictionaryType type) {
		if (key == null || type == null) {
			return null;
		}
		SimpleDictionary dto = getDictByKey(type, key.getKey());
		return dto != null ? dto.getValue() : null;
	}

	private SimpleDictionary getDictByKey(IDictionaryType type, String key) {
		if (key == null || type == null) {
			return null;
		}
		return getDictByKey(type.getName(), key);
	}

	private SimpleDictionary getDictByKey(String type, String key) {
		if (key == null || type == null) {
			return null;
		}

		return Optional.ofNullable(cache.get().byKey(getLanguage()))
				.map(m -> m.get(type))
				.map(m -> m.get(key))
				.orElse(null);
	}

	private SimpleDictionary getDictByVal(IDictionaryType type, String val) {
		if (val == null || type == null) {
			return null;
		}
		return getDictByVal(type.getName(), val);
	}

	private SimpleDictionary getDictByVal(String type, String val) {
		if (val == null || type == null) {
			return null;
		}

		return Optional.ofNullable(cache.get().byValue(getLanguage()))
				.map(m -> m.get(type))
				.map(m -> m.get(val))
				.orElse(null);
	}

	/**
	 * Finding a key in the dictionary by value
	 *
	 * @param val value
	 * @param type dictionary type
	 * @return dictionary key
	 */
	@Override
	public LOV lookupName(String val, IDictionaryType type) {
		if (val == null || type == null) {
			return new LOV(null);
		}
		return lookupName(val, type.getName());
	}

	@Override
	public LOV lookupName(String val, String type) {
		if (val == null || type == null) {
			return new LOV(null);
		}
		SimpleDictionary dto = getDictByVal(type, val);
		if (dto == null) {
			return new LOV(val);
		}
		return new LOV(dto.getKey());
	}

	@Override
	public boolean containsKey(String key, IDictionaryType type) {
		if (key == null || type == null) {
			return false;
		}
		return getDictByKey(type, key) != null;
	}

	@Override
	public boolean containsKey(LOV key, IDictionaryType type) {
		if (key == null || type == null) {
			return false;
		}
		return containsKey(key.getKey(), type);
	}

	@Override
	public boolean containsValue(String value, IDictionaryType type) {
		if (value == null || type == null) {
			return false;
		}
		return getDictByVal(type, value) != null;
	}

	@Override
	public String getDescription(String key, IDictionaryType type) {
		if (key == null || type == null) {
			return null;
		}
		SimpleDictionary SimpleDictionary = getDictByKey(type, key);
		if (SimpleDictionary != null) {
			return SimpleDictionary.getDescription();
		}
		return null;
	}

	private String getLanguage() {
		return LocaleContextHolder.getLocale().getLanguage();
	}

	public Set<String> types() {
		return Optional.ofNullable(cache.get().byKey(getLanguage()))
				.map(Map::keySet)
				.orElse(Collections.emptySet());
	}


	private static class Cache {

		private final List<DictionaryCacheLoader> loaders;

		private final Set<String> languages;

		private final String defaultLanguage;

		private final Map<String, Map<String, Map<String, SimpleDictionary>>> byKey;

		private final Map<String, Map<String, Map<String, SimpleDictionary>>> byValue;

		private Cache(LocaleService localeService, List<DictionaryCacheLoader> loaders) {
			this.loaders = loaders;
			languages = localeService.getSupportedLanguages();
			defaultLanguage = this.languages.iterator().next();
			byKey = new HashMap<>();
			byValue = new HashMap<>();
			languages.forEach(language -> {
				byKey.put(language, new HashMap<>());
				byValue.put(language, new HashMap<>());
			});
		}

		private Map<String, Map<String, SimpleDictionary>> byKey(String language) {
			return byKey.getOrDefault(language, byKey.get(this.defaultLanguage));
		}

		private Map<String, Map<String, SimpleDictionary>> byValue(String language) {
			return byValue.getOrDefault(language, byValue.get(this.defaultLanguage));
		}

		private Cache load() {
			Map<String, Map<String, Map<String, SimpleDictionary>>> data = loaders.stream()
					.map(DictionaryCacheLoader::load)
					.flatMap(Collection::stream)
					.filter(dictionary -> StringUtils.isNotBlank(dictionary.getLanguage()))
					.filter(dictionary -> languages.contains(dictionary.getLanguage()))
					.collect(Collectors.groupingBy(
							SimpleDictionary::getLanguage,
							Collectors.groupingBy(
									SimpleDictionary::getType,
									Collectors.toMap(
											SimpleDictionary::getKey,
											Function.identity()
									)
							)
					));
			syncLanguages(data);
			data.forEach((lang, typeaware) -> typeaware.forEach((type, dictmap) -> {
				List<SimpleDictionary> dicts = new ArrayList<>(dictmap.values());
				dicts.sort(Comparator.comparing(SimpleDictionary::getDisplayOrder));
				dicts.forEach(dict -> {
					byKey.get(lang).computeIfAbsent(type, v -> new LinkedHashMap<>()).put(dict.getKey(), dict);
					byValue.get(lang).computeIfAbsent(type, v -> new LinkedHashMap<>()).put(dict.getValue(), dict);
				});
			}));

			return this;
		}

		private void syncLanguages(Map<String, Map<String, Map<String, SimpleDictionary>>> map) {
			Map<String, Map<String, SimpleDictionary>> base = map.get(this.defaultLanguage);
			if (base == null) {
				log.error("No dictionary data for default locale");
				map.clear();
				return;
			}

			languages.stream().filter(lang -> !this.defaultLanguage.equals(lang))
					.forEach(lang ->
							merge(base, map.computeIfAbsent(
									lang,
									l -> new HashMap<>()
							))
					);
		}

		private void merge(Map primary, Map secondary) {
			for (Object key : primary.keySet()) {
				secondary.merge(key, primary.get(key), (oldValue, value) -> {
					if (oldValue instanceof Map) {
						merge((Map) value, (Map) oldValue);
						return oldValue;
					}
					return oldValue;
				});
			}
			cleanup(primary, secondary);
		}

		private void cleanup(Map primary, Map secondary) {
			Iterator<String> iter = secondary.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				if (!primary.containsKey(key)) {
					iter.remove();
					continue;
				}
				Object value = secondary.get(key);
				if (value instanceof Map) {
					cleanup((Map) primary.get(key), (Map) value);
				}
			}
		}

	}

}
