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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.springframework.util.ReflectionUtils;


class JDKOverrideMethodHandler<T> implements InvocationHandler {

	protected final IDecorator<T> decorator;

	public JDKOverrideMethodHandler(final IDecorator<T> decorator) {
		this.decorator = Objects.requireNonNull(decorator);
	}

	private static boolean isHashCode(final Method method) {
		if (!"hashCode".equals(method.getName())) {
			return false;
		}
		Class<?>[] types = method.getParameterTypes();
		return types.length == 0;
	}

	private static boolean isEquals(final Method method) {
		if (!"equals".equals(method.getName())) {
			return false;
		}
		Class<?>[] types = method.getParameterTypes();
		return types.length == 1 && types[0] == Object.class;
	}

	private static Method getMethod(final Object object,
			final String name,
			final Class<?>[] parameterTypes) {
		Method result = ReflectionUtils.findMethod(
				object.getClass(),
				name,
				parameterTypes
		);
		if (result != null) {
			ReflectionUtils.makeAccessible(result);
		}
		return result;
	}

	@Override
	public Object invoke(final Object proxy,
			final Method method,
			final Object[] args) throws Throwable {
		if (isEquals(method)) {
			return proxy == args[0];
		}
		if (isHashCode(method)) {
			return System.identityHashCode(proxy);
		}
		Method override = getMethod(
				decorator,
				method.getName(),
				method.getParameterTypes()
		);
		try {
			T wrapped = decorator.unwrap();
			if (override == null) {
				return method.invoke(wrapped, args);
			}
			return override.invoke(decorator, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}


}
