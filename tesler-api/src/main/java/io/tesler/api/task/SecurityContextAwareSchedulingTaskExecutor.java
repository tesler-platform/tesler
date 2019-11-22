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

import io.tesler.api.task.decorators.ClassLoaderAwareCallable;
import io.tesler.api.task.decorators.ClassLoaderAwareRunnable;
import io.tesler.api.task.decorators.SubjectAwareCallable;
import io.tesler.api.task.decorators.SubjectAwareRunnable;
import io.tesler.api.task.executors.DelegatingSchedulingTaskExecutor;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;


public class SecurityContextAwareSchedulingTaskExecutor
		extends DelegatingSchedulingTaskExecutor<SchedulingTaskExecutor> {

	public SecurityContextAwareSchedulingTaskExecutor(SchedulingTaskExecutor delegate) {
		super(delegate);
	}

	@Override
	@SneakyThrows
	protected <E> Callable<E> wrap(Callable<E> task) {
		Callable<E> contextAware = DelegatingSecurityContextCallable.create(task, null);
		contextAware = SubjectAwareCallable.wrap(contextAware);
		contextAware = ClassLoaderAwareCallable.wrap(contextAware);
		return contextAware;
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
