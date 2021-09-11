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

import io.tesler.core.service.FileService;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.TeslerFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class TeslerSimpleFileService<T extends BaseEntity & TeslerFile> implements FileService<T> {

	private final JpaDao jpaDao;

	private final Class<T> clazz;

	@Override
	@SneakyThrows
	public T save(String name, String type, boolean temporary, byte[] content) {
		T entity = clazz.newInstance();
		entity.setFileName(name);
		entity.setFileType(type);
		entity.setFileContent(content);
		entity.setTemporary(temporary);
		jpaDao.save(entity);
		return entity;
	}

	@Override
	public void remove(final Long fileId) {
		T entity = getFileEntityChecked(fileId);
		jpaDao.delete(entity);
	}

	@Override
	public T getFileEntity(final Long fileId) {
		return jpaDao.findById(clazz, fileId);
	}

}
