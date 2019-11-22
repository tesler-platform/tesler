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

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;


public class DelegatingTaskScheduler<T extends TaskScheduler & TaskExecutor> extends DelegatingExecutor<T>
		implements TaskScheduler {

	public DelegatingTaskScheduler(T delegate) {
		super(delegate);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
		return getDelegate().schedule(wrap(task), trigger);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
		return getDelegate().schedule(wrap(task), startTime);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
		return getDelegate().scheduleAtFixedRate(wrap(task), startTime, period);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
		return getDelegate().scheduleAtFixedRate(wrap(task), period);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
		return getDelegate().scheduleWithFixedDelay(wrap(task), startTime, delay);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
		return getDelegate().scheduleWithFixedDelay(wrap(task), delay);
	}

}
