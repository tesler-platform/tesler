/*-
 * #%L
 * IO Tesler - Core
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

package io.tesler.core.controller;

import io.tesler.core.metahotreload.service.MetaHotReloadService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.tesler.core.config.CacheConfig;

@RestController
@RequestMapping("bc-registry")
@RequiredArgsConstructor
public class MetaHotReloadController {

	final MetaHotReloadService metaHotReloadService;

	final TransactionService txService;

	final CacheManager cacheManager;

	@GetMapping("refresh-meta")
	public void refresh() {
		metaHotReloadService.loadMeta();
		txService.invokeAfterCompletion(Invoker.of(
				() -> {
					cacheManager.getCache(CacheConfig.UI_CACHE).clear();
					cacheManager.getCache(CacheConfig.REQUEST_CACHE).clear();
					cacheManager.getCache(CacheConfig.USER_CACHE).clear();
				}
		));
	}

}
