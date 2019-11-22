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

package io.tesler.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUtils {

	@SuppressWarnings("LoopStatementThatDoesntLoop")
	public static <T> List<T> loadServices(Class<T> cls, Object caller) {
		List<ServiceLoader<T>> serviceLoaders = new ArrayList<>();
		serviceLoaders.add(ServiceLoader.load(cls));
		serviceLoaders.add(ServiceLoader.load(cls, ClassLoader.getSystemClassLoader()));
		if (caller != null) {
			Class callerClass;
			if (caller instanceof Class) {
				callerClass = (Class) caller;
			} else {
				callerClass = caller.getClass();
			}
			serviceLoaders.add(ServiceLoader.load(cls, callerClass.getClassLoader()));
		}
		List<T> result = new ArrayList<>();
		for (ServiceLoader<T> serviceLoader : serviceLoaders) {
			for (T service : serviceLoader) {
				result.add(service);
			}
		}
		return result;
	}

	@SuppressWarnings("LoopStatementThatDoesntLoop")
	public static <T> T getService(Class<T> cls, Object caller) {
		for (T service : loadServices(cls, caller)) {
			return service;
		}
		return null;
	}

}
