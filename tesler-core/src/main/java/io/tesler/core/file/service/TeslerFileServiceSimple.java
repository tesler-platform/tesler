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

package io.tesler.core.file.service;

import io.tesler.core.file.dto.FileDownloadDto;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
@ConditionalOnMissingBean(TeslerFileService.class)
public class TeslerFileServiceSimple implements TeslerFileService {

	public static final String UNIQUE_PREFIX_SEPARATOR = ".";

	private final Path fileFolder;

	@SneakyThrows
	public TeslerFileServiceSimple(String fileFolder) {
		if (StringUtils.isEmpty(fileFolder)) {
			this.fileFolder = Files.createTempDirectory("files");
		} else {
			this.fileFolder = Paths.get(fileFolder).toAbsolutePath().normalize();
			Files.createDirectories(this.fileFolder);
		}
	}

	@Override
	@SneakyThrows
	public String upload(@NonNull FileDownloadDto file, @Nullable String source) {
		String id = UUID.randomUUID().toString().replaceAll("-", "")
				+ UNIQUE_PREFIX_SEPARATOR
				+ file.getName();
		FileCopyUtils.copy(file.getBytes(), Files.newOutputStream(getPathFromId(id)));
		return id;
	}

	@Override
	@SneakyThrows
	public FileDownloadDto download(@NonNull String id, @Nullable String source) {
		Path path = getPathFromId(id);
		return new FileDownloadDto(
				Files.readAllBytes(path),
				path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(UNIQUE_PREFIX_SEPARATOR)),
				Files.probeContentType(path)
		);
	}

	@Override
	@SneakyThrows
	public void remove(@NonNull String id, @Nullable String source) {
		Path path = getPathFromId(id);
		Files.deleteIfExists(path);
	}

	@NonNull
	private Path getPathFromId(@NonNull String id) {
		Path fileId = Paths.get("/" + id).normalize();
		return Paths.get(this.fileFolder + "/" + fileId);
	}

}
