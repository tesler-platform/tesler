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

package io.tesler.api.task.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.springframework.core.task.AsyncTaskExecutor;


public class DelegatingAsyncTaskExecutor<T extends AsyncTaskExecutor> extends DelegatingTaskExecutor<T>
		implements AsyncTaskExecutor {

	public DelegatingAsyncTaskExecutor(T delegate) {
		super(delegate);
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		getDelegate().execute(wrap(task), startTimeout);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return getDelegate().submit(wrap(task));
	}

	@Override
	public <E> Future<E> submit(Callable<E> task) {
		return getDelegate().submit(wrap(task));
	}

	protected <E> Callable<E> wrap(Callable<E> task) {
		return task;
	}

}
