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
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;


public class DelegatingAsyncListenableTaskExecutor<T extends AsyncListenableTaskExecutor>
		extends DelegatingAsyncTaskExecutor<T> implements AsyncListenableTaskExecutor {

	public DelegatingAsyncListenableTaskExecutor(T delegate) {
		super(delegate);
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		return getDelegate().submitListenable(wrap(task));
	}

	@Override
	public <E> ListenableFuture<E> submitListenable(Callable<E> task) {
		return getDelegate().submitListenable(wrap(task));
	}

}
