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

import java.util.Objects;
import java.util.concurrent.Executor;


public class DelegatingExecutor<T extends Executor> implements Executor {

	private final T delegate;

	public DelegatingExecutor(T delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public T getDelegate() {
		return delegate;
	}

	@Override
	public void execute(Runnable command) {
		getDelegate().execute(wrap(command));
	}

	protected Runnable wrap(Runnable command) {
		return command;
	}

}
