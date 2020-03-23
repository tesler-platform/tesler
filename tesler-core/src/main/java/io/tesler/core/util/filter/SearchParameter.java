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

package io.tesler.core.util.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchParameter {

	String name() default "";

	SearchParameterType type() default SearchParameterType.STRING;

	boolean strict() default false;

	/**
	 * Блокировать механизм фильтрации на уровне ядра или нет. Если блокировано - фильтрация должна быть
	 * реализована на уровне сервисов, которые управляют сущностью, в которую входит данный параметр.
	 * По умолчанию - фильтрация на уровне ядра не блокируется.
	 *
	 * @return необходимость блокировки механизма фильтрации на уровне ядра
	 */
	boolean suppressProcess() default false;

	/**
	 * In case of multi-field value use as the key
	 * @return SearchParameterType
	 */
	SearchParameterType multiFieldKey() default SearchParameterType.LONG;

}
