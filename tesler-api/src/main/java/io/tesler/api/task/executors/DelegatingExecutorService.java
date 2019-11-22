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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DelegatingExecutorService<T extends ExecutorService> extends DelegatingExecutor<T>
		implements ExecutorService {

	public DelegatingExecutorService(T delegate) {
		super(delegate);
	}

	@Override
	public void shutdown() {
		getDelegate().shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return getDelegate().shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return getDelegate().isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return getDelegate().isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return getDelegate().awaitTermination(timeout, unit);
	}

	@Override
	public <E> Future<E> submit(Callable<E> task) {
		return getDelegate().submit(wrap(task));
	}

	@Override
	public <E> Future<E> submit(Runnable task, E result) {
		return getDelegate().submit(wrap(task), result);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return getDelegate().submit(wrap(task));
	}

	@Override
	public <E> List<Future<E>> invokeAll(Collection<? extends Callable<E>> tasks) throws InterruptedException {
		return getDelegate().invokeAll(wrap(tasks));
	}

	@Override
	public <E> List<Future<E>> invokeAll(Collection<? extends Callable<E>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return getDelegate().invokeAll(wrap(tasks), timeout, unit);
	}

	@Override
	public <E> E invokeAny(Collection<? extends Callable<E>> tasks) throws InterruptedException, ExecutionException {
		return getDelegate().invokeAny(wrap(tasks));
	}

	@Override
	public <E> E invokeAny(Collection<? extends Callable<E>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return getDelegate().invokeAny(wrap(tasks), timeout, unit);
	}

	protected <E> Callable<E> wrap(Callable<E> task) {
		return task;
	}

	protected <E> Collection<Callable<E>> wrap(Collection<? extends Callable<E>> tasks) {
		if (tasks == null) {
			return null;
		}
		List<Callable<E>> results = new ArrayList<>(tasks.size());
		for (Callable<E> task : tasks) {
			results.add(wrap(task));
		}
		return results;
	}

}
