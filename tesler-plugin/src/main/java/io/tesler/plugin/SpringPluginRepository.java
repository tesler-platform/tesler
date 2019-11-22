/*-
 * #%L
 * IO Tesler - Plugin
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

package io.tesler.plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.BasePluginRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;


@Slf4j
public class SpringPluginRepository extends BasePluginRepository {

	private final SpringPluginManager pluginManager;

	public SpringPluginRepository(SpringPluginManager pluginManager) {
		super(null);
		this.pluginManager = pluginManager;
	}

	@SneakyThrows
	@Override
	public List<Path> getPluginPaths() {
		ApplicationContext context = pluginManager.getApplicationContext();
		return Arrays.stream(context.getResources(pluginManager.getLocation()))
				.map(this::createPath)
				.collect(Collectors.toList());
	}

	@SneakyThrows
	private Path createPath(Resource resource) {
		try {
			return resource.getFile().toPath();
		} catch (IOException ex) {
			log.info("Failed to match file to resource {}, creating temporary file", resource.getURL());
		}

		boolean success = false;
		File file = null;
		try {
			file = Files.createTempFile("plugin", ".jar").toFile();
			file.deleteOnExit();
			try (
					InputStream is = resource.getInputStream();
					OutputStream os = new BufferedOutputStream(
							new FileOutputStream(file)
					)
			) {
				StreamUtils.copy(is, os);
				success = true;
				return file.toPath();
			}
		} finally {
			if (!success && file != null) {
				file.delete();
			}
		}
	}

}
