/*-
 * #%L
 * IO Tesler - Plugin
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

package io.tesler.plugin;


import java.util.HashSet;
import java.util.Set;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;


public class SpringPluginClassLoader extends PluginClassLoader {

	private final Set<String> parentPackages = new HashSet<>();

	private final Set<String> parentClasses = new HashSet<>();

	public SpringPluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor,
			ClassLoader parent) {
		super(pluginManager, pluginDescriptor, parent);
	}

	public SpringPluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor,
			ClassLoader parent, boolean parentFirst) {
		super(pluginManager, pluginDescriptor, parent, parentFirst);
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(className)) {
			if (isParentClass(className)) {
				return getParent().loadClass(className);
			}
			return super.loadClass(className);
		}
	}

	private boolean isParentClass(String className) {
		for (String packageName : parentPackages) {
			if (className.startsWith(packageName)) {
				return true;
			}
		}
		return parentClasses.contains(className);
	}

	public boolean addParentPackage(String packageName) {
		return parentPackages.add(packageName);
	}

	public boolean addParentClass(String className) {
		return parentClasses.add(className);
	}


}
