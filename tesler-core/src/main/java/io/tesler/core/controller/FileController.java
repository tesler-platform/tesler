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

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static io.tesler.core.config.properties.APIProperties.TESLER_API_PATH_SPEL;

import io.tesler.api.exception.ServerException;
import io.tesler.core.dto.TeslerResponseDTO;
import io.tesler.core.dto.data.FileUploadDto;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.FileService;
import io.tesler.model.core.entity.TeslerFile;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
//@RestController
@RequestMapping(TESLER_API_PATH_SPEL + "/file")
@ConditionalOnMissingBean(TeslerFileControllerContract.class)
public class FileController implements TeslerFileControllerContract {

	private final FileService fileService;

	@Override
	@PostMapping
	public TeslerResponseDTO<FileUploadDto> upload(MultipartFile file, String source) {
		try {
			return new TeslerResponseDTO<FileUploadDto>().setData(doUpload(file, source));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ClientException(errorMessage("error.failed_to_upload_file", file.getName()));
		}
	}

	@Override
	@GetMapping
	public HttpEntity<byte[]> download(String id, String source, boolean preview) {
		try {
			return doDownload(Long.parseLong(id), source, preview);
		} catch (ClientException e) {
			throw e;
		} catch (Exception e) {
			throw new ServerException("Не удалось скачать файл.", e);
		}
	}

	@Override
	@DeleteMapping
	public TeslerResponseDTO<Void> remove(String id, String source) {
		fileService.remove(Long.parseLong(id));
		return new TeslerResponseDTO<>();
	}

	private FileUploadDto doUpload(MultipartFile file, String source) throws IOException {
		TeslerFile fileEntity = fileService.saveUpload(
				file.getOriginalFilename(),
				file.getContentType(),
				false,
				file.getBytes()
		);
		return new FileUploadDto(fileEntity);
	}

	private HttpEntity<byte[]> doDownload(Long id, String source, boolean inline) {
		TeslerFile file = fileService.getFileEntityChecked(id);
		byte[] content = fileService.getContent(file);
		String fileType = file.getFileType();
		String fileName = file.getFileName();
		return buildFileHttpEntity(content, fileName, fileType, inline);
	}
}
