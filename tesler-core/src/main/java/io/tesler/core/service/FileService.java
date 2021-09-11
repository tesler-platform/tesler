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

package io.tesler.core.service;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.exception.ClientException;
import io.tesler.model.core.entity.TeslerFile;


public interface FileService<T extends TeslerFile> {

	T save(String name, String type, boolean temporary, byte[] content);

	default T saveUpload(String name, String type, boolean temporary, byte[] content) {
		return save(name, type, temporary, content);
	}

	default byte[] getContent(final T entity) {
		return entity.getFileContent();
	}

	T getFileEntity(Long fileId);

	default T getFileEntityChecked(final Long fileId) {
		T fileEntity = getFileEntity(fileId);
		if (fileEntity == null) {
			throw new ClientException(errorMessage("error.file_not_found"));
		}
		return fileEntity;
	}

	void remove(Long fileId);

}
