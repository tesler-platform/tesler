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

package io.tesler.api.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


public final class ZipBuilder {

	private final List<File> files = new ArrayList<>();

	public ZipBuilder add(final String name, final String content) {
		if (content == null) {
			throw new IllegalArgumentException("Content is null");
		}
		files.add(new File(name, content.getBytes(StandardCharsets.UTF_8)));
		return this;
	}

	public ZipBuilder add(final String name, final byte[] content) {
		if (content == null) {
			throw new IllegalArgumentException("Content is null");
		}
		files.add(new File(name, content));
		return this;
	}

	@SneakyThrows
	public byte[] toByteArray() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (final ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (final File file : files) {
				zos.putNextEntry(new ZipEntry(file.getName()));
				zos.write(file.getContent());
				zos.closeEntry();
			}
		}
		return baos.toByteArray();
	}

	@Getter
	@RequiredArgsConstructor
	private static final class File {

		private final String name;

		private final byte[] content;

	}

}
