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

package io.tesler.acl.model;

import lombok.experimental.UtilityClass;


@UtilityClass
public class AttributeTypes {

	public static final IAttributeType XACML_SUBJECT = new AttributeType(
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id",
			Category.SUBJECT
	);

	private static final String RESOURCE_PREFIX = "urn:tesler:attr:01:resource";

	public static final IAttributeType BUSINESS_COMPONENT = new AttributeType(
			RESOURCE_PREFIX + ":business_component",
			Category.RESOURCE
	);

	public static final IAttributeType BUSINESS_OBJECT = new AttributeType(
			RESOURCE_PREFIX + ":object_id",
			Category.RESOURCE
	);

	public static final IAttributeType FORM_FIELD = new AttributeType(
			RESOURCE_PREFIX + ":form_field",
			Category.RESOURCE
	);

	private static final String ACTION_PREFIX = "urn:tesler:attr:01:action";

	public static final IAttributeType ACTION_TYPE = new AttributeType(
			ACTION_PREFIX + ":action_type",
			Category.ACTION
	);

	public static final IAttributeType BC_ACTION = new AttributeType(
			ACTION_PREFIX + ":bc_action",
			Category.ACTION
	);

	private static final String SUBJECT_PREFIX = "urn:tesler:attr:01:subject";

	public static final IAttributeType USER_SUBJECT = new AttributeType(
			SUBJECT_PREFIX + ":user_subject",
			Category.SUBJECT
	);

	private static final String ENVIRONMENT_PREFIX = "urn:tesler:attr:01:environment";

	public static final IAttributeType CURRENT_ROLE = new AttributeType(
			ENVIRONMENT_PREFIX + ":current_role",
			Category.ENVIRONMENT
	);

}
