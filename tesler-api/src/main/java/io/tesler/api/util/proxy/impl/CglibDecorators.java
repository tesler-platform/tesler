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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;


public class CglibDecorators {

	private CglibDecorators() {
		super();
	}

	public static <T> T wrap(final IDecorator<T> decorator,
			final Class<?>... ifaces) {
		return wrap(
				decorator,
				new CglibOverrideMethodHandler<>(decorator),
				ifaces
		);
	}

	public static <T> T wrap(final IDecorator<T> decorator,
			final MethodInterceptor interceptor,
			final Class<?>... ifaces) {
		List<Class<?>> interfaces = new ArrayList<>();
		if (ifaces != null) {
			Collections.addAll(interfaces, ifaces);
		}
		return wrap(decorator, interceptor, interfaces);
	}

	public static <T> T wrap(final IDecorator<T> decorator,
			final Collection<Class<?>> ifaces) {
		return wrap(
				decorator,
				new CglibOverrideMethodHandler<>(decorator),
				ifaces
		);
	}

	@SuppressWarnings("unchecked")
	public static <T> T wrap(final IDecorator<T> decorator,
			final MethodInterceptor interceptor,
			final Collection<Class<?>> ifaces) {
		final T wrapped = decorator.unwrap();
		Class<?> cls = wrapped.getClass();
		Enhancer enhancer = new Enhancer();
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setSuperclass(cls);
		if (ifaces != null && !ifaces.isEmpty()) {
			enhancer.setInterfaces(ifaces.toArray(new Class<?>[0]));
		}
		enhancer.setCallback(interceptor);
		T result = (T) enhancer.create();
		decorator.setProxy(result);
		return result;
	}

}
