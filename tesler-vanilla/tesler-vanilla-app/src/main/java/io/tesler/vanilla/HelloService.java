/*-
 * #%L
 * IO Tesler - Vanilla APP
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

package io.tesler.vanilla;

import io.tesler.api.service.PluginAware;
import io.tesler.vanilla.plugin.helloworld.api.IHello;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@PluginAware
@RequiredArgsConstructor
public class HelloService {

	private final IHello hello;

	@PostConstruct
	public void init() {
		hello.sayHello();
	}

}
