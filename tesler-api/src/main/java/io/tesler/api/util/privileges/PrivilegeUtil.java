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

package io.tesler.api.util.privileges;

import io.tesler.api.util.Invoker;
import io.tesler.api.util.ServiceUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
@UtilityClass
public class PrivilegeUtil {

	@SuppressWarnings("unchecked")
	private static <T, E extends Throwable> IPrivilegedInvoker<T, E> getInvoker() {
		IPrivilegedInvoker<T, E> invoker = ServiceUtils.getService(IPrivilegedInvoker.class, PrivilegeUtil.class);
		String message;
		if (invoker != null) {
			message = String.format("Acquired privileged invoker with class: %s", invoker.getClass());
		} else {
			message = "No privileged invoker";
		}
		log.debug(message);
		return invoker;
	}

	public static <T, E extends Throwable> T runPrivileged(final Invoker<T, E> action) throws E {
		IPrivilegedInvoker<T, E> invoker = getInvoker();
		if (invoker == null) {
			return action.invoke();
		}
		String securityAlias = invoker.getDefaultSecurityAlias();
		if (StringUtils.isBlank(securityAlias)) {
			log.debug("Empty security alias");
			return action.invoke();
		}
		log.debug("Using security alias: " + securityAlias);
		return invoker.runAs(action, invoker.getSubject(securityAlias));
	}


}
