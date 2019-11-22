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

package io.tesler.core.service.impl;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.api.data.dictionary.CoreDictionaries.FileStorage;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.exception.ClientException;
import io.tesler.core.service.FileService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.FileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class BaseFileService implements FileService {

	protected final JpaDao jpaDao;

	@Override
	public FileEntity save(String name, String type, boolean temporary, byte[] content) {
		return save(name, type, temporary, content, FileStorage.DB);
	}

	@Override
	public FileEntity save(String name, String type, boolean temporary, byte[] content, LOV storageType) {
		FileEntity entity = new FileEntity();
		entity.setFileName(name);
		entity.setFileType(type);
		entity.setFileContent(content);
		entity.setSize((long) content.length);
		entity.setFileStorageCd(storageType);
		entity.setTemporary(temporary);
		jpaDao.save(entity);
		return entity;
	}

	@Override
	public byte[] getContent(final FileEntity entity) {
		return entity.getFileContent();
	}

	@Override
	public void remove(final Long fileId) {
		FileEntity entity = getFileEntity(fileId);
		jpaDao.delete(entity);
	}

	private FileEntity getFileEntity(final Long fileId) {
		FileEntity fileEntity = jpaDao.findById(FileEntity.class, fileId);
		if (fileEntity == null) {
			throw new ClientException(errorMessage("error.file_not_found"));
		}
		return fileEntity;
	}

}
