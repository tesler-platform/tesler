/*-
 * #%L
 * IO Tesler - Workflow Impl
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

package io.tesler.engine.workflow;

import io.tesler.plugin.SpringPlugin;
import io.tesler.plugin.SpringPluginManager;
import java.util.Optional;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class WorkflowPlugin extends SpringPlugin {

	public WorkflowPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
		Optional.ofNullable(getWrapper())
				.map(PluginWrapper::getPluginManager)
				.filter(SpringPluginManager.class::isInstance)
				.map(SpringPluginManager.class::cast)
				.map(SpringPluginManager::getApplicationContext)
				.ifPresent(applicationContext::setParent);
		applicationContext.register(SpringConfiguration.class);
		applicationContext.refresh();
		return applicationContext;
	}

}
