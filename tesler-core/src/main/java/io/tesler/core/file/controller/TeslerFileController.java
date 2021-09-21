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

package io.tesler.core.file.controller;

import io.tesler.core.file.dto.TeslerResponseDTO;
import io.tesler.core.file.dto.FileUploadDto;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Add
 * <pre>{@code
 * @RestController
 * @RequestMapping(TESLER_API_PATH_SPEL + "/file")
 * }</pre>
 * on implementation class
 * */
public interface TeslerFileController {

	/**
	 * Add
	 * <pre>{@code
	 * @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	 * }</pre>
	 * on implementation class.
	 * You can extend api for /file endpoint (declaring method with another signature), so we are not placing @PostMapping on interface to avoid endpoints clash
	 * */
	TeslerResponseDTO<? extends FileUploadDto> upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "source", required = false) String source);


	/**
	 * Add
	 * <pre>{@code
	 * @GetMapping
	 * }</pre>
	 * on implementation class.
	 * You can extend api for /file endpoint (declaring method with another signature), so we are not placing @PostMapping on interface to avoid endpoints clash.
	 * <pre></pre>
	 * See buildFileHttpEntity example of constructing response for this method
	 * */
	HttpEntity<byte[]> download(
			@RequestParam("id") String id,
			@RequestParam(value = "source", required = false) String source,
			@RequestParam(value = "preview", required = false, defaultValue = "false") boolean preview);

	/**
	 * Add
	 * <pre>{@code
	 * @DeleteMapping
	 * }</pre>
	 * on implementation class.
	 * You can extend api for /file endpoint (declaring method with another signature), so we are not placing @PostMapping on interface to avoid endpoints clash
	 * */
	TeslerResponseDTO<Void> remove(
			@RequestParam("id") String id,
			@RequestParam("source") String source);


	default HttpEntity<byte[]> buildFileHttpEntity(byte[] content, String fileName, String fileType, boolean inline) {
		HttpHeaders header = new HttpHeaders();
		header.set(
				HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition.builder(inline ? "inline" : "attachment")
						.filename(fileName, StandardCharsets.UTF_8)
						.build()
						.toString()
		);
		header.setContentType(getMediaType(fileType));
		header.setContentLength(content.length);
		return new HttpEntity<>(content, header);
	}

	default MediaType getMediaType(final String type) {
		try {
			return MediaType.parseMediaType(type);
		} catch (InvalidMediaTypeException e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

}
