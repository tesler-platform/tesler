/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service.action;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.PostAction;
import io.tesler.core.exception.BusinessException;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.vanilla.dto.VanillaTaskDTO;
import io.tesler.vanilla.entity.VanillaTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VanillaTaskActionDownloadFile {

	private final JpaDao jpaDao;

	public boolean fileExists(BusinessComponent bc) {
		if (bc.getIdAsLong() == null) {
			return false;
		}
		VanillaTask task = jpaDao.findById(VanillaTask.class, bc.getIdAsLong());
		return task != null && task.getFileEntity() != null;
	}

	public ActionResultDTO<VanillaTaskDTO> downloadFile(final BusinessComponent bc, final VanillaTaskDTO data) {
		if (data.getFileId() == null) {
			throw new BusinessException().addPopup(errorMessage("error.file_not_found"));
		}
		return new ActionResultDTO<>(data)
				.setAction(PostAction.downloadFile(data.getFileId()));
	}

}
