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


package io.tesler.core.crudma.bc;

import io.tesler.core.crudma.bc.impl.BcDescription;
import lombok.Getter;


@Getter
public enum TestServiceAssociation implements EnumBcIdentifier {

	// @formatter:off
	extremeBcExample(TestCrudma.class),
		childBcWithRefreshExample(extremeBcExample, TestCrudma.class,true),
	innerBcExample(TestReponseService.class),
	overridableBcExample(TestCrudma.class),
	;
	// @formatter:on

	public static final Holder<TestServiceAssociation> Holder = new Holder<>(TestServiceAssociation.class);

	private final BcDescription bcDescription;

	TestServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	TestServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	TestServiceAssociation(TestServiceAssociation parentName, Class<?> serviceClass, boolean refresh) {
		this(parentName.getName(), serviceClass, refresh);
	}

}
