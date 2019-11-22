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

import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.model.dictionary.links.entity.DictionaryLnkRuleCond;


public interface LinkedDictionaryConditionChecker<T> {

	LOV getType();

	<R extends DictionaryLnkRuleCond> boolean check(T object, R ruleCond);

	<R extends DictionaryLnkRuleCond> T prepare(R ruleCond, BusinessComponent bc);

	<R extends DictionaryLnkRuleCond> boolean accept(R ruleCond, BusinessComponent bc);

}
