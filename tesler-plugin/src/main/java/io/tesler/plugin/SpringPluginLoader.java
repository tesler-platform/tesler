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

import java.nio.file.Path;
import org.pf4j.DefaultPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;


public class SpringPluginLoader extends DefaultPluginLoader {

	public SpringPluginLoader(PluginManager pluginManager) {
		super(pluginManager);
	}

	@Override
	protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor) {
		return new SpringPluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
	}

}
