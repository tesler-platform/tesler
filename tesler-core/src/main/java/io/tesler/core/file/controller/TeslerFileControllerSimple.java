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

import static io.tesler.core.config.properties.APIProperties.TESLER_API_PATH_SPEL;

import io.tesler.core.file.dto.TeslerResponseDTO;
import io.tesler.core.file.dto.FileDownloadDto;
import io.tesler.core.file.dto.FileUploadDto;
import io.tesler.core.file.service.TeslerFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TESLER_API_PATH_SPEL + "/file")
@ConditionalOnMissingBean(TeslerFileController.class)
public class TeslerFileControllerSimple implements TeslerFileController {

	private final TeslerFileService teslerFileService;

	@Override
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public TeslerResponseDTO<FileUploadDto> upload(MultipartFile file, String source) {
		String id = teslerFileService.upload(file, source);
		return new TeslerResponseDTO<FileUploadDto>()
				.setData(new FileUploadDto(id, file.getOriginalFilename(), file.getContentType()));
	}

	@Override
	@GetMapping
	public HttpEntity<byte[]> download(String id, String source, boolean preview) {
		FileDownloadDto file = teslerFileService.download(id, source);
		return buildFileHttpEntity(file.getBytes(), file.getName(), file.getType(), preview);
	}

	@Override
	@DeleteMapping
	public TeslerResponseDTO<Void> remove(String id, String source) {
		teslerFileService.remove(id, source);
		return new TeslerResponseDTO<>();
	}

}
