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

import lombok.RequiredArgsConstructor;
import org.pf4j.DefaultExtensionFactory;
import org.pf4j.ExtensionFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

@RequiredArgsConstructor
public class SpringExtensionFactory implements ExtensionFactory {

	private final DefaultExtensionFactory defaultExtensionFactory = new DefaultExtensionFactory();

	private final PluginManager pluginManager;

	@Override
	public <T> T create(Class<T> extensionClass) {
		final PluginWrapper pluginWrapper = pluginManager.whichPlugin(extensionClass);
		if (pluginWrapper != null) {
			final Plugin plugin = pluginWrapper.getPlugin();
			if (plugin instanceof SpringPlugin) {
				final ApplicationContext pluginContext = ((SpringPlugin) plugin).getApplicationContext();
				try {
					return pluginContext.getBean(extensionClass);
				} catch (NoSuchBeanDefinitionException e) {
					final Object extension = createWithoutSpring(extensionClass);
					if (extension != null) {
						pluginContext.getAutowireCapableBeanFactory().autowireBean(extension);
					}
					return (T) extension;
				}
			}
		}
		return (T) createWithoutSpring(extensionClass);
	}

	private Object createWithoutSpring(final Class<?> extensionClass) {
		return defaultExtensionFactory.create(extensionClass);
	}

}
