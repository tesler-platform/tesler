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
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginLoader;
import org.pf4j.PluginRepository;
import org.pf4j.PluginStatusProvider;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;


@Slf4j
public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

	@Getter
	private final String location;

	@Getter
	@Setter
	private ApplicationContext applicationContext;

	private PluginRepository pluginRepository;

	public SpringPluginManager(String location) {
		this.location = location;
	}

	@Override
	protected ExtensionFactory createExtensionFactory() {
		return new SpringExtensionFactory(this);
	}

	@Override
	protected Path createPluginsRoot() {
		return Paths.get(".");
	}

	@Override
	protected PluginRepository createPluginRepository() {
		pluginRepository = new SpringPluginRepository(this);
		return pluginRepository;
	}

	@Override
	protected PluginLoader createPluginLoader() {
		return new SpringJarPluginLoader(this);
	}

	@Override
	protected PluginStatusProvider createPluginStatusProvider() {
		return new SpringPluginStatusProvider(this);
	}

	@SneakyThrows
	@Override
	public void loadPlugins() {
		log.debug("Lookup plugins in '{}'", location);
		if (StringUtils.isBlank(location)) {
			log.warn("Empty location");
			return;
		}

		List<Path> pluginPaths = pluginRepository.getPluginPaths();

		if (pluginPaths.isEmpty()) {
			log.info("No plugins");
			return;
		}

		log.debug("Found {} possible plugins: {}", pluginPaths.size(), pluginPaths);

		for (Path pluginPath : pluginPaths) {
			loadPluginFromPath(pluginPath);
		}

		resolvePlugins();
	}

	@PostConstruct
	public void init() {
		loadPlugins();
		startPlugins();
		injectExtensions(ExtensionPoint.class);
	}

	protected void injectExtensions(Class<?> cls) {
		AbstractApplicationContext context = (AbstractApplicationContext) getApplicationContext();
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		getPlugins().stream().map(PluginWrapper::getPlugin)
				.filter(SpringPlugin.class::isInstance)
				.map(SpringPlugin.class::cast)
				.forEach(plugin -> injectExtensions(plugin, cls, beanFactory));
	}

	protected void injectExtensions(SpringPlugin plugin, Class<?> cls, SingletonBeanRegistry beanRegistry) {
		ApplicationContext pluginContext = plugin.getApplicationContext();
		pluginContext.getBeansOfType(cls, false, true)
				.forEach(beanRegistry::registerSingleton);
	}

}
