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

package io.tesler.core.security.impl.pip;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.security.IPolicyInformationPoint;
import io.tesler.api.security.attributes.IAttributeSet;
import io.tesler.api.service.session.CoreSessionService;
import io.tesler.api.service.session.TeslerUserDetailsInterface;
import io.tesler.core.security.impl.AttributeTypes;
import io.tesler.core.security.impl.attributes.Attribute;
import io.tesler.core.security.impl.attributes.AttributeSet;
import io.tesler.core.util.session.SessionService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class SessionServiceInformation implements IPolicyInformationPoint<Object> {

	private final CoreSessionService coreSessionService;

	private final SessionService sessionService;

	@Override
	public Class<Object> getContextType() {
		return Object.class;
	}

	@Override
	public IAttributeSet getAttributes(Object context) {
		AttributeSet attributeSet = new AttributeSet();
		TeslerUserDetailsInterface userDetails = coreSessionService.getSessionUserDetails(false);
		if (userDetails == null) {
			return attributeSet;
		}
		attributeSet.addAttribute(new Attribute(userDetails.getUsername(), AttributeTypes.USER_SUBJECT));
		attributeSet.addAttribute(new Attribute(userDetails.getUsername(), AttributeTypes.XACML_SUBJECT));
		attributeSet.addAttribute(new Attribute(
				Optional.ofNullable(sessionService.getSessionUserRole()).map(LOV::getKey).orElse(null),
				AttributeTypes.CURRENT_ROLE
		));
		return attributeSet;
	}


}
