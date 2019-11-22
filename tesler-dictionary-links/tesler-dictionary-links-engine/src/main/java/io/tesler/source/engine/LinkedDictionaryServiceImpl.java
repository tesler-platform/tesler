/*-
 * #%L
 * IO Tesler - Dictionary Links Engine
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

package io.tesler.source.engine;

import io.tesler.api.data.dictionary.DictionaryCache;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.constgen.DtoField;
import io.tesler.core.config.CacheConfig;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.EngineFieldsMeta;
import io.tesler.core.service.linkedlov.LinkedDictionaryService;
import io.tesler.core.ui.BcUtils;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.dictionary.links.entity.CustomizableResponseService_;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleValue;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRule_;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class LinkedDictionaryServiceImpl implements LinkedDictionaryService {

	private final Map<LOV, LinkedDictionaryConditionChecker> conditions;

	@Autowired
	private DictionaryCache dictionaryCache;

	@Autowired
	private LinkedDictionaryCache linkedDictionaryCache;

	@Autowired
	private BcUtils bcUtils;

	public LinkedDictionaryServiceImpl(Optional<List<LinkedDictionaryConditionChecker>> conditionCheckers) {
		final Builder<LOV, LinkedDictionaryConditionChecker> builder = ImmutableMap.builder();
		conditionCheckers.ifPresent(checkers -> {
			for (final LinkedDictionaryConditionChecker checker : checkers) {
				builder.put(checker.getType(), checker);
			}
		});
		this.conditions = builder.build();
	}

	@Override
	public void fillRowMetaWithLinkedDictionaries(EngineFieldsMeta<?> meta, BusinessComponent bc, boolean filterValues) {
		final String serviceName = getServiceName(bc);
		final Set<String> requiredFields = bcUtils.getBcFieldsForCurrentScreen(bc);
		Map<String, List<DictionaryLnkRule>> rulesByField = linkedDictionaryCache.getRules(serviceName);
		rulesByField.forEach((field, fieldRules) -> {
			if (requiredFields.contains(field)) {
				processRules(meta, bc, fieldRules, filterValues);
			}
		});
	}

	private void processRules(
			EngineFieldsMeta<?> meta,
			BusinessComponent bc,
			List<DictionaryLnkRule> rules,
			boolean filterValues) {
		// Разделение списка правил на 4 группы по значению полей 'Правило по-умолчанию' и 'Правило фильтра'
		Map<Boolean, Map<Boolean, List<DictionaryLnkRule>>> ruleMap = rules.stream()
				.collect(Collectors.partitioningBy(
						DictionaryLnkRule::isDefaultRuleFlg,
						Collectors.partitioningBy(DictionaryLnkRule::isFilterableField)
				));
		// ruleMap.get(значение флага defaultRuleFlg).get(значение флага filterableField)
		// возвращает список правил с учетом флагов defaultRuleFlg и filterableField
		if (!filterValues && !processRules(ruleMap.get(false).get(false), meta, bc)) {
			processRules(ruleMap.get(true).get(false), meta, bc);
		}
		if (!processRules(ruleMap.get(false).get(true), meta, bc)) {
			processRules(ruleMap.get(true).get(true), meta, bc);
		}
	}

	private String getServiceName(BusinessComponent bc) {
		if (bc.getDescription() instanceof InnerBcDescription) {
			InnerBcDescription innerBcDescription = bc.getDescription();
			return innerBcDescription.getServiceClass().getSimpleName();
		}
		return bc.getDescription().getCrudmaService().getSimpleName();
	}

	@Override
	public Set<LOV> getDictionariesForField(DtoField field, BusinessComponent bc, boolean filterValues) {
		String serviceName = bc.<InnerBcDescription>getDescription().getServiceClass().getSimpleName();
		List<DictionaryLnkRule> rules = linkedDictionaryCache.getRules(serviceName)
				.getOrDefault(field.getName(), Collections.emptyList())
				.stream().filter(rule -> filterValues == rule.isFilterableField())
				.collect(Collectors.toList());
		Set<LOV> result = new HashSet<>();
		long ruleMatchCount = rules.stream()
				.filter(rule -> !rule.isDefaultRuleFlg() && processRule(rule, result, bc))
				.count();
		if (ruleMatchCount < 1) {
			rules.stream()
					.filter(DictionaryLnkRule::isDefaultRuleFlg)
					.findFirst()
					.ifPresent(rule -> processRule(rule, result, bc));
		}
		return result;
	}

	private boolean processRule(DictionaryLnkRule rule, Set<LOV> ruleValues, BusinessComponent bc) {
		Set<LOV> lovs = new LinkedHashSet<>();
		boolean result = processRule(rule, bc, lovs);
		if (result) {
			ruleValues.addAll(lovs);
		}
		return result;
	}

	private boolean processRules(List<DictionaryLnkRule> rules, EngineFieldsMeta<?> meta, BusinessComponent bc) {
		if (rules.isEmpty()) {
			return false;
		}

		String type = rules.get(0).getType();
		String field = rules.get(0).getField();
		boolean isFilterableField = rules.get(0).isFilterableField();

		boolean anyApplied = false;
		Set<LOV> allLovs = new HashSet<>();

		for (DictionaryLnkRule rule : rules) {
			Set<LOV> lovs = new HashSet<>();
			if (processRule(rule, bc, lovs)) {
				anyApplied = true;
				allLovs.addAll(lovs);
			}
		}

		List<SimpleDictionary> values = allLovs.stream()
				.map(lov -> dictionaryCache.get(type, lov.getKey()))
				.filter(Objects::nonNull)
				.sorted(Comparator.comparingInt(SimpleDictionary::getDisplayOrder))
				.collect(Collectors.toList());

		if (anyApplied) {
			if (isFilterableField) {
				meta.addEngineFilterValues(field, values);
			} else {
				meta.addEngineConcreteValues(field, values);
			}
		}
		return anyApplied;
	}

	private boolean processRule(DictionaryLnkRule rule, BusinessComponent bc, Set<LOV> lovs) {
		if (rule.getField() == null || rule.getType() == null) {
			return false;
		}

		if (!processRuleAllConditions(bc, rule)) {
			return false;
		}

		if (rule.isAllValues()) {
			dictionaryCache.getAll(rule.getType()).stream()
					.map(SimpleDictionary::getKey)
					.map(LOV::new).forEach(lovs::add);
		} else {
			rule.getValues().stream().map(DictionaryLnkRuleValue::getChildKey)
					.forEach(lovs::add);
		}
		return true;
	}

	private boolean processRuleAllConditions(BusinessComponent bc, DictionaryLnkRule rule) {
		for (DictionaryLnkRuleCond ruleCondition : rule.getConditions()) {
			if (!processRuleCond(bc, ruleCondition)) {
				return false;
			}
		}
		return true;
	}

	private boolean processRuleCond(BusinessComponent bc, DictionaryLnkRuleCond ruleCondition) {
		try {
			LinkedDictionaryConditionChecker conditionChecker = conditions.get(ruleCondition.getType());
			if (conditionChecker == null || !conditionChecker.accept(ruleCondition, bc)) {
				return false;
			}
			boolean result = conditionChecker.check(conditionChecker.prepare(ruleCondition, bc), ruleCondition);
			if (ruleCondition.isRuleInversionFlg()) {
				result = !result;
			}
			return result;
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Component
	@RequiredArgsConstructor
	public static class LinkedDictionaryCache {

		private final JpaDao jpaDao;

		@Cacheable(
				cacheNames = {CacheConfig.LINKED_DICTIONARY_RULES},
				key = "{#root.methodName, #serviceName}"
		)
		public Map<String, List<DictionaryLnkRule>> getRules(String serviceName) {
			return jpaDao.getList(DictionaryLnkRule.class, (root, cq, cb) -> {
						root.fetch(DictionaryLnkRule_.conditions, JoinType.LEFT);
						root.fetch(DictionaryLnkRule_.values, JoinType.LEFT);
						return cb.and(
								cb.equal(
										root.get(DictionaryLnkRule_.service).get(CustomizableResponseService_.serviceName),
										serviceName
								),
								cb.isNotNull(root.get(DictionaryLnkRule_.type))
						);
					}
			).stream().distinct().map(jpaDao::evict).collect(Collectors.groupingBy(DictionaryLnkRule::getField));
		}

		@CacheEvict(value = CacheConfig.LINKED_DICTIONARY_RULES, allEntries = true)
		public void evictRules() {

		}

	}

}
