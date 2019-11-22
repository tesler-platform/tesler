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

package io.tesler.api.util.proxy.impl;

import io.tesler.api.util.proxy.IDecorator;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;


class CglibOverrideMethodHandler<T> extends JDKOverrideMethodHandler<T>
		implements MethodInterceptor {

	CglibOverrideMethodHandler(final IDecorator<T> decorator) {
		super(decorator);
	}

	@Override
	public Object intercept(final Object self,
			final Method method,
			final Object[] args,
			final MethodProxy methodProxy) throws Throwable {
		return invoke(self, method, args);
	}

}
