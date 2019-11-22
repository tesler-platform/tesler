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

package io.tesler.api.task;

import io.tesler.api.task.decorators.ClassLoaderAwareRunnable;
import io.tesler.api.task.decorators.SubjectAwareRunnable;
import io.tesler.api.task.executors.DelegatingTaskScheduler;
import lombok.SneakyThrows;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;


public class SecurityContextAwareTaskScheduler<T extends TaskScheduler & TaskExecutor>
		extends DelegatingTaskScheduler<T> {

	public SecurityContextAwareTaskScheduler(T delegate) {
		super(delegate);
	}

	@Override
	@SneakyThrows
	protected Runnable wrap(Runnable command) {
		Runnable contextAware = DelegatingSecurityContextRunnable.create(command, null);
		contextAware = SubjectAwareRunnable.wrap(contextAware);
		contextAware = ClassLoaderAwareRunnable.wrap(contextAware);
		return contextAware;
	}

}
