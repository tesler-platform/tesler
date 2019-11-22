/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.service;

import io.tesler.api.service.AsyncService;
import io.tesler.api.util.Invoker;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service(AsyncService.SERVICE_NAME)
public class AsyncServiceImpl implements AsyncService {

	@Async
	public <T, E extends Throwable> CompletableFuture<T> invokeAsync(Invoker<T, E> invoker) throws E {
		return CompletableFuture.completedFuture(invoker.invoke());
	}

}
