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

import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.finish.FileFinishAction;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import io.tesler.core.dto.data.FileUploadDto;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.AVService;
import io.tesler.core.service.FileService;
import io.tesler.core.service.file.CustomSourceFile;
import io.tesler.core.service.file.CustomSourceFileService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.FileEntity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(value = "file")
public class FileController {

	@Autowired
	private FileService fileService;

	@Autowired
	private CustomSourceFileService customSourceFileService;

	@Autowired
	private Optional<AVService> avService;

	@Autowired
	private ResponseBuilder resp;

	@Autowired
	private JpaDao jpaDao;

	@Autowired(required = false)
	private FileFinishAction fileFinishAction;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseDTO upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "source", required = false) String source) {
		try {
			return processFinishAction(resp.build(doUpload(file, source)));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ClientException(errorMessage("error.failed_to_upload_file", file.getName()));
		}
	}

	private FileUploadDto doUpload(MultipartFile file, String source) throws IOException {
		byte[] content = file.getBytes();
		avService.ifPresent(s -> s.requireClean(content, file.getOriginalFilename()));
		if (source != null) {
			return customSourceUpload(file, source);
		}
		FileEntity fileEntity = fileService.saveUpload(
				file.getOriginalFilename(),
				file.getContentType(),
				false,
				content
		);
		return new FileUploadDto(fileEntity);
	}

	private FileUploadDto customSourceUpload(MultipartFile file, String source) {
		Long id = customSourceFileService.saveFileToSource(source, file);
		CustomSourceFile fileFromSource = customSourceFileService.getFileFromSource(source, id);
		return new FileUploadDto(
				fileFromSource.getId().toString(),
				fileFromSource.getName(),
				fileFromSource.getType()
		);
	}

	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<byte[]> download(
			@RequestParam("id") Long id,
			@RequestParam(value = "source", required = false) String source,
			@RequestParam(value = "preview", required = false, defaultValue = "false") boolean preview) {
		try {
			return doDownload(id, source, preview);
		} catch (ClientException e) {
			throw e;
		} catch (Exception e) {
			throw new ServerException("Не удалось скачать файл.", e);
		}
	}

	private HttpEntity<byte[]> doDownload(Long id, String source, boolean inline) {
		if (source != null) {
			return customSourceDownload(id, source, inline);
		}
		FileEntity file = jpaDao.findById(FileEntity.class, id);
		if (file == null) {
			throw new ClientException(errorMessage("error.file_not_found"));
		}
		byte[] content = fileService.getContent(file);
		String fileType = file.getFileType();
		String fileName = file.getFileName();
		return buildHttpEntity(content, fileName, fileType, inline);
	}

	private HttpEntity<byte[]> customSourceDownload(Long id, String source, boolean inline) {
		CustomSourceFile fileFromSource = customSourceFileService.getFileFromSource(source, id);
		return buildHttpEntity(fileFromSource.getContent(), fileFromSource.getName(), fileFromSource.getType(), inline);
	}

	private HttpEntity<byte[]> buildHttpEntity(byte[] content, String fileName, String fileType, boolean inline) {
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
		processFinishAction(resp.build("Download file. Name: " + fileName + ", type: " + fileType));
		return new HttpEntity<>(content, header);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseDTO remove(
			@RequestParam("id") Long id,
			@RequestParam("source") String source) {
		fileService.remove(id);
		processFinishAction(resp.build("Remove file with id: " + id));
		return resp.build(new ArrayList<>());
	}

	private MediaType getMediaType(final String type) {
		try {
			return MediaType.parseMediaType(type);
		} catch (InvalidMediaTypeException e) {
			log.debug("Invalid media type", e);
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	protected ResponseDTO processFinishAction(ResponseDTO result) {
		if (fileFinishAction != null) {
			fileFinishAction.invoke(result);
		}
		return result;
	}

}
